package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.text.TextFactory;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class RecordCreatorHelper {

	private final String authToken;
	private DataGroup dataGroup;
	private String implementingTextType;

	private RecordCreatorHelper(String authToken, DataGroup dataGroup,
			String implementingTextType) {
		this.authToken = authToken;
		this.dataGroup = dataGroup;
		this.implementingTextType = implementingTextType;
	}

	public static RecordCreatorHelper withAuthTokenDataGroupAndImplementingTextType(
			String authToken, DataGroup dataGroup, String implementingTextType) {
		return new RecordCreatorHelper(authToken, dataGroup, implementingTextType);
	}

	public void createTextsIfMissing() {
		createTextWithTextIdToExtractIfMissing("textId");
		createTextWithTextIdToExtractIfMissing("defTextId");
	}

	private void createTextWithTextIdToExtractIfMissing(String textIdToExtract) {
		DataGroup textIdGroup = this.dataGroup.getFirstGroupWithNameInData(textIdToExtract);
		String textId = textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
		if (textIsMissing(textId)) {
			createTextWithTextId(textId);
		}
	}

	private boolean textIsMissing(String textId) {
		try {
			RecordReader recordReader = SpiderInstanceProvider.getRecordReader();
			recordReader.readRecord(authToken, implementingTextType, textId);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void createTextWithTextId(String textId) {
		String dataDivider = DataCreatorHelperImp.extractDataDividerIdFromDataGroup(dataGroup);
		createTextInStorageWithTextIdDataDividerAndTextType(textId, dataDivider,
				implementingTextType);
	}

	private void createTextInStorageWithTextIdDataDividerAndTextType(String textId,
			String dataDivider, String implementingTextType) {
		TextFactory textConstructor = TextFactoryImp.usingDataCreatorHelper(dataCreatorHelper);
		DataGroup textGroup = textConstructor.createTextUsingTextIdAndDataDividerId("someTextId",
				"someDataDivider");

		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		recordCreator.createAndStoreRecord(authToken, implementingTextType, textGroup);
	}
}
