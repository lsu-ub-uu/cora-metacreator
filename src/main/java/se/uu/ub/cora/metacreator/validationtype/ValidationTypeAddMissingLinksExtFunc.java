/*
 * Copyright 2016, 2017, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.validationtype;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class ValidationTypeAddMissingLinksExtFunc implements ExtendedFunctionality {

	private DataGroup validationTypeGroup;
	private String id;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.validationTypeGroup = data.dataGroup;
		addValuesToDataGroup();
	}

	private void addValuesToDataGroup() {
		DataRecordGroup recordGroup = DataProvider
				.createRecordGroupFromDataGroup(validationTypeGroup);
		id = recordGroup.getId();
		addMissingMetadataIds();
		addMissingPresentationIds();
	}

	private void addMissingMetadataIds() {
		String linkedRecordType = "metadata";
		createAndAddLinkWithNameInDataRecordTypeAndRecordIdIfNotExisting("metadataId",
				linkedRecordType, id + "Group");
		createAndAddLinkWithNameInDataRecordTypeAndRecordIdIfNotExisting("newMetadataId",
				linkedRecordType, id + "NewGroup");

	}

	private void createAndAddLinkWithNameInDataRecordTypeAndRecordIdIfNotExisting(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		if (childWithNameInDataIsMissing(nameInData)) {
			DataRecordLink link = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
					nameInData, linkedRecordType, linkedRecordId);
			validationTypeGroup.addChild(link);
		}
	}

	private boolean childWithNameInDataIsMissing(String nameInData) {
		return !validationTypeGroup.containsChildWithNameInData(nameInData);
	}

	private void addMissingPresentationIds() {
		String linkedRecordType = "presentation";

		createAndAddLinkWithNameInDataRecordTypeAndRecordIdIfNotExisting("presentationFormId",
				linkedRecordType, id + "PGroup");
		createAndAddLinkWithNameInDataRecordTypeAndRecordIdIfNotExisting("newPresentationFormId",
				linkedRecordType, id + "NewPGroup");
	}

}
