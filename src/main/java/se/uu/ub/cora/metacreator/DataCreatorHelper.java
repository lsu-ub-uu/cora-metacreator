package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;

public final class DataCreatorHelper {

	private static final String RECORD_INFO = "recordInfo";

	private DataCreatorHelper() {
		// not called
		throw new UnsupportedOperationException();
	}

	public static String extractDataDividerStringFromDataGroup(DataGroup topLevelGroup) {
		DataGroup dataDividerGroup = extractDataDividerGroupFromDataGroup(topLevelGroup);
		return dataDividerGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private static DataGroup extractDataDividerGroupFromDataGroup(DataGroup topLevelGroup) {
		DataGroup recordInfoGroup = topLevelGroup.getFirstGroupWithNameInData(RECORD_INFO);
		return recordInfoGroup.getFirstGroupWithNameInData("dataDivider");
	}

	public static DataGroup createRecordInfoWithIdAndDataDivider(String id,
			String dataDividerLinkedRecordId) {
		DataGroup recordInfo = DataGroupProvider.getDataGroupUsingNameInData(RECORD_INFO);
		recordInfo.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("id", id));
		createAndAddDataDivider(dataDividerLinkedRecordId, recordInfo);

		return recordInfo;
	}

	private static void createAndAddDataDivider(String dataDividerLinkedRecordId,
			DataGroup recordInfo) {
		DataRecordLink dataDivider = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("dataDivider", "system",
						dataDividerLinkedRecordId);
		recordInfo.addChild(dataDivider);
	}

	public static String extractIdFromDataGroup(DataGroup mainDataGroup) {
		DataGroup recordInfo = mainDataGroup.getFirstGroupWithNameInData(RECORD_INFO);
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

}
