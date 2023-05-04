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
package se.uu.ub.cora.metacreator.metadata;

import java.util.Optional;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class ColVarFromItemCollectionExtFunc implements ExtendedFunctionality {
	private String authToken;
	private CollectionVariableFactory colVarFactory;

	public static ColVarFromItemCollectionExtFunc usingCollectionVariableFactory(
			CollectionVariableFactory colVarFactory) {
		return new ColVarFromItemCollectionExtFunc(colVarFactory);
	}

	private ColVarFromItemCollectionExtFunc(CollectionVariableFactory colVarFactory) {
		this.colVarFactory = colVarFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		authToken = data.authToken;
		DataGroup dataGroup = data.dataGroup;
		DataRecordGroup recordGroup = DataProvider.createRecordGroupFromDataGroup(dataGroup);

		if (dataToHandleIsOfTypeItemCollection(recordGroup)) {
			createCollectionVarForItemCollection(recordGroup);
		}
	}

	private boolean dataToHandleIsOfTypeItemCollection(DataRecordGroup recordGroup) {
		Optional<String> type = recordGroup.getAttributeValue("type");
		return type.isPresent() && "itemCollection".equals(type.get());
	}

	private void createCollectionVarForItemCollection(DataRecordGroup recordGroup) {
		String nameInData = recordGroup.getFirstAtomicValueWithNameInData("nameInData");

		DataRecordGroup colVar = colVarFactory
				.factorCollectionVarUsingItemCollectionIdDataDividerAndNameInData(
						recordGroup.getId(), recordGroup.getDataDivider(), nameInData);
		if (collectionVarDoesNotExistInStorge(colVar.getId())) {
			storeColVarInStorage(colVar);
		}
	}

	private boolean collectionVarDoesNotExistInStorge(String id) {
		RecordReader reader = SpiderInstanceProvider.getRecordReader();
		try {
			reader.readRecord(authToken, "metadata", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void storeColVarInStorage(DataRecordGroup colVar) {
		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		DataGroup groupFromColVar = DataProvider.createGroupFromRecordGroup(colVar);
		recordCreator.createAndStoreRecord(authToken, "metadata", groupFromColVar);
	}

	public CollectionVariableFactory onlyForTestGetColVarFactory() {
		return colVarFactory;
	}
}
