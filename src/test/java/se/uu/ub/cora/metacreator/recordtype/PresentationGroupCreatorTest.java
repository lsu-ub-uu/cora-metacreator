package se.uu.ub.cora.metacreator.recordtype;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactoryOldSpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorOldSpy;
import se.uu.ub.cora.metacreator.spy.DataAtomicSpy;
import se.uu.ub.cora.metacreator.spy.DataGroupSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class PresentationGroupCreatorTest {

	private SpiderInstanceFactoryOldSpy instanceFactory;

	@BeforeMethod
	public void setUp() {
		instanceFactory = new SpiderInstanceFactoryOldSpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
	}

	@Test
	public void testCreatePresentationGroupWhenPGroupDoesNotExist() {
		PresentationGroupCreator pGroupCreator = createPresentationGroupCreatorWithIdModeAndChildId(
				"myRecordTypeViewId", "input", "searchTitleTextVar");
		pGroupCreator.createPGroupIfNotAlreadyExist();

		List<SpiderRecordCreatorOldSpy> spiderRecordCreators = instanceFactory.spiderRecordCreators;
		SpiderRecordCreatorOldSpy spiderRecordCreatorSpy = spiderRecordCreators.get(0);

		assertEquals(spiderRecordCreators.size(), 1);
		assertEquals(spiderRecordCreatorSpy.authToken, "testUser");

		DataGroup record = spiderRecordCreatorSpy.record;
		assertCorrectIdAndDataDivider(record);

		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), "input");
		DataGroup presentationOf = record.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"myRecordType");

		assertCorrectChildReferences(record);

	}

	private PresentationGroupCreator createPresentationGroupCreatorWithIdModeAndChildId(String id,
			String mode, String childId) {
		PresentationGroupCreator pGroupCreator = PresentationGroupCreator
				.withAuthTokenPresentationIdAndDataDivider("testUser", id, "cora");

		pGroupCreator.setPresentationOfAndMode("myRecordType", mode);
		List<DataGroup> metadataChildren = createMetadataChildReferences();
		pGroupCreator.setMetadataChildReferences(metadataChildren);
		return pGroupCreator;
	}

	private void assertCorrectIdAndDataDivider(DataGroup record) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "myRecordTypeViewId");
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "cora");
	}

	private void assertCorrectChildReferences(DataGroup record) {
		DataGroup childReferences = record.getFirstGroupWithNameInData("childReferences");
		assertEquals(childReferences.getChildren().size(), 2);
		DataGroup refText = extractRefFromChildReferenceByChildIndex(childReferences, 0);
		assertEquals(refText.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"searchTitleTextVarText");
		assertEquals(refText.getAttribute("type").getValue(), "text");
		DataGroup ref = extractRefFromChildReferenceByChildIndex(childReferences, 1);
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), "searchTitlePVar");
		assertEquals(ref.getAttribute("type").getValue(), "presentation");
	}

	private DataGroup extractRefFromChildReferenceByChildIndex(DataGroup childReferences,
			int index) {
		DataGroup childReference = childReferences.getAllGroupsWithNameInData("childReference")
				.get(index);
		DataGroup refGRoup = childReference.getFirstGroupWithNameInData("refGroup");
		return refGRoup.getFirstGroupWithNameInData("ref");
	}

	private List<DataGroup> createMetadataChildReferences() {
		List<DataGroup> metadataChildReferences = new ArrayList<>();
		DataGroup childReference = createChildReference();

		addRefPartToChildReference(childReference, "searchTitleTextVar");
		childReference.setRepeatId("0");
		metadataChildReferences.add(childReference);
		return metadataChildReferences;
	}

	private void addRefPartToChildReference(DataGroup childReference, String childId) {
		DataGroup ref = new DataGroupSpy("ref");
		ref.addChild(new DataAtomicSpy("linkedRecordType", "metadata"));
		ref.addChild(new DataAtomicSpy("linkedRecordId", childId));
		childReference.addChild(ref);
	}

	private DataGroup createChildReference() {
		DataGroup childReference = new DataGroupSpy("childReference");
		childReference.addChild(new DataAtomicSpy("repeatMin", "1"));
		childReference.addChild(new DataAtomicSpy("repeatMax", "1"));
		return childReference;
	}

	@Test
	public void testCreatePresentationGroupWhenPGroupAlreadyExist() {
		PresentationGroupCreator pGroupCreator = createPresentationGroupCreatorWithIdModeAndChildId(
				"someExistingPGroup", "input", "searchTitleTextVar");
		pGroupCreator.createPGroupIfNotAlreadyExist();
		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}

}
