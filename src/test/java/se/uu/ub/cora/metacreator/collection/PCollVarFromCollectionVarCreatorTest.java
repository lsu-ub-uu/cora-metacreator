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
import se.uu.ub.cora.metacreator.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorSpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class PCollVarFromCollectionVarCreatorTest {
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
	public void testPCollVarsDoesNotExist() {
		DataGroup collectionVar = DataCreator
				.createCollectionVariableWithIdDataDividerAndNameInData("someTestCollectionVar",
						"testSystem", "some");

		PCollVarFromCollectionVarCreator creator = new PCollVarFromCollectionVarCreator();
		creator.useExtendedFunctionality(authToken, collectionVar);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
		assertCorrectlyCreatedInputPCollVar();
		assertCorrectlyCreatedOutputPCollVar();
	}

	private void assertCorrectlyCreatedInputPCollVar() {
		SpiderRecordCreatorSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators.get(0);
		assertEquals(spiderRecordCreatorSpy.type, "presentationCollectionVar");
		DataGroup record = spiderRecordCreatorSpy.record;

		assertEquals(record.getNameInData(), "presentation");
		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), "input");

		assertCorrectPresentationOf(record);
		assertCorrectRecordInfo(record, "someTestPCollVar");
		assertEquals(record.getAttribute("type").getValue(), "pCollVar");
	}

	private void assertCorrectPresentationOf(DataGroup record) {
		DataGroup presentationOf = record.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someTestCollectionVar");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataCollectionVariable");
	}

	private void assertCorrectRecordInfo(DataGroup record, String expextedId) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), expextedId);
		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "testSystem");
	}

	private void assertCorrectlyCreatedOutputPCollVar() {
		SpiderRecordCreatorSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators.get(1);
		assertEquals(spiderRecordCreatorSpy.type, "presentationCollectionVar");
		DataGroup record = spiderRecordCreatorSpy.record;
		assertEquals(record.getNameInData(), "presentation");
		assertEquals(record.getFirstAtomicValueWithNameInData("mode"), "output");

		assertCorrectPresentationOf(record);
		assertCorrectRecordInfo(record, "someTestOutputPCollVar");
		assertEquals(record.getAttribute("type").getValue(), "pCollVar");
	}

	@Test
	public void testPCollVarsAlreadyExist() {
		DataGroup collectionVar = DataCreator
				.createCollectionVariableWithIdDataDividerAndNameInData("someExistingCollectionVar",
						"testSystem", "someExisting");

		PCollVarFromCollectionVarCreator creator = new PCollVarFromCollectionVarCreator();
		creator.useExtendedFunctionality(authToken, collectionVar);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}
}
