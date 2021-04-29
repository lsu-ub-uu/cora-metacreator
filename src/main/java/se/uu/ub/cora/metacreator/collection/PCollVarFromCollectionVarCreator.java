package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

public class PCollVarFromCollectionVarCreator implements ExtendedFunctionality {

	private String authToken;
	private String presentationOf;
	private String dataDivider;
	private PCollVarConstructor constructor;
	private String idForPVars;

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup collectionVarToCreateFrom) {
		this.authToken = authToken;

		setParametersForCreation(collectionVarToCreateFrom);

		possiblyCreateInputPCollVar();
		possiblyCreateOutputPCollVar();
	}

	private void setParametersForCreation(DataGroup collectionvarToCreateFrom) {
		constructor = new PCollVarConstructor();
		presentationOf = DataCreatorHelper.extractIdFromDataGroup(collectionvarToCreateFrom);
		dataDivider = DataCreatorHelper
				.extractDataDividerStringFromDataGroup(collectionvarToCreateFrom);
		String id = DataCreatorHelper.extractIdFromDataGroup(collectionvarToCreateFrom);
		idForPVars = id.substring(0, id.indexOf("CollectionVar"));
	}

	private void possiblyCreateInputPCollVar() {
		String pCollVarId = constructIdForPCollVar();

		if (pCollVarIsMissing(pCollVarId)) {
			createPCollVarWithIdAndMode(pCollVarId, "input");
		}
	}

	private boolean pCollVarIsMissing(String pCollVarId) {
		try {
			RecordReader reader = SpiderInstanceProvider.getRecordReader();
			reader.readRecord(authToken, "presentationCollectionVar", pCollVarId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private void createPCollVarWithIdAndMode(String pCollVarId, String mode) {
		DataGroup pCollVar = constructor.constructPCollVarWithIdDataDividerPresentationOfAndMode(
				pCollVarId, dataDivider, presentationOf, mode);
		createRecord("presentationCollectionVar", pCollVar);
	}

	private String constructIdForPCollVar() {
		return idForPVars + "PCollVar";
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider
				.getRecordCreator(recordTypeToCreate);
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}

	private void possiblyCreateOutputPCollVar() {
		String pCollVarId = constructIdForOutputPCollVar();
		if (pCollVarIsMissing(pCollVarId)) {
			createPCollVarWithIdAndMode(pCollVarId, "output");
		}
	}

	private String constructIdForOutputPCollVar() {
		return idForPVars + "OutputPCollVar";
	}
}
