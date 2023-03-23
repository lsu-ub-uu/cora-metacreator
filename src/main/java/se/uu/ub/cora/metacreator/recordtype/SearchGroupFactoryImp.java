/*
 * Copyright 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class SearchGroupFactoryImp implements SearchGroupFactory {

	@Override
	public DataRecordGroup factorUsingRecordTypeIdToSearchInAndDataDivider(String recordTypeIdToSearchIn,
			String dataDivider) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupUsingNameInData("search");
		setBasicRecordGroupInfo(dataDivider, recordTypeIdToSearchIn, recordGroup);
		setRecordTypeToSearchInLink(recordTypeIdToSearchIn, recordGroup);
		setMetadataIdLink(recordGroup);
		setPresentationIdLink(recordGroup);
		setAtomicSearchGroup(recordGroup);
		return recordGroup;
	}

	private void setBasicRecordGroupInfo(String dataDivider, String recordTypeIdToSearchIn,
			DataRecordGroup recordGroup) {
		recordGroup.setId(recordTypeIdToSearchIn + "Search");
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("search");
	}

	private void setRecordTypeToSearchInLink(String recordTypeIdToSearchIn,
			DataRecordGroup recordGroup) {
		DataRecordLink link = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"recordTypeToSearchIn", "recordType", recordTypeIdToSearchIn);
		recordGroup.addChild(link);
		link.setRepeatId("0");
	}

	private void setMetadataIdLink(DataRecordGroup recordGroup) {
		DataRecordLink link = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId("metadataId",
				"metadata", "autocompleteSearchGroup");
		recordGroup.addChild(link);
	}

	private void setPresentationIdLink(DataRecordGroup recordGroup) {
		DataRecordLink link = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"presentationId", "presentation", "autocompleteSearchPGroup");
		recordGroup.addChild(link);
	}

	private void setAtomicSearchGroup(DataRecordGroup recordGroup) {
		DataAtomic atomic = DataProvider.createAtomicUsingNameInDataAndValue("searchGroup",
				"autocomplete");
		recordGroup.addChild(atomic);
	}
}
