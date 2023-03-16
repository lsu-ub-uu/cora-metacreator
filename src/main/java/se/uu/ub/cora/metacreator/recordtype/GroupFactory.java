package se.uu.ub.cora.metacreator.recordtype;

import java.util.Optional;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class GroupFactory {
	protected String dataDivider;
	protected DataGroup topLevelDataGroup;
	private DataRecordGroup recordGroup;
	private String validationType;

	private GroupFactory(String dataDivider, String validationType) {
		this.dataDivider = dataDivider;
		this.validationType = validationType;
	}

	public static GroupFactory withDataDividerAndValidationType(String dataDivider,
			String validationType) {
		return new GroupFactory(dataDivider, validationType);
	}

	public DataRecordGroup factorDataGroup(String id, String nameInData,
			String childRefRecordInfoId, Optional<String> attributeType) {
		recordGroup = DataProvider.createRecordGroupUsingNameInData(nameInData);
		setBasicRecordGroupInfo(id);
		addChildReferences(childRefRecordInfoId);
		possiblyAddAttributeType(attributeType);
		return recordGroup;
	}

	private void possiblyAddAttributeType(Optional<String> attributeType) {
		if (attributeType.isPresent()) {
			recordGroup.addAttributeByIdWithValue("type", attributeType.get());
		}
	}

	private void setBasicRecordGroupInfo(String id) {
		recordGroup.setId(id);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType(validationType);
	}

	private void addChildReferences(String childRefRecordInfoId) {
		DataRecordLink ref = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId("ref",
				"metadata", childRefRecordInfoId);

		DataAtomic repeatMin = DataProvider.createAtomicUsingNameInDataAndValue("repeatMin", "1");
		DataAtomic repeatMax = DataProvider.createAtomicUsingNameInDataAndValue("repeatMax", "1");
		DataGroup childReference = DataProvider.createGroupUsingNameInData("childReference");

		DataGroup childReferences = DataProvider.createGroupUsingNameInData("childReferences");

		childReference.addChild(ref);
		childReference.addChild(repeatMin);
		childReference.addChild(repeatMax);
		childReference.setRepeatId("0");
		childReferences.addChild(childReference);
		recordGroup.addChild(childReferences);
	}

}
