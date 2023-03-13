/*
 * Copyright 2018, 2022 Uppsala University Library
 * Copyright 2016 Olov McKie
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

package se.uu.ub.cora.metacreator.textvar;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class PVarFromTextVarCreator implements ExtendedFunctionality {

	private static final String PRESENTATION_VAR = "presentationVar";
	private String authToken;
	private String id;
	private String dataDividerString;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup dataGroup = data.dataGroup;

		extractIdAndDataDividerFromDataGroup(dataGroup);
		PTextVarFactoryImp pVarConstructor = PTextVarFactoryImp.usingMetadataIdToPresentationId(id);

		if (pVarDoesNotExistInStorage(id + "PVar")) {
			DataGroup createdInputPVar = pVarConstructor.createInputPVar();
			RecordCreator spiderRecordCreator = SpiderInstanceProvider.getRecordCreator();
			spiderRecordCreator.createAndStoreRecord(authToken, PRESENTATION_VAR, createdInputPVar);
		}
		if (pVarDoesNotExistInStorage(id + "OutputPVar")) {
			DataGroup createdOutputPVar = pVarConstructor.createOutputPVar();
			RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
			spiderRecordCreatorOutput.createAndStoreRecord(authToken, PRESENTATION_VAR,
					createdOutputPVar);
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

	private boolean pVarDoesNotExistInStorage(String pVarId) {
		try {
			RecordReader spiderRecordReader = SpiderInstanceProvider.getRecordReader();
			spiderRecordReader.readRecord(authToken, PRESENTATION_VAR, pVarId);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}
}
