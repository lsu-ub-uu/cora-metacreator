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
package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class TextCreator implements ExtendedFunctionality {
	private TextFactory textFactory;
	private String authToken;
	private DataRecordGroup recordGroup;
	private DataGroup dataGroup;

	public static TextCreator usingTextFactory(TextFactory textFactory) {
		return new TextCreator(textFactory);
	}

	private TextCreator(TextFactory textFactory) {
		this.textFactory = textFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		authToken = data.authToken;
		dataGroup = data.dataGroup;
		recordGroup = DataProvider.createRecordGroupFromDataGroup(dataGroup);

		possiblyCreateTextLinkUsingNameInDataForTextLink("textId", "Text");
		possiblyCreateTextLinkUsingNameInDataForTextLink("defTextId", "DefText");

		createTextsIfMissing();
	}

	private void possiblyCreateTextLinkUsingNameInDataForTextLink(String nameInDataTextLink,
			String textIdEnding) {
		if (linkToTextDoesNotExistInRecordGroup(nameInDataTextLink)) {
			createAndAddNewTextLink(nameInDataTextLink, textIdEnding);
		}
	}

	private boolean linkToTextDoesNotExistInRecordGroup(String nameInDataTextLink) {
		return !recordGroup.containsChildOfTypeAndName(DataRecordLink.class, nameInDataTextLink);
	}

	private void createAndAddNewTextLink(String nameInDataTextLink, String textIdEnding) {
		DataRecordLink createdTextLink = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				nameInDataTextLink, "text", recordGroup.getId() + textIdEnding);
		recordGroup.addChild(createdTextLink);
		dataGroup.addChild(createdTextLink);
	}

	private void createTextsIfMissing() {
		createTextWithTextIdToExtractIfMissing("textId");
		createTextWithTextIdToExtractIfMissing("defTextId");
	}

	private void createTextWithTextIdToExtractIfMissing(String name) {
		DataRecordLink textLink = recordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				name);
		String linkId = textLink.getLinkedRecordId();
		if (textIsMissing(linkId)) {
			createTextInStorageWithTextIdDataDividerAndTextType(linkId);
		}
	}

	private boolean textIsMissing(String id) {
		try {
			RecordReader recordReader = SpiderInstanceProvider.getRecordReader();
			recordReader.readRecord(authToken, "text", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void createTextInStorageWithTextIdDataDividerAndTextType(String id) {
		DataRecordGroup text = textFactory.createTextUsingTextIdAndDataDividerId(id,
				recordGroup.getDataDivider());
		DataGroup textAsGroup = DataProvider.createGroupFromRecordGroup(text);
		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		recordCreator.createAndStoreRecord(authToken, "coraText", textAsGroup);
	}
}
