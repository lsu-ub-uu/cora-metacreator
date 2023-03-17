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

package se.uu.ub.cora.metacreator.text;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;

public final class TextFactoryImp implements TextFactory {
	private DataRecordGroup textGroup;

	@Override
	public DataRecordGroup createTextUsingTextIdAndDataDividerId(String id, String dataDivider) {
		textGroup = DataProvider.createRecordGroupUsingNameInData("text");
		setBasicRecordGroupInfo(id, dataDivider);
		createTextPartWithTextIdTypeLangText("default", "sv", "Text för:" + id);
		createTextPartWithTextIdTypeLangText("alternative", "en", "Text for:" + id);
		return textGroup;
	}

	private void setBasicRecordGroupInfo(String id, String dataDivider) {
		textGroup.setId(id);
		textGroup.setDataDivider(dataDivider);
		textGroup.setValidationType("coraText");
	}

	private void createTextPartWithTextIdTypeLangText(String type, String lang, String text) {
		DataGroup textPart = DataProvider.createGroupUsingNameInData("textPart");
		textPart.addAttributeByIdWithValue("type", type);
		textPart.addAttributeByIdWithValue("lang", lang);
		DataAtomic textChild = DataProvider.createAtomicUsingNameInDataAndValue("text", text);
		textPart.addChild(textChild);
		textGroup.addChild(textPart);
	}
}
