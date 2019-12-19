package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;

public class MetadataCompleterTest {

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	}

	@Test
	public void testCompleteTextsNoTextIdsExist() {
		MetadataCompleter metaCompleter = new MetadataCompleter();
		DataGroup metadataGroup = createItemWithNoTexts();
		metaCompleter.completeDataGroupWithTexts(metadataGroup);

		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("textId"), "someIdText");
		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("defTextId"), "someIdDefText");
	}

	private DataGroup createItemWithNoTexts() {
		DataGroup metadataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someId"));
		metadataGroup.addChild(recordInfo);
		return metadataGroup;
	}

	@Test
	public void testCompleteTextsTextIdExists() {
		MetadataCompleter metaCompleter = new MetadataCompleter();
		DataGroup metadataGroup = createItemWithNoTexts();
		metadataGroup.addChild(new DataAtomicSpy("textId", "someText"));

		metaCompleter.completeDataGroupWithTexts(metadataGroup);

		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("textId"), "someText");
		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("defTextId"), "someIdDefText");
	}

	@Test
	public void testCompleteTextsDefTextIdExists() {
		MetadataCompleter metaCompleter = new MetadataCompleter();
		DataGroup metadataGroup = createItemWithNoTexts();
		metadataGroup.addChild(new DataAtomicSpy("defTextId", "someDefText"));

		metaCompleter.completeDataGroupWithTexts(metadataGroup);

		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("textId"), "someIdText");
		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("defTextId"), "someDefText");
	}

	@Test
	public void testCompleteTextsTextIdAndDefTextIdExist() {
		MetadataCompleter metaCompleter = new MetadataCompleter();
		DataGroup metadataGroup = createItemWithNoTexts();
		metadataGroup.addChild(new DataAtomicSpy("textId", "someText"));
		metadataGroup.addChild(new DataAtomicSpy("defTextId", "someDefText"));

		metaCompleter.completeDataGroupWithTexts(metadataGroup);

		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("textId"), "someText");
		assertEquals(metadataGroup.getFirstAtomicValueWithNameInData("defTextId"), "someDefText");
	}

	@Test
	public void testCompleteLinkedTextsNoTextIdsExist() {
		MetadataCompleter metaCompleter = new MetadataCompleter();
		DataGroup metadataGroup = createItemWithNoTexts();
		metaCompleter.completeDataGroupWithLinkedTexts(metadataGroup, "textSystemOne");

		DataGroup textIdGroup = metadataGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"), "someIdText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"textSystemOne");
		DataGroup defTextIdGroup = metadataGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someIdDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"textSystemOne");
	}

	@Test
	public void testCompleteLinkedTextsTextIdAndDefTextIdExist() {
		MetadataCompleter metaCompleter = new MetadataCompleter();
		DataGroup metadataGroup = createItemWithNoTexts();
		addTexts(metadataGroup);

		metaCompleter.completeDataGroupWithLinkedTexts(metadataGroup, "testOtherText");

		DataGroup textIdGroup = metadataGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someExistingText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "testText");
		DataGroup defTextIdGroup = metadataGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someExistingDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"testText");
	}

	private void addTexts(DataGroup metadataGroup) {
		DataGroup textIdGroup = new DataGroupSpy("textId");
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "testText"));
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordId", "someExistingText"));
		metadataGroup.addChild(textIdGroup);

		DataGroup defTextIdGroup = new DataGroupSpy("defTextId");
		defTextIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "testText"));
		defTextIdGroup.addChild(new DataAtomicSpy("linkedRecordId", "someExistingDefText"));
		metadataGroup.addChild(defTextIdGroup);
	}
}
