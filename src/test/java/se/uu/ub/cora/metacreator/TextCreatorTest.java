package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorSpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class TextCreatorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		authToken = "testUser";
	}

	@Test
	public void testNonExistingTexts() {
		TextCreator creator = TextCreator.forImplementingTextType("textSystemOne");
		DataGroup item = DataCreator
				.createCollectionItemGroupWithIdTextIdDefTextIdAndImplementingTextType("firstItem",
						"nonExistingText", "nonExistingDefText", "textSystemOne");

		creator.useExtendedFunctionality(authToken, item);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
		assertCorrectTextCreatedWithUserIdAndTypeAndId(0, "nonExistingText");
		assertCorrectTextCreatedWithUserIdAndTypeAndId(1, "nonExistingDefText");
	}

	private void assertCorrectTextCreatedWithUserIdAndTypeAndId(int createdTextNo,
			String createdIdForText) {
		SpiderRecordCreatorSpy spiderRecordCreator = instanceFactory.spiderRecordCreators
				.get(createdTextNo);
		assertEquals(spiderRecordCreator.authToken, authToken);
		assertEquals(spiderRecordCreator.type, "textSystemOne");
		DataGroup createdTextRecord = spiderRecordCreator.record;
		DataGroup recordInfo = createdTextRecord.getFirstGroupWithNameInData("recordInfo");
		String id = recordInfo.getFirstAtomicValueWithNameInData("id");
		assertEquals(id, createdIdForText);
	}

	@Test
	public void testWithExistingTextsInStorage() {
		TextCreator creator = TextCreator.forImplementingTextType("textSystemOne");

		DataGroup item = DataCreator
				.createCollectionItemGroupWithIdTextIdDefTextIdAndImplementingTextType("firstItem",
						"someExistingTextId", "someExistingDefTextId", "textSystemOne");
		creator.useExtendedFunctionality(authToken, item);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}
}
