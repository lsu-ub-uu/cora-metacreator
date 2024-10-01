/*
 * Copyright 2017, 2022, 2023, 2024 Uppsala University Library
 * Copyright 2023 Olov McKie
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
package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SearchFromRecordTypeExtFunc implements ExtendedFunctionality {
	private String authToken;
	private SearchGroupFactory searchGroupFactory;

	public static SearchFromRecordTypeExtFunc usingSearchGroupFactory(
			SearchGroupFactory searchGroupFactory) {
		return new SearchFromRecordTypeExtFunc(searchGroupFactory);
	}

	private SearchFromRecordTypeExtFunc(SearchGroupFactory searchGroupFactory) {
		this.searchGroupFactory = searchGroupFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		authToken = data.authToken;
		DataRecordGroup recordGroup = data.dataRecordGroup;
		createCollectionVarForItemCollection(recordGroup);
	}

	private void createCollectionVarForItemCollection(DataRecordGroup recordGroup) {
		DataRecordGroup search = searchGroupFactory.factorUsingRecordTypeIdToSearchInAndDataDivider(
				recordGroup.getId(), recordGroup.getDataDivider());
		if (collectionVarDoesNotExistInStorge(search.getId())) {
			storeSearchInStorage(search);
		}
	}

	private boolean collectionVarDoesNotExistInStorge(String id) {
		RecordReader reader = SpiderInstanceProvider.getRecordReader();
		try {
			reader.readRecord(authToken, "search", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void storeSearchInStorage(DataRecordGroup colVar) {
		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		recordCreator.createAndStoreRecord(authToken, "search", colVar);
	}

	public SearchGroupFactory onlyForTestGetSearchGroupFactory() {
		return searchGroupFactory;
	}
}
