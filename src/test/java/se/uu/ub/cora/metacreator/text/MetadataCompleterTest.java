/*
 * Copyright 2017, 2019, 2020 Uppsala University Library
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
package se.uu.ub.cora.metacreator.text;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.spy.DataAtomicSpy;
import se.uu.ub.cora.metacreator.spy.DataGroupSpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkSpy;

public class MetadataCompleterTest {

	private DataRecordLinkFactory dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
	}

	private DataGroup createItemWithNoTexts() {
		DataGroup metadataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someId"));
		metadataGroup.addChild(recordInfo);
		return metadataGroup;
	}

	@Test
	public void testCompleteLinkedTextsNoTextIdsExist() {
		MetadataCompleter metaCompleter = new MetadataCompleterImp();
		DataGroup metadataGroup = createItemWithNoTexts();
		metaCompleter.completeDataGroupWithLinkedTexts(metadataGroup, "textSystemOne");

		DataRecordLink textIdGroup = (DataRecordLink) metadataGroup
				.getFirstChildWithNameInData("textId");
		assertEquals(textIdGroup.getLinkedRecordId(), "someIdText");
		assertEquals(textIdGroup.getLinkedRecordType(), "textSystemOne");
		DataRecordLink defTextIdGroup = (DataRecordLink) metadataGroup
				.getFirstChildWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getLinkedRecordId(), "someIdDefText");
		assertEquals(defTextIdGroup.getLinkedRecordType(), "textSystemOne");
	}

	@Test
	public void testCompleteLinkedTextsTextIdAndDefTextIdExist() {
		MetadataCompleterImp metaCompleter = new MetadataCompleterImp();
		DataGroup metadataGroup = createItemWithNoTexts();
		addTexts(metadataGroup);

		metaCompleter.completeDataGroupWithLinkedTexts(metadataGroup, "testOtherText");

		DataRecordLink textIdGroup = (DataRecordLink) metadataGroup
				.getFirstChildWithNameInData("textId");
		assertEquals(textIdGroup.getLinkedRecordId(), "someExistingText");
		assertEquals(textIdGroup.getLinkedRecordType(), "testText");
		DataRecordLink defTextIdGroup = (DataRecordLink) metadataGroup
				.getFirstChildWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getLinkedRecordId(), "someExistingDefText");
		assertEquals(defTextIdGroup.getLinkedRecordType(), "testText");
	}

	private void addTexts(DataGroup metadataGroup) {
		DataRecordLink textIdGroup = new DataRecordLinkSpy("textId", "testText",
				"someExistingText");
		metadataGroup.addChild(textIdGroup);

		DataRecordLink defTextIdGroup = new DataRecordLinkSpy("defTextId", "testText",
				"someExistingDefText");
		metadataGroup.addChild(defTextIdGroup);
	}
}
