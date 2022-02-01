/*
 * Copyright 2021, 2022 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

public class PCollVarFromCollectionVarCreator implements ExtendedFunctionality {

	private String authToken;
	private String presentationOf;
	private String dataDivider;
	private PCollVarConstructor constructor;
	private String idForPVars;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup collectionVarToCreateFrom = data.dataGroup;

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
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
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
