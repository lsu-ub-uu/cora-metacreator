/*
 * Copyright 2017, 2022, 2023 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.spider.record.RecordUpdater;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SearchFromRecordTypeExtFunc implements ExtendedFunctionality {
	private static final String SEARCH_TYPE = "search";
	private static final String SEARCH_CHILD = "search";
	private String authToken;
	private SearchGroupFactory searchGroupFactory;
	private String recordTypeId;
	private String searchId;
	private DataRecordGroup recordTypeRecord;
	private DataGroup recordTypeGroup;

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
		recordTypeGroup = data.dataGroup;
		setupVariables();

		createSearchIfMissing();
		addSearchLinkIfMissing();
	}

	private void setupVariables() {
		recordTypeRecord = DataProvider.createRecordGroupFromDataGroup(recordTypeGroup);
		recordTypeId = recordTypeRecord.getId();
		searchId = recordTypeId + "Search";
	}

	private void createSearchIfMissing() {
		if (searchNotExistsInStorge()) {
			DataRecordGroup search = createSearch();
			storeSearchInStorage(search);
		}
	}

	private DataRecordGroup createSearch() {
		return searchGroupFactory.factorUsingRecordTypeIdToSearchInAndDataDivider(recordTypeId,
				recordTypeRecord.getDataDivider());
	}

	private boolean searchNotExistsInStorge() {
		RecordReader reader = SpiderInstanceProvider.getRecordReader();
		try {
			reader.readRecord(authToken, SEARCH_CHILD, searchId);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void storeSearchInStorage(DataRecordGroup searchRecordGroup) {
		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		DataGroup groupFromColVar = DataProvider.createGroupFromRecordGroup(searchRecordGroup);
		recordCreator.createAndStoreRecord(authToken, SEARCH_CHILD, groupFromColVar);
	}

	private void addSearchLinkIfMissing() {
		if (searchLinkDoesNotExist()) {
			createLinkAndAddItToRecordType();
			updateRecordType();
		}
	}

	private void updateRecordType() {
		RecordUpdater recordUpdater = SpiderInstanceProvider.getRecordUpdater();
		recordUpdater.updateRecord(authToken, "recordType", recordTypeId, recordTypeGroup);
	}

	private void createLinkAndAddItToRecordType() {
		DataRecordLink searchLink = DataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId(SEARCH_CHILD, SEARCH_TYPE, searchId);
		recordTypeGroup.addChild(searchLink);
	}

	private boolean searchLinkDoesNotExist() {
		return !recordTypeRecord.containsChildWithNameInData(SEARCH_CHILD);
	}

	public SearchGroupFactory onlyForTestGetSearchGroupFactory() {
		return searchGroupFactory;
	}
}
