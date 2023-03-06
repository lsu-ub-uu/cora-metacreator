/*
 * Copyright 2016, 2023 Olov McKie
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

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;

public final class TextConstructor {
	private DataCreatorHelper dataCreatorHelper;

	public static TextConstructor usingDataCreatorHelper(DataCreatorHelper dataCreatorHelper) {
		return new TextConstructor(dataCreatorHelper);
	}

	private TextConstructor(DataCreatorHelper dataCreatorHelper) {
		this.dataCreatorHelper = dataCreatorHelper;
	}

	public DataGroup createTextUsingTextIdAndDataDividerIdAndValidationTypeId(String textId,
			String dataDividerId, String validationTypeId) {

		DataGroup textGroup = DataGroupProvider.getDataGroupUsingNameInData("text");

		DataGroup recordInfo = createRecordInfoWithIdAndDataDividerRecordId(textId, dataDividerId,
				validationTypeId);
		textGroup.addChild(recordInfo);

		DataGroup textPartSv = createTextPartWithTextIdTypeLangText("default", "sv",
				"Text för:" + textId);
		textGroup.addChild(textPartSv);
		DataGroup textPartEn = createTextPartWithTextIdTypeLangText("alternative", "en",
				"Text for:" + textId);
		textGroup.addChild(textPartEn);
		return textGroup;
	}

	private DataGroup createTextPartWithTextIdTypeLangText(String type, String lang, String text) {
		DataGroup textPart = DataGroupProvider.getDataGroupUsingNameInData("textPart");
		textPart.addAttributeByIdWithValue("type", type);
		textPart.addAttributeByIdWithValue("lang", lang);
		textPart.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("text", text));
		return textPart;
	}

	private DataGroup createRecordInfoWithIdAndDataDividerRecordId(String textId,
			String dataDividerId, String validationTypeId) {
		return dataCreatorHelper.createRecordInfoWithIdAndDataDividerAndValidationType(textId,
				dataDividerId, validationTypeId);
	}

	public DataCreatorHelper onlyForTestGetDataCreatorHelper() {
		return dataCreatorHelper;
	}

}
