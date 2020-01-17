/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;

public class MetadataCompleterTest {

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

		DataRecordLink textIdGroup = (DataRecordLink) metadataGroup
				.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"), "someIdText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"textSystemOne");
		DataRecordLink defTextIdGroup = (DataRecordLink) metadataGroup
				.getFirstGroupWithNameInData("defTextId");
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

		DataRecordLink textIdGroup = (DataRecordLink) metadataGroup
				.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someExistingText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "testText");
		DataRecordLink defTextIdGroup = (DataRecordLink) metadataGroup
				.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someExistingDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"testText");
	}

	private void addTexts(DataGroup metadataGroup) {
		DataRecordLink textIdGroup = new DataRecordLinkSpy("textId");
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "testText"));
		textIdGroup.addChild(new DataAtomicSpy("linkedRecordId", "someExistingText"));
		metadataGroup.addChild(textIdGroup);

		DataRecordLink defTextIdGroup = new DataRecordLinkSpy("defTextId");
		defTextIdGroup.addChild(new DataAtomicSpy("linkedRecordType", "testText"));
		defTextIdGroup.addChild(new DataAtomicSpy("linkedRecordId", "someExistingDefText"));
		metadataGroup.addChild(defTextIdGroup);
	}
}
