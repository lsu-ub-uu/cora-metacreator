package se.uu.ub.cora.metacreator.group;

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
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class GroupCompleterTest {
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
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		authToken = "testUser";
	}

	@Test
	public void testGroupWithNoTexts() {
		GroupCompleter completer = GroupCompleter.forTextLinkedRecordType("someLinkedRecordType");

		DataGroup metadataGroup = DataCreator
				.createMetadataGroupWithIdAndTextVarAsChildReference("someMetadataGroup");

		completer.useExtendedFunctionality(authToken, metadataGroup);

		DataGroup textIdGroup = metadataGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someMetadataGroupText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"someLinkedRecordType");

		DataGroup defTextIdGroup = metadataGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someMetadataGroupDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"someLinkedRecordType");
	}

	@Test
	public void testGroupWithTexts() {
		GroupCompleter completer = GroupCompleter.forTextLinkedRecordType("someLinkedRecordType");

		DataGroup metadataGroup = DataCreator
				.createMetadataGroupWithIdAndTextVarAsChildReference("someMetadataGroup");

		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(metadataGroup,
				"textId", "textSystemOne", "anExistingText");
		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(metadataGroup,
				"defTextId", "textSystemOne", "anExistingDefText");

		completer.useExtendedFunctionality(authToken, metadataGroup);

		DataGroup textIdGroup = metadataGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"anExistingText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"textSystemOne");

		DataGroup defTextIdGroup = metadataGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"anExistingDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"textSystemOne");
	}

}
