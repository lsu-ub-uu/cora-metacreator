package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;

public abstract class GroupCreator {
	protected String id;
	protected String dataDivider;
	protected DataGroup topLevelDataGroup;

	protected GroupCreator(String id, String dataDivider) {
		this.id = id;
		this.dataDivider = dataDivider;
	}

	public DataGroup createGroup(String refRecordInfoId) {
		topLevelDataGroup = createTopLevelDataGroup();

		createAndAddRecordInfoToDataGroup();

		addChildReferencesWithChildId(refRecordInfoId);
		addAttributeType();
		return topLevelDataGroup;
	}

	abstract DataGroup createTopLevelDataGroup();

	abstract void addAttributeType();

	protected void createAndAddRecordInfoToDataGroup() {
		DataGroup recordInfo = DataGroupProvider.getDataGroupUsingNameInData("recordInfo");
		recordInfo.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("id", id));

		DataRecordLink dataDividerGroup = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("dataDivider", "system",
						dataDivider);

		recordInfo.addChild(dataDividerGroup);
		topLevelDataGroup.addChild(recordInfo);
	}

	protected void addChildReferencesWithChildId(String refRecordInfoId) {
		DataGroup childReferences = DataGroupProvider
				.getDataGroupUsingNameInData("childReferences");
		DataGroup childReference = DataGroupProvider.getDataGroupUsingNameInData("childReference");

		DataRecordLink refGroup = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("ref", "metadataGroup",
						refRecordInfoId);
		childReference.addChild(refGroup);

		addValuesForChildReference(childReference);
		childReference.setRepeatId("0");
		childReferences.addChild(childReference);
		topLevelDataGroup.addChild(childReferences);
	}

	void addValuesForChildReference(DataGroup childReference) {
		childReference.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("repeatMin", "1"));
		childReference.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("repeatMax", "1"));
	}
}
