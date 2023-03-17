package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class GroupFactoryImp implements GroupFactory {

	@Override
	public DataRecordGroup factorMetadataGroup(String dataDivider, String id, String nameInData,
			String childRefRecordInfoId, boolean excludePGroupCreation) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupUsingNameInData("metadata");
		setBasicRecordGroupInfo(recordGroup, id, dataDivider, "metadataGroup");
		addChildReferences(recordGroup, childRefRecordInfoId);
		addNameInData(recordGroup, nameInData);
		possiblyAddExcludePGroupCreation(recordGroup, excludePGroupCreation);
		possiblyAddAttributeType(recordGroup, "group");
		return recordGroup;
	}

	private void setBasicRecordGroupInfo(DataRecordGroup recordGroup, String id, String dataDivider,
			String validationType) {
		recordGroup.setId(id);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType(validationType);
	}

	private void addChildReferences(DataRecordGroup recordGroup, String childRefRecordInfoId) {
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

	private void addNameInData(DataRecordGroup recordGroup, String nameInData) {
		DataAtomic nameInDataAtomic = DataProvider.createAtomicUsingNameInDataAndValue("nameInData",
				nameInData);
		recordGroup.addChild(nameInDataAtomic);
	}

	private void possiblyAddExcludePGroupCreation(DataRecordGroup recordGroup,
			boolean excludePGroupCreation) {
		if (excludePGroupCreation) {
			DataAtomic excludePGroupCreationAtomic = DataProvider
					.createAtomicUsingNameInDataAndValue("excludePGroupCreation", "true");
			recordGroup.addChild(excludePGroupCreationAtomic);
		}
	}

	private void possiblyAddAttributeType(DataRecordGroup recordGroup, String attributeType) {
		recordGroup.addAttributeByIdWithValue("type", attributeType);
	}

	// SPIKE
	// @Override
	// public DataRecordGroup factorSearchGroup(String dataDivider, String id, String nameInData,
	// String validationType, String childRefRecordInfoId, Optional<String> attributeType) {
	// DataRecordGroup recordGroup = DataProvider.createRecordGroupUsingNameInData("search");
	// setBasicRecordGroupInfo(recordGroup, id, dataDivider, validationType);
	// otherStuff...
	// return recordGroup;
	// }

}
