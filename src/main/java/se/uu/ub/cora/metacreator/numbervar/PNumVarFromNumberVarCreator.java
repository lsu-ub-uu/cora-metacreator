/*
 * Copyright 2018, 2022 Uppsala University Library
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
package se.uu.ub.cora.metacreator.numbervar;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class PNumVarFromNumberVarCreator implements ExtendedFunctionality {

	private static final String PRESENTATION_NUMBER_VAR = "presentationNumberVar";
	private String authToken;
	private String id;
	private String dataDividerString;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup dataGroup = data.dataGroup;
		extractIdAndDataDividerFromDataGroup(dataGroup);

		PNumVarFactoryImp pNumVarConstructor = PNumVarFactoryImp.withTextVarIdAndDataDivider(id,
				dataDividerString);

		possiblyCreateInputPNumVar(pNumVarConstructor);
		possiblyCreateOutputPNumVar(pNumVarConstructor);
	}

	private void possiblyCreateOutputPNumVar(PNumVarFactoryImp pNumVarConstructor) {
		if (pNumVarDoesNotExistInStorage(id, "OutputPNumVar")) {
			DataGroup outputPNumVar = pNumVarConstructor.createOutputPNumVar();
			createPNumVar(outputPNumVar);
		}
	}

	private void createPNumVar(DataGroup inputPNumVar) {
		RecordCreator spiderRecordCreator = SpiderInstanceProvider.getRecordCreator();
		spiderRecordCreator.createAndStoreRecord(authToken, PRESENTATION_NUMBER_VAR, inputPNumVar);
	}

	private void possiblyCreateInputPNumVar(PNumVarFactoryImp pNumVarConstructor) {
		if (pNumVarDoesNotExistInStorage(id, "PNumVar")) {
			DataGroup inputPNumVar = pNumVarConstructor.createInputPNumVar();
			createPNumVar(inputPNumVar);
		}
	}

	private void extractIdAndDataDividerFromDataGroup(DataGroup dataGroup) {
		DataGroup recordInfoGroup = dataGroup.getFirstGroupWithNameInData("recordInfo");
		id = extractIdFromDataGroup(recordInfoGroup);
		dataDividerString = extractDataDividerFromDataGroup(recordInfoGroup);
	}

	private String extractIdFromDataGroup(DataGroup recordInfoGroup) {
		return recordInfoGroup.getFirstAtomicValueWithNameInData("id");
	}

	private String extractDataDividerFromDataGroup(DataGroup recordInfoGroup) {
		return recordInfoGroup.getFirstGroupWithNameInData("dataDivider")
				.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private boolean pNumVarDoesNotExistInStorage(String id, String suffix) {
		String idWithoutEnding = id.substring(0, id.indexOf("NumberVar"));
		try {
			RecordReader spiderRecordReader = SpiderInstanceProvider.getRecordReader();
			spiderRecordReader.readRecord(authToken, PRESENTATION_NUMBER_VAR,
					idWithoutEnding + suffix);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

}
