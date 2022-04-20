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
package se.uu.ub.cora.metacreator.group;

import java.util.List;

import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

public class PGroupFromMetadataGroupCreator implements ExtendedFunctionality {

	private String authToken;
	private String metadataId;
	private String dataDivider;
	private List<DataChild> metadataChildReferences;
	protected PGroupConstructor constructor;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup dataGroup = data.dataGroup;
		if (pGroupsShouldBeCreated(dataGroup)) {
			tryToCreatePGroups(dataGroup);
		}
	}

	private boolean pGroupsShouldBeCreated(DataGroup dataGroup) {
		return !dataGroup.containsChildWithNameInData("excludePGroupCreation") || "false"
				.equals(dataGroup.getFirstAtomicValueWithNameInData("excludePGroupCreation"));
	}

	private void tryToCreatePGroups(DataGroup dataGroup) {
		setParametersForCreation(dataGroup);
		possiblyCreateInputGroup();
		possiblyCreateOutputPGroup();
	}

	private void setParametersForCreation(DataGroup dataGroup) {
		PChildRefConstructorFactory constructorFactory = new PChildRefConstructorFactoryImp();
		constructor = PGroupConstructor.usingAuthTokenAndPChildRefConstructorFactory(authToken,
				constructorFactory);
		metadataId = DataCreatorHelper.extractIdFromDataGroup(dataGroup);
		dataDivider = DataCreatorHelper.extractDataDividerStringFromDataGroup(dataGroup);
		metadataChildReferences = dataGroup.getFirstGroupWithNameInData("childReferences")
				.getChildren();
	}

	private void possiblyCreateInputGroup() {
		String id = getIdForInputPGroup();
		possiblyConstructAndCreatePGroupWithIdAndMode(id, "input");
	}

	private void possiblyCreateOutputPGroup() {
		String outputId = getIdForOutputPGroup();
		possiblyConstructAndCreatePGroupWithIdAndMode(outputId, "output");
	}

	private String getIdForInputPGroup() {
		return metadataId.substring(0, metadataId.indexOf("Group")) + "PGroup";
	}

	private void possiblyConstructAndCreatePGroupWithIdAndMode(String id, String mode) {
		if (pGroupIsMissing(id)) {
			constructAndCreatePGroupWithIdAndMode(id, mode);
		}
	}

	private boolean pGroupIsMissing(String pGroupId) {
		try {
			RecordReader reader = SpiderInstanceProvider.getRecordReader();
			reader.readRecord(authToken, "presentationGroup", pGroupId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private void constructAndCreatePGroupWithIdAndMode(String id, String mode) {
		try {
			DataGroup inputPGroup = constructor
					.constructPGroupWithIdDataDividerPresentationOfChildrenAndMode(id, dataDivider,
							metadataId, metadataChildReferences, mode);
			createRecord("presentationGroup", inputPGroup);
		} catch (DataException e) {
			// do nothing
		}
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}

	private String getIdForOutputPGroup() {
		return metadataId.substring(0, metadataId.indexOf("Group")) + "OutputPGroup";
	}
}
