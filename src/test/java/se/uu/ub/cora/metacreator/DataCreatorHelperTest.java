package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;

public class DataCreatorHelperTest {

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<DataCreatorHelper> constructor = DataCreatorHelper.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<DataCreatorHelper> constructor = DataCreatorHelper.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testCreateRecordInfo() {
		DataGroup recordInfo = DataCreatorHelper.createRecordInfoWithIdAndDataDivider("someId",
				"test");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "someId");
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	}

	@Test
	public void testExtractDataDivider() {
		DataGroup mainDataGroup = DataCreator.createTextVarGroupWithIdAndTextIdAndDefTextId(
				"someId", "someTextId", "someDefTextId");
		String dataDivider = DataCreatorHelper.extractDataDividerStringFromDataGroup(mainDataGroup);
		assertEquals(dataDivider, "cora");
	}

	@Test
	public void testExtractId() {
		DataGroup mainDataGroup = DataCreator.createTextVarGroupWithIdAndTextIdAndDefTextId(
				"someId", "someTextId", "someDefTextId");
		String id = DataCreatorHelper.extractIdFromDataGroup(mainDataGroup);
		assertEquals(id, "someId");
	}

}
