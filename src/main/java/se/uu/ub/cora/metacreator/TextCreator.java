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

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class TextCreator implements ExtendedFunctionality {
	private TextFactory textFactory;
	private String authToken;
	private DataRecordGroup recordGroup;

	public static TextCreator usingTextFactory(TextFactory textFactory) {
		return new TextCreator(textFactory);
	}

	private TextCreator(TextFactory textFactory) {
		this.textFactory = textFactory;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		authToken = data.authToken;
		recordGroup = DataProvider.createRecordGroupFromDataGroup(data.dataGroup);

		// RecordCreatorHelper recordCreatorHelper = RecordCreatorHelper
		// .withAuthTokenDataGroupAnd"text"(authToken, dataGroup, "coraText");
		createTextsIfMissing();
	}

	// From here
	private void createTextsIfMissing() {
		createTextWithTextIdToExtractIfMissing("textId");
		createTextWithTextIdToExtractIfMissing("defTextId");
	}

	private void createTextWithTextIdToExtractIfMissing(String name) {
		// DataRecordLink textLink = (DataRecordLink) recordGroup
		// .getFirstChildWithNameInData(name);
		DataRecordLink textLink = recordGroup
				.getFirstChildOfTypeWithNameAndAttributes(DataRecordLink.class, name);
		String linkId = textLink.getLinkedRecordId();
		// String textId = textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
		if (textIsMissing(linkId)) {
			createTextWithTextId(linkId);
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

	private void createTextWithTextId(String id) {
		String dataDivider = "";
		// String dataDivider = DataCreatorHelperImp.extractDataDividerIdFromDataGroup(dataGroup);
		// createTextInStorageWithTextIdDataDividerAndTextType(textId, dataDivider, "text");
		createTextInStorageWithTextIdDataDividerAndTextType(id, dataDivider);
	}

	private void createTextInStorageWithTextIdDataDividerAndTextType(String id,
			String dataDivider) {

		DataRecordGroup textGroup = textFactory.createTextUsingTextIdAndDataDividerId("someTextId",
				"someDataDivider");

		// RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		// recordCreator.createAndStoreRecord(authToken, "text", textGroup);
	}

}
