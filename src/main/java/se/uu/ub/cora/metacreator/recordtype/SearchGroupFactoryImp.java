/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class SearchGroupFactoryImp {

	public DataRecordGroup factorUsingDataDividerAndRecordTypeIdToSearchIn(String dataDivider,
			String recordTypeIdToSearchIn) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupUsingNameInData("search");
		setBasicRecordGroupInfo(dataDivider, recordTypeIdToSearchIn, recordGroup);
		DataRecordLink typeToSearchIn = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"recordTypeToSearchIn", "recordType", recordTypeIdToSearchIn);
		typeToSearchIn.setRepeatId("0");
		recordGroup.addChild(typeToSearchIn);
		return recordGroup;
	}

	private void setBasicRecordGroupInfo(String dataDivider, String recordTypeIdToSearchIn,
			DataRecordGroup recordGroup) {
		recordGroup.setId(recordTypeIdToSearchIn + "Search");
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("search");
	}

	// private String recordType;
	//
	// public SearchGroupFactoryImp(String id, String dataDivider, String recordType) {
	// super(id, dataDivider);
	// this.recordType = recordType;
	// }
	//
	// public static SearchGroupFactoryImp withIdIdAndDataDividerAndRecordType(String id,
	// String dataDivider, String recordType) {
	// return new SearchGroupFactoryImp(id, dataDivider, recordType);
	// }
	//
	// @Override
	// public DataGroup factorDataGroup(String childReferenceId) {
	// super.factorDataGroup(childReferenceId);
	// addChildren();
	// return topLevelDataGroup;
	// }
	//
	// private void addChildren() {
	// addLinkChildWithNameInDataLinkedTypeAndLinkedIdAndRepeatId("recordTypeToSearchIn",
	// "recordType", recordType, "0");
	// addLinkChildWithNameInDataLinkedTypeAndLinkedId("metadataId", "metadataGroup",
	// "autocompleteSearchGroup");
	// addLinkChildWithNameInDataLinkedTypeAndLinkedId("presentationId", "presentationGroup",
	// "autocompleteSearchPGroup");
	//
	// // addTexts();
	//
	// topLevelDataGroup.addChild(DataAtomicProvider
	// .getDataAtomicUsingNameInDataAndValue("searchGroup", "autocomplete"));
	// }
	//
	// private void addLinkChildWithNameInDataLinkedTypeAndLinkedId(String nameInData,
	// String linkedRecordType, String linkedRecordId) {
	// DataRecordLink recordTypeToSearchIn = createLinkChildWithNameInDataAndLinkedTypeAndLinkedId(
	// nameInData, linkedRecordType, linkedRecordId);
	// topLevelDataGroup.addChild(recordTypeToSearchIn);
	// }
	//
	// private void addLinkChildWithNameInDataLinkedTypeAndLinkedIdAndRepeatId(String nameInData,
	// String linkedRecordType, String linkedRecordId, String repeatId) {
	// DataRecordLink linkChild = createLinkChildWithNameInDataAndLinkedTypeAndLinkedId(nameInData,
	// linkedRecordType, linkedRecordId);
	// linkChild.setRepeatId(repeatId);
	// topLevelDataGroup.addChild(linkChild);
	// }
	//
	// private DataRecordLink createLinkChildWithNameInDataAndLinkedTypeAndLinkedId(String
	// nameInData,
	// String linkedRecordType, String linkedRecordId) {
	// return DataRecordLinkProvider.getDataRecordLinkAsLinkUsingNameInDataTypeAndId(nameInData,
	// linkedRecordType, linkedRecordId);
	// }
	//
	// // private void addTexts() {
	// // addLinkChildWithNameInDataLinkedTypeAndLinkedId("textId", "coraText", id + "Text");
	// // addLinkChildWithNameInDataLinkedTypeAndLinkedId("defTextId", "coraText", id + "DefText");
	// // }
	//
	// @Override
	// DataGroup createTopLevelDataGroup() {
	// return DataGroupProvider.getDataGroupUsingNameInData("search");
	// }
	//
	// @Override
	// void addAttributeType() {
	// // not implemented for search
	// }
	//
	// @Override
	// protected void addChildReferencesWithChildId(String refRecordInfoId) {
	// // not implemented for search
	// }
}
