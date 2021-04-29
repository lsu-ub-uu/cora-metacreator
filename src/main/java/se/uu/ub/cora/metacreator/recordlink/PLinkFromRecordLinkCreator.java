package se.uu.ub.cora.metacreator.recordlink;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

public class PLinkFromRecordLinkCreator implements ExtendedFunctionality {

	private String authToken;
	private String presentationOf;
	private String dataDivider;
	private PLinkConstructor constructor;
	private String id;

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup recordLinkToCreateFrom) {
		this.authToken = authToken;

		constructor = new PLinkConstructor();
		setParametersForCreation(recordLinkToCreateFrom);

		possiblyCreateInputPLink();
		possiblyCreateOutputPLink();
	}

	private void setParametersForCreation(DataGroup recordLinkToCreateFrom) {
		id = DataCreatorHelper.extractIdFromDataGroup(recordLinkToCreateFrom);
		presentationOf = DataCreatorHelper.extractIdFromDataGroup(recordLinkToCreateFrom);
		dataDivider = DataCreatorHelper
				.extractDataDividerStringFromDataGroup(recordLinkToCreateFrom);
	}

	private void possiblyCreateInputPLink() {
		String pLinkId = constructIdForPLink();

		if (pLinkIsMissing(pLinkId)) {
			createPCollVarWithIdAndMode(pLinkId, "input");
		}
	}

	private boolean pLinkIsMissing(String pLinkId) {
		try {
			RecordReader reader = SpiderInstanceProvider.getRecordReader();
			reader.readRecord(authToken, "presentationRecordLink", pLinkId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private void createPCollVarWithIdAndMode(String pCollVarId, String mode) {
		DataGroup pCollVar = constructor.constructPLinkWithIdDataDividerPresentationOfAndMode(
				pCollVarId, dataDivider, presentationOf, mode);
		createRecord("presentationRecordLink", pCollVar);
	}

	private String constructIdForPLink() {
		String firstPartOfId = id.substring(0, id.lastIndexOf("Link"));
		return firstPartOfId + "PLink";
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider
				.getRecordCreator(recordTypeToCreate);
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}

	private void possiblyCreateOutputPLink() {
		String pCollVarId = constructIdForOutputPCollVar();
		if (pLinkIsMissing(pCollVarId)) {
			createPCollVarWithIdAndMode(pCollVarId, "output");
		}
	}

	private String constructIdForOutputPCollVar() {
		String firstPartOfId = id.substring(0, id.indexOf("Link"));
		return firstPartOfId + "OutputPLink";
	}
}
