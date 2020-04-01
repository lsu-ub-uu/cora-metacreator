package se.uu.ub.cora.metacreator.collection;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataGroupSpy;
import se.uu.ub.cora.metacreator.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordReaderSpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class ItemCollectionCreatorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		authToken = "testUser";
	}

	@Test
	public void testCreateItems() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("someCollection");
		addExistingTextsToCollection(itemCollection);
		ItemCollectionCreator creator = ItemCollectionCreator
				.forImplementingTextType("textSystemOne");
		creator.useExtendedFunctionality(authToken, itemCollection);

		SpiderRecordReaderSpy spiderRecordReaderSpy = instanceFactory.spiderRecordReaders.get(0);
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

	@Test
	public void testCreateItemOneItemAlreadyExist() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("someOtherCollection");
		DataGroup itemReferences = itemCollection
				.getFirstGroupWithNameInData("collectionItemReferences");

		DataGroup ref = DataCreator.createItemRefWithLinkedIdAndRepeatId("alreadyExistItem", "4");
		itemReferences.addChild(ref);
		addExistingTextsToCollection(itemCollection);

		ItemCollectionCreator creator = ItemCollectionCreator
				.forImplementingTextType("textSystemOne");
		creator.useExtendedFunctionality(authToken, itemCollection);

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

		ItemCollectionCreator creator = ItemCollectionCreator
				.forImplementingTextType("textSystemOne");
		creator.useExtendedFunctionality(authToken, itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
	}

	@Test
	public void testCreateTextWhenTextExists() {
		DataGroup itemCollection = createItemCollectionWithOneExistingItem();
		addExistingTextsToCollection(itemCollection);

		ItemCollectionCreator creator = ItemCollectionCreator
				.forImplementingTextType("textSystemOne");
		creator.useExtendedFunctionality(authToken, itemCollection);

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
