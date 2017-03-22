package se.uu.ub.cora.metacreator.collection;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class ItemCollectionCreatorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String userId;

	@BeforeMethod
	public void setUp() {
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		userId = "testUser";
	}

	@Test
	public void testCreateItems() {
		SpiderDataGroup itemCollection = DataCreator.createItemCollectionWithId("someCollection");

		ItemCollectionCreator creator = new ItemCollectionCreator();
		creator.useExtendedFunctionality(userId, itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 3);
		SpiderDataGroup record = instanceFactory.spiderRecordCreators.get(0).record;
		assertEquals(record.extractAtomicValue("nameInData"), "first");
		assertEquals(record.extractAtomicValue("textId"), "firstItemText");
		assertEquals(record.extractAtomicValue("defTextId"), "firstItemDefText");

		SpiderDataGroup recordInfo = record.extractGroup("recordInfo");
		assertEquals(recordInfo.extractAtomicValue("id"), "firstItem");
		SpiderDataGroup dataDivider = recordInfo.extractGroup("dataDivider");
		assertEquals(dataDivider.extractAtomicValue("linkedRecordId"), "test");
	}

	@Test
	public void testCreateItemOneItemAlreadyExist() {
		SpiderDataGroup itemCollection = DataCreator
				.createItemCollectionWithId("someOtherCollection");
		SpiderDataGroup itemReferences = itemCollection.extractGroup("collectionItemReferences");

		SpiderDataGroup ref = DataCreator.createItemRefWithLinkedIdAndRepeatId("alreadyExistItem",
				"4");
		itemReferences.addChild(ref);

		ItemCollectionCreator creator = new ItemCollectionCreator();
		creator.useExtendedFunctionality(userId, itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 3);
		SpiderDataGroup record = instanceFactory.spiderRecordCreators.get(1).record;
		assertEquals(record.extractAtomicValue("nameInData"), "second");
		assertEquals(record.extractAtomicValue("textId"), "secondItemText");
		assertEquals(record.extractAtomicValue("defTextId"), "secondItemDefText");

		SpiderDataGroup recordInfo = record.extractGroup("recordInfo");
		assertEquals(recordInfo.extractAtomicValue("id"), "secondItem");
		SpiderDataGroup dataDivider = recordInfo.extractGroup("dataDivider");
		assertEquals(dataDivider.extractAtomicValue("linkedRecordId"), "test");
	}
}
