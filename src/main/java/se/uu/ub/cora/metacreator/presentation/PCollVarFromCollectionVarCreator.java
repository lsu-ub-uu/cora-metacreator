/*
 * Copyright 2021, 2022, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.presentation;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

@Deprecated
public class PCollVarFromCollectionVarCreator implements ExtendedFunctionality {
	private static final String PRESENTATION_TYPE = "presentation";
	private String authToken;
	private String variableRecordId;
	private String dataDivider;
	private PVarFactory pCollVarFactory;
	private String idForPVars;

	public static PCollVarFromCollectionVarCreator usingPCollVarFactory(
			PVarFactory pCollVarFactory) {
		return new PCollVarFromCollectionVarCreator(pCollVarFactory);
	}

	private PCollVarFromCollectionVarCreator(PVarFactory pCollVarFactory) {
		this.pCollVarFactory = pCollVarFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		collectInformationForPresentation(data);

		possiblyCreateInputPCollVar();
		possiblyCreateOutputPCollVar();
	}

	private void collectInformationForPresentation(ExtendedFunctionalityData data) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupFromDataGroup(data.dataGroup);
		this.authToken = data.authToken;
		dataDivider = recordGroup.getDataDivider();
		variableRecordId = recordGroup.getId();
		idForPVars = variableRecordId.substring(0, variableRecordId.indexOf("CollectionVar"));
	}

	private void possiblyCreateInputPCollVar() {
		String pCollVarId = constructIdForPCollVar();

		if (pCollVarIsMissing(pCollVarId)) {
			createPCollVarWithIdAndMode("input");
		}
	}

	private String constructIdForPCollVar() {
		return idForPVars + "PCollVar";
	}

	private boolean pCollVarIsMissing(String pCollVarId) {
		try {
			RecordReader reader = SpiderInstanceProvider.getRecordReader();
			reader.readRecord(authToken, PRESENTATION_TYPE, pCollVarId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private void createPCollVarWithIdAndMode(String mode) {
		DataRecordGroup pCollVar = pCollVarFactory.factorPVarUsingPresentationOfDataDividerAndMode(
				variableRecordId, dataDivider, mode);

		createRecord("presentationCollectionVar", pCollVar);
	}

	private void createRecord(String recordTypeToCreate, DataRecordGroup pCollVar) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
		DataGroup pCollVarAsGroup = DataProvider.createGroupFromRecordGroup(pCollVar);
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				pCollVarAsGroup);
	}

	private void possiblyCreateOutputPCollVar() {
		String pCollVarId = constructIdForOutputPCollVar();
		if (pCollVarIsMissing(pCollVarId)) {
			createPCollVarWithIdAndMode("output");
		}
	}

	private String constructIdForOutputPCollVar() {
		return idForPVars + "OutputPCollVar";
	}

	public Object onlyForTestGetPCollVarFactory() {
		return pCollVarFactory;
	}
}
