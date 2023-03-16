package se.uu.ub.cora.metacreator.recordtype;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;

public class MetadataGroupCreatorTest {

	private DataGroupFactorySpy dataGroupFactory;
	private DataAtomicFactorySpy dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;

	@Test
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);

	}

	@Test
	public void testCreateMetadataGroup() {
		MetadataGroupCreator creator = MetadataGroupCreator
				.withIdAndNameInDataAndDataDivider("myRecordTypeGroup", "myRecordType", "cora");
		DataGroup metadataGroup = creator.factorDataGroup("recordInfoGroup");

		DataGroup recordInfo = metadataGroup.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "myRecordTypeGroup");

		DataRecordLink dataDivider = (DataRecordLink) recordInfo
				.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getLinkedRecordType(), "system");
		assertEquals(dataDivider.getLinkedRecordId(), "cora");

		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("nameInData"), "myRecordType");
		DataRecordLink textIdGroup = (DataRecordLink) metadataGroup
				.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getLinkedRecordId(), "myRecordTypeGroupText");
		assertEquals(textIdGroup.getLinkedRecordType(), "coraText");
		DataRecordLink defTextIdGroup = (DataRecordLink) metadataGroup
				.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getLinkedRecordId(), "myRecordTypeGroupDefText");
		assertEquals(defTextIdGroup.getLinkedRecordType(), "coraText");

		assertCorrectChildReferences(metadataGroup);

		assertEquals(metadataGroup.getAttribute("type").getValue(), "group");
	}

	private void assertCorrectChildReferences(DataGroup metadataGroup) {
		DataGroup childRefs = metadataGroup.getFirstGroupWithNameInData("childReferences");
		assertEquals(childRefs.getChildren().size(), 1);

		DataGroup childRef = (DataGroup) childRefs.getFirstChildWithNameInData("childReference");
		DataRecordLink ref = (DataRecordLink) childRef.getFirstChildWithNameInData("ref");
		assertEquals(ref.getLinkedRecordId(), "recordInfoGroup");
		assertEquals(ref.getLinkedRecordType(), "metadataGroup");

		DataAtomic repeatMin = (DataAtomic) childRef.getFirstChildWithNameInData("repeatMin");
		assertEquals(repeatMin.getValue(), "1");

		DataAtomic repeatMax = (DataAtomic) childRef.getFirstChildWithNameInData("repeatMax");
		assertEquals(repeatMax.getValue(), "1");
	}
}
