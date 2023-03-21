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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactoryOldSpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordReaderOldSpy;
import se.uu.ub.cora.metacreator.metadata.ItemCollectionCreator;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataGroupSpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class ItemCollectionCreatorTest {
	private SpiderInstanceFactoryOldSpy instanceFactory;
	private String authToken = "testUser";;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private ItemCollectionCreator extendedFunctionality;

	private DataFactorySpy dataFactory;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		instanceFactory = new SpiderInstanceFactoryOldSpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		extendedFunctionality = new ItemCollectionCreator();
	}

	@Test
	public void testCreateItems() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("someCollection");
		addExistingTextsToCollection(itemCollection);
		callExtendedFunctionalityWithGroup(itemCollection);

		SpiderRecordReaderOldSpy spiderRecordReaderSpy = instanceFactory.spiderRecordReaders.get(0);
		assertEquals(spiderRecordReaderSpy.readMetadataTypes.get(0), "metadataCollectionItem");

		assertEquals(instanceFactory.spiderRecordCreators.size(), 3);
		String type = instanceFactory.spiderRecordCreators.get(0).type;
		assertEquals(type, "genericCollectionItem");

		DataGroup record = instanceFactory.spiderRecordCreators.get(0).record;
		assertEquals(record.getFirstAtomicValueWithNameInData("nameInData"), "first");

		DataGroup textIdGroup = record.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"firstItemText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		DataGroup defTextIdGroup = record.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"firstItemDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"coraText");

		assertEquals(record.getAttribute("type").getValue(), "collectionItem");

		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "firstItem");
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	@Test
	public void testCreateItemOneItemAlreadyExist() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("someOtherCollection");
		DataGroup itemReferences = itemCollection
				.getFirstGroupWithNameInData("collectionItemReferences");

		DataGroup ref = DataCreator.createItemRefWithLinkedIdAndRepeatId("alreadyExistItem", "4");
		itemReferences.addChild(ref);
		addExistingTextsToCollection(itemCollection);

		callExtendedFunctionalityWithGroup(itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 3);
		DataGroup record = instanceFactory.spiderRecordCreators.get(1).record;
		assertEquals(record.getFirstAtomicValueWithNameInData("nameInData"), "second");

		DataGroup textIdGroup = record.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"secondItemText");
		DataGroup defTextIdGroup = record.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"secondItemDefText");

		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "secondItem");
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	}

	private void addExistingTextsToCollection(DataGroup itemCollection) {
		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(itemCollection,
				"textId", "textSystemOne", "someExistingText");
		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(itemCollection,
				"defTextId", "textSystemOne", "someExistingDefText");
	}

	@Test
	public void testCreateTextNoTextExists() {
		DataGroup itemCollection = createItemCollectionWithOneExistingItem();
		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(itemCollection,
				"textId", "textSystemOne", "someNonExistingText");
		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(itemCollection,
				"defTextId", "textSystemOne", "someNonExistingDefText");

		callExtendedFunctionalityWithGroup(itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
	}

	@Test
	public void testCreateTextWhenTextExists() {
		DataGroup itemCollection = createItemCollectionWithOneExistingItem();
		addExistingTextsToCollection(itemCollection);

		callExtendedFunctionalityWithGroup(itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}

	private DataGroup createItemCollectionWithOneExistingItem() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("someOtherCollection");
		// Clear itemReferences, we are only just interested in creating texts
		itemCollection.removeFirstChildWithNameInData("collectionItemReferences");
		DataGroup itemReferences = new DataGroupSpy("collectionItemReferences");
		DataGroup ref = DataCreator.createItemRefWithLinkedIdAndRepeatId("alreadyExistItem", "4");
		itemReferences.addChild(ref);
		itemCollection.addChild(itemReferences);
		return itemCollection;
	}
}
