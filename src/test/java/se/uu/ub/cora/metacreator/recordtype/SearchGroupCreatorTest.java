package se.uu.ub.cora.metacreator.recordtype;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;

public class SearchGroupCreatorTest {

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
	public void testCreateSearchGroup() {

		SearchGroupCreator searchGroupCreator = SearchGroupCreator
				.withIdIdAndDataDividerAndRecordType("myRecordTypeSearch", "cora", "myRecordType");

		DataGroup searchGroup = searchGroupCreator.createGroup("");
		DataGroup recordInfo = searchGroup.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "myRecordTypeSearch");

		assertCorrectDataDivider(recordInfo);

		assertFalse(searchGroup.containsChildWithNameInData("childReferences"));

		assertCorrectRecordTypeToSearchIn(searchGroup);

		assertCorrectMetadataId(searchGroup);

		assertCorrectPresentationId(searchGroup);

		assertEquals(searchGroup.getFirstAtomicValueWithNameInData("searchGroup"), "autocomplete");

		assertCorrectTexts(searchGroup);

	}

	private void assertCorrectDataDivider(DataGroup recordInfo) {
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "cora");
	}

	private void assertCorrectRecordTypeToSearchIn(DataGroup searchGroup) {
		DataGroup recordTypeToSearchIn = searchGroup
				.getFirstGroupWithNameInData("recordTypeToSearchIn");
		assertEquals(recordTypeToSearchIn.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"myRecordType");
		assertEquals(recordTypeToSearchIn.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"recordType");
		assertNotNull(recordTypeToSearchIn.getRepeatId());
	}

	private void assertCorrectMetadataId(DataGroup searchGroup) {
		DataGroup metadataId = searchGroup.getFirstGroupWithNameInData("metadataId");
		assertEquals(metadataId.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"autocompleteSearchGroup");
	}

	private void assertCorrectPresentationId(DataGroup searchGroup) {
		DataGroup presentationId = searchGroup.getFirstGroupWithNameInData("presentationId");
		assertEquals(presentationId.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"autocompleteSearchPGroup");
	}

	private void assertCorrectTexts(DataGroup searchGroup) {
		DataGroup textIdGroup = searchGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"myRecordTypeSearchText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");

		DataGroup defTextIdGroup = searchGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"myRecordTypeSearchDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"coraText");
	}
}
