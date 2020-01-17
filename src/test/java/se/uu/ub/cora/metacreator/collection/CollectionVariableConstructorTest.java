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

public class CollectionVariableConstructorTest {

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
	public void testConstructCollectionVar() {

		CollectionVariableConstructor constructor = new CollectionVariableConstructor();
		DataGroup collectionVar = constructor
				.constructCollectionVarWithIdNameInDataDataDividerAndRefCollection(
						"someCollectionVar", "someNameInData", "testSystem", "someCollection");
		assertEquals(collectionVar.getFirstAtomicValueWithNameInData("nameInData"),
				"someNameInData");

		assertCorrectRefCollection(collectionVar);
		assertCorrectRecordInfo(collectionVar);

	}

	private void assertCorrectRecordInfo(DataGroup record) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "someCollectionVar");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "testSystem");
	}

	private void assertCorrectRefCollection(DataGroup record) {
		DataRecordLink refCollection = (DataRecordLink) record
				.getFirstGroupWithNameInData("refCollection");
		assertEquals(refCollection.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataItemCollection");
		assertEquals(refCollection.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someCollection");
	}

}
