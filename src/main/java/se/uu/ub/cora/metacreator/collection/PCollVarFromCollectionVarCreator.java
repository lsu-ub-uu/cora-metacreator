package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.SpiderRecordCreator;
import se.uu.ub.cora.spider.record.SpiderRecordReader;

public class PCollVarFromCollectionVarCreator implements ExtendedFunctionality {

	private String authToken;
	private String nameInData;
	private String presentationOf;
	private String dataDivider;
	private PCollVarConstructor constructor;
	private String idForPVars;

	@Override
	public void useExtendedFunctionality(String authToken,
			SpiderDataGroup collectionVarToCreateFrom) {
		this.authToken = authToken;

		setParametersForCreation(collectionVarToCreateFrom);

		possiblyCreateInputPCollVar();
		possiblyCreateOutputPCollVar();
	}

	private void setParametersForCreation(SpiderDataGroup collectionvarToCreateFrom) {
		constructor = new PCollVarConstructor();
		nameInData = collectionvarToCreateFrom.extractAtomicValue("nameInData");
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
			SpiderRecordReader reader = SpiderInstanceProvider.getSpiderRecordReader();
			reader.readRecord(authToken, "presentationCollectionVar", pCollVarId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private void createPCollVarWithIdAndMode(String pCollVarId, String mode) {
		SpiderDataGroup pCollVar = constructor
				.constructPCollVarWithIdDataDividerPresentationOfAndMode(pCollVarId, dataDivider,
						presentationOf, mode);
		createRecord("presentationCollectionVar", pCollVar);
	}

	private String constructIdForPCollVar() {
		return idForPVars + "PCollVar";
	}

	private void createRecord(String recordTypeToCreate, SpiderDataGroup spiderDataGroupToCreate) {
		SpiderRecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider
				.getSpiderRecordCreator();
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				spiderDataGroupToCreate);
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
