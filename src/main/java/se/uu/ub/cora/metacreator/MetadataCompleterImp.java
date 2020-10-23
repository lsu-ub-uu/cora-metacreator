/*
 * Copyright 2017 Uppsala University Library
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
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;

public class MetadataCompleterImp implements MetadataCompleter {

	private static final String DEF_TEXT_ID = "defTextId";
	private static final String TEXT_ID = "textId";
	private String id = "";

	public void completeDataGroupWithTexts(DataGroup metadataGroup) {
		id = extractIdFromMetadataGroup(metadataGroup);
		possiblyCompleteTextId(metadataGroup);
		possiblyCompleteDefTextId(metadataGroup);
	}

	private void possiblyCompleteDefTextId(DataGroup metadataGroup) {
		if (!metadataGroup.containsChildWithNameInData(DEF_TEXT_ID)) {
			metadataGroup.addChild(DataAtomicProvider
					.getDataAtomicUsingNameInDataAndValue(DEF_TEXT_ID, id + "DefText"));
		}
	}

	private void possiblyCompleteTextId(DataGroup metadataGroup) {
		if (!metadataGroup.containsChildWithNameInData(TEXT_ID)) {
			metadataGroup.addChild(
					DataAtomicProvider.getDataAtomicUsingNameInDataAndValue(TEXT_ID, id + "Text"));
		}
	}

	private String extractIdFromMetadataGroup(DataGroup metadataGroup) {
		DataGroup recordInfo = metadataGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	public void completeDataGroupWithLinkedTexts(DataGroup metadataGroup, String textRecordType) {
		id = extractIdFromMetadataGroup(metadataGroup);
		possiblyAddLinkedTextWithNameInDataTextIdAndTextRecordType(metadataGroup, TEXT_ID,
				id + "Text", textRecordType);
		possiblyAddLinkedTextWithNameInDataTextIdAndTextRecordType(metadataGroup, DEF_TEXT_ID,
				id + "DefText", textRecordType);
	}

	private void possiblyAddLinkedTextWithNameInDataTextIdAndTextRecordType(DataGroup metadataGroup,
			String textNameInData, String textId, String textRecordType) {
		if (!metadataGroup.containsChildWithNameInData(textNameInData)) {
			addLinkedTextWithNameInDataTextIdAndTextRecordType(metadataGroup, textNameInData,
					textId, textRecordType);
		}
	}

	private void addLinkedTextWithNameInDataTextIdAndTextRecordType(DataGroup metadataGroup,
			String textNameInData, String textId, String textRecordType) {
		DataGroup textIdGroup = createLinkedTextWithNameInDataLinkedIdAndLinkedType(textNameInData,
				textId, textRecordType);
		metadataGroup.addChild(textIdGroup);
	}

	private DataGroup createLinkedTextWithNameInDataLinkedIdAndLinkedType(String nameInData,
			String textId, String textRecordType) {
		DataRecordLink textIdGroup = DataRecordLinkProvider
				.getDataRecordLinkUsingNameInData(nameInData);
		textIdGroup.addChild(DataAtomicProvider
				.getDataAtomicUsingNameInDataAndValue("linkedRecordType", textRecordType));
		textIdGroup.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("linkedRecordId", textId));
		return textIdGroup;
	}

}
