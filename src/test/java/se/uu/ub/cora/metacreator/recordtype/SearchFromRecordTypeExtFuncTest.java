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

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.spy.RecordCreatorSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.RecordUpdaterSpy;
import se.uu.ub.cora.metacreator.spy.SearchGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SearchFromRecordTypeExtFuncTest {
	private SearchFromRecordTypeExtFunc extendedFunctionality;
	private DataFactorySpy dataFactory;
	private SpiderInstanceFactorySpy spiderInstanceFactory;
	private String authToken;
	private SearchGroupFactorySpy searchGroupFactory;
	private DataGroupSpy recordTypeGroup;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private RecordReaderSpy recordReaderSpy;
	private RecordCreatorSpy recordCreatorSpy;
	private RecordUpdaterSpy recordUpdaterSpy;
	private static final String ID = "someRecordId";

	@BeforeMethod
	public void setup() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		spiderInstanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);
		recordReaderSpy = new RecordReaderSpy();
		spiderInstanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReaderSpy);
		recordCreatorSpy = new RecordCreatorSpy();
		spiderInstanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordCreator",
				() -> recordCreatorSpy);
		recordUpdaterSpy = new RecordUpdaterSpy();
		spiderInstanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordUpdater",
				() -> recordUpdaterSpy);

		authToken = "someAuthToken";
		recordTypeGroup = new DataGroupSpy();
		setUpRecordGroupCreatedFromGroup();
		data = createExtendedFunctionalityWithDataGroupSpy(recordTypeGroup);

		searchGroupFactory = new SearchGroupFactorySpy();
		extendedFunctionality = SearchFromRecordTypeExtFunc
				.usingSearchGroupFactory(searchGroupFactory);

	}

	private void setUpRecordGroupCreatedFromGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> ID);
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
	}

	private ExtendedFunctionalityData createExtendedFunctionalityWithDataGroupSpy(
			DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		return data;
	}

	@Test
	public void testOnlyForTestGetSearchFactory() throws Exception {
		SearchGroupFactory searchGroupFactory2 = extendedFunctionality
				.onlyForTestGetSearchGroupFactory();
		assertSame(searchGroupFactory2, searchGroupFactory);
	}

	@Test
	public void testGroupChangedToRecordGroup() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, recordTypeGroup);
	}

	@Test
	public void testSearchExistInStorage() {
		extendedFunctionality.useExtendedFunctionality(data);

		assertStorageIsCheckedForExistenseOfFactoredSearch();
		assertNoSearchCreatedInStorage();
	}

	private void assertExtFuncFactorsSearchWithCorrectParameters() {
		searchGroupFactory.MCR.assertParameters("factorUsingRecordTypeIdToSearchInAndDataDivider",
				0, dataRecordGroup.getId(), dataRecordGroup.getDataDivider());
	}

	private DataRecordGroupSpy getFactoredSearch() {
		return (DataRecordGroupSpy) searchGroupFactory.MCR
				.getReturnValue("factorUsingRecordTypeIdToSearchInAndDataDivider", 0);
	}

	private void assertStorageIsCheckedForExistenseOfFactoredSearch() {
		recordReaderSpy.MCR.assertParameters("readRecord", 0, authToken, "search", ID + "Search");
	}

	private void assertNoSearchCreatedInStorage() {
		spiderInstanceFactory.MCR.assertMethodNotCalled("factorRecordCreator");
	}

	@Test
	public void testCollectionVarDoesNotExistInStorageAndIsStored() {
		setupRecordReaderToThrowErrorForReadWithId("inputIdRecordGroup");

		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncFactorsSearchWithCorrectParameters();
		DataRecordGroupSpy searchFromSpy = getFactoredSearch();
		assertStorageIsCheckedForExistenseOfFactoredSearch();

		assertDataRecordGroupForSearchChangedToGroup(searchFromSpy);
		var groupFromFactoredSearch = getGroupCreatedFromColVarRecordGroup();
		assertSearchGroupStoredInStorage(groupFromFactoredSearch);
	}

	private void setupRecordReaderToThrowErrorForReadWithId(String id) {
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord",
				new RecordNotFoundException("Record not found"));
	}

	private void assertDataRecordGroupForSearchChangedToGroup(DataRecordGroupSpy searchFromSpy) {
		dataFactory.MCR.assertParameters("factorGroupFromDataRecordGroup", 0, searchFromSpy);
	}

	private Object getGroupCreatedFromColVarRecordGroup() {
		return dataFactory.MCR.getReturnValue("factorGroupFromDataRecordGroup", 0);
	}

	private void assertSearchGroupStoredInStorage(Object groupFromFactoredSearch) {
		spiderInstanceFactory.MCR.assertParameters("factorRecordCreator", 0);
		recordCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0, authToken, "search",
				groupFromFactoredSearch);
	}

	@Test
	public void testAddMissingSearchLink() {

		extendedFunctionality.useExtendedFunctionality(data);

		assertAddLinkToRecordType(0, "search", "search", ID + "Search");

		dataFactory.MCR.assertNumberOfCallsToMethod("factorRecordLinkUsingNameInDataAndTypeAndId",
				1);
		recordUpdaterSpy.MCR.assertParameters("updateRecord", 0, authToken, "recordType", ID,
				recordTypeGroup);
	}

	private void assertAddLinkToRecordType(int callNumber, String nameInData,
			String linkedRecordType, String linkedRecordId) {
		dataRecordGroup.MCR.assertParameters("containsChildWithNameInData", callNumber, nameInData);
		var recordLink = assertCreateLink(callNumber, nameInData, linkedRecordType, linkedRecordId);
		recordTypeGroup.MCR.assertParameters("addChild", callNumber, recordLink);
	}

	private Object assertCreateLink(int callNumber, String nameInData, String linkedRecordType,
			String linkedRecordId) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", callNumber,
				nameInData, linkedRecordType, linkedRecordId);
		return dataFactory.MCR.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId",
				callNumber);
	}

	@Test
	public void testSearchLinkAlreadyExists() throws Exception {
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				() -> true);

		extendedFunctionality.useExtendedFunctionality(data);

		dataFactory.MCR.assertMethodNotCalled("factorRecordLinkUsingNameInDataAndTypeAndId");
		recordUpdaterSpy.MCR.assertMethodNotCalled("updateRecord");
	}

}
