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

import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metacreator.spy.CollectionItemFactorySpy;
import se.uu.ub.cora.metacreator.spy.RecordCreatorSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class CollectionItemsFromItemCollectionExtFuncTest {
	private DataFactorySpy dataFactory;
	private SpiderInstanceFactorySpy spiderInstanceFactory;
	private String authToken;
	private CollectionItemsFromItemCollectionExtFunc extendedFunctionality;
	private CollectionItemFactorySpy colItemFactory;
	private DataGroupSpy dataGroup;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private RecordReaderSpy recordReaderSpy;
	private RecordCreatorSpy recordCreatorSpy;
	private DataGroupSpy collectionItemReferences;

	@BeforeMethod
	public void setUp() {
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

		authToken = "someAuthToken";
		dataGroup = new DataGroupSpy();
		setUpRecordGroupCreatedFromGroup();
		data = createExtendedFunctionalityWithDataGroupSpy();

		setUpColItemFactoryToReturnId();
	}

	private void setUpRecordGroupCreatedFromGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("itemCollection"), "type");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someCollection");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");

		setUpRecordGroupCollectionItemReferences();
	}

	private void setUpRecordGroupCollectionItemReferences() {
		collectionItemReferences = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> collectionItemReferences, "collectionItemReferences");

		List<DataRecordLink> refs = new ArrayList<>();
		collectionItemReferences.MRV.setDefaultReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> refs);

		DataRecordLinkSpy ref = new DataRecordLinkSpy();
		refs.add(ref);
		ref.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "firstItem");
		DataRecordLinkSpy ref2 = new DataRecordLinkSpy();
		refs.add(ref2);
		ref2.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "secondItem");
	}

	private ExtendedFunctionalityData createExtendedFunctionalityWithDataGroupSpy() {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		return data;
	}

	private void setUpColItemFactoryToReturnId() {
		colItemFactory = new CollectionItemFactorySpy();
		extendedFunctionality = CollectionItemsFromItemCollectionExtFunc
				.usingCollectionItemFactory(colItemFactory);

		DataRecordGroupSpy recordGroup = new DataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId",
				() -> "generatedIdFromColItemFactoryItem");
		colItemFactory.MRV.setDefaultReturnValuesSupplier(
				"factorCollectionItemUsingItemCollectionIdAndDataDivider", () -> recordGroup);
	}

	@Test
	public void testOnlyForTestGetCollectionItemFactory() throws Exception {
		CollectionItemFactory colItemFactory2 = extendedFunctionality
				.onlyForTestGetCollectionItemFactory();
		assertSame(colItemFactory, colItemFactory2);
	}

	@Test
	public void testGroupChangedToRecordGroup() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, dataGroup);
	}

	@Test
	public void testNoTypeOfMetadataDoNothing() throws Exception {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.empty(), "type");

		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncDoesNothing();
	}

	private void assertExtFuncDoesNothing() {
		colItemFactory.MCR
				.assertMethodNotCalled("factorCollectionItemUsingItemCollectionIdAndDataDivider");
		spiderInstanceFactory.MCR.assertMethodNotCalled("factorRecordReader");
		assertNoColItemCreatedInStorage();
	}

	private void assertNoColItemCreatedInStorage() {
		spiderInstanceFactory.MCR.assertMethodNotCalled("factorRecordCreator");
	}

	@Test
	public void testWrongTypeOfMetadataDoNothing() throws Exception {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of("NOTitemCollection"), "type");

		extendedFunctionality.useExtendedFunctionality(data);

		assertExtFuncDoesNothing();
	}

	@Test
	public void testTwoItemsBothExistsInStorageSinceBefore() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);

		assertItemFactoredUsingIdAndFactorNum("firstItem", 0);
		assertIfFactoredCollectionItemExistsInStorageForFactorNumAndReadNum(0, 0);

		assertItemFactoredUsingIdAndFactorNum("secondItem", 1);
		assertIfFactoredCollectionItemExistsInStorageForFactorNumAndReadNum(1, 1);

		assertNoColItemCreatedInStorage();
	}

	private void assertIfFactoredCollectionItemExistsInStorageForFactorNumAndReadNum(int factorNum,
			int readNum) {
		String factorMethod = "factorCollectionItemUsingItemCollectionIdAndDataDivider";
		DataRecordGroupSpy generatedColItem = (DataRecordGroupSpy) colItemFactory.MCR
				.getReturnValue(factorMethod, factorNum);
		Object idFromSpy = generatedColItem.MCR.getReturnValue("getId", factorNum);

		recordReaderSpy.MCR.assertParameters("readRecord", readNum, authToken, "metadata",
				idFromSpy);
	}

	private void assertItemFactoredUsingIdAndFactorNum(String itemId, int factorNum) {
		String factorMethod = "factorCollectionItemUsingItemCollectionIdAndDataDivider";
		colItemFactory.MCR.assertParameters(factorMethod, factorNum, itemId, "someDataDivider");
	}

	@Test
	public void testTwoItemsNoneExistsInStorageSinceBefore() throws Exception {
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord", new RecordNotFoundException(""));

		extendedFunctionality.useExtendedFunctionality(data);

		assertItemFactoredUsingIdAndFactorNum("firstItem", 0);
		assertIfFactoredCollectionItemExistsInStorageForFactorNumAndReadNum(0, 0);
		assertFactoredItemStoredUsingFactorNumAndStoreNum(0, 0);

		assertItemFactoredUsingIdAndFactorNum("secondItem", 1);
		assertIfFactoredCollectionItemExistsInStorageForFactorNumAndReadNum(1, 1);
		assertFactoredItemStoredUsingFactorNumAndStoreNum(1, 1);

	}

	private void assertFactoredItemStoredUsingFactorNumAndStoreNum(int factorNum, int createNum) {
		var groupChangedForStorage = dataFactory.MCR
				.getReturnValue("factorGroupFromDataRecordGroup", factorNum);
		recordCreatorSpy.MCR.assertParameters("createAndStoreRecord", createNum, authToken,
				"metadata", groupChangedForStorage);
	}
}
