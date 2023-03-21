/*
 * Copyright 2017, 2022, 2023 Uppsala University Library
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
import se.uu.ub.cora.metacreator.DataCreatorHelperImp;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class CollectionVarFromItemCollectionExtFunc implements ExtendedFunctionality {

	private String authToken;
	private DataGroup itemCollectionToCreateFrom;
	private String itemCollectionId;
	private String idForCollectionVariable;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		this.itemCollectionToCreateFrom = data.dataGroup;
		extractIds();

		if (collectionVarDoesNotExist(idForCollectionVariable)) {
			DataGroup collectionVar = extractDataAndConstructCollectionVariable(
					itemCollectionToCreateFrom);
			createRecord("metadataCollectionVariable", collectionVar);
		}
	}

	private void extractIds() {
		itemCollectionId = extractIdFromItemCollection();
		idForCollectionVariable = itemCollectionId + "Var";
	}

	private boolean collectionVarDoesNotExist(String id) {
		RecordReader reader = SpiderInstanceProvider.getRecordReader();
		try {
			reader.readRecord(authToken, "metadataCollectionVariable", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private DataGroup extractDataAndConstructCollectionVariable(
			DataGroup itemCollectionToCreateFrom) {

		String nameInData = itemCollectionToCreateFrom
				.getFirstAtomicValueWithNameInData("nameInData");
		String dataDivider = DataCreatorHelperImp
				.extractDataDividerIdFromDataGroup(itemCollectionToCreateFrom);
		return constructCollectionVariable(idForCollectionVariable, nameInData, dataDivider);
	}

	private DataGroup constructCollectionVariable(String id, String nameInData,
			String dataDivider) {
		CollectionVariableFactoryImp constructor = new CollectionVariableFactoryImp();
		return constructor.factorCollectionVarWithIdNameInDataDataDividerAndRefCollection(id,
				nameInData, dataDivider, itemCollectionId);
	}

	private String extractIdFromItemCollection() {
		DataGroup recordInfo = itemCollectionToCreateFrom.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}
}
