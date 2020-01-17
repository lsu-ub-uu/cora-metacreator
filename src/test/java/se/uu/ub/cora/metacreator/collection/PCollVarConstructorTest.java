package se.uu.ub.cora.metacreator.collection;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;

public class PCollVarConstructorTest {

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
	}

	@Test
	public void testPCollVarConstructor() {
		PCollVarConstructor constructor = new PCollVarConstructor();
		DataGroup pCollVar = constructor.constructPCollVarWithIdDataDividerPresentationOfAndMode(
				"somePCollVar", "testSystem", "someCollectionVar", "input");

		assertCorrectRecordInfo(pCollVar);
		assertCorrectPresentationOf(pCollVar);

		assertEquals(pCollVar.getNameInData(), "presentation");
		assertEquals(pCollVar.getFirstAtomicValueWithNameInData("mode"), "input");
		DataRecordLink emptyValue = (DataRecordLink) pCollVar
				.getFirstGroupWithNameInData("emptyTextId");
		assertEquals(emptyValue.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		assertEquals(emptyValue.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"initialEmptyValueText");
		assertEquals(pCollVar.getAttributes().get("type"), "pCollVar");

	}

	private void assertCorrectRecordInfo(DataGroup pCollVar) {
		DataGroup recordInfo = pCollVar.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "somePCollVar");
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "testSystem");
	}

	private void assertCorrectPresentationOf(DataGroup pCollVar) {
		DataRecordLink presentationOf = (DataRecordLink) pCollVar
				.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataCollectionVariable");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someCollectionVar");
	}
}
