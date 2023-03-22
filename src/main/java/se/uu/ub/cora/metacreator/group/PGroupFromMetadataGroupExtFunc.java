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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

public class PGroupFromMetadataGroupExtFunc implements ExtendedFunctionality {

	private String authToken;
	private String metadataId;
	private String dataDivider;
	private List<DataGroup> metadataChildReferences;
	private PGroupFactory pGroupFactory;
	private RecordCreator creator;
	private RecordReader reader;

	public static PGroupFromMetadataGroupExtFunc usingPGroupFactory(PGroupFactory pGroupFactory) {
		return new PGroupFromMetadataGroupExtFunc(pGroupFactory);
	}

	private PGroupFromMetadataGroupExtFunc(PGroupFactory pGroupFactory) {
		this.pGroupFactory = pGroupFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup dataGroup = data.dataGroup;
		creator = SpiderInstanceProvider.getRecordCreator();
		reader = SpiderInstanceProvider.getRecordReader();
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
		if (childReferencesHasChilds()) {
			possiblyCreateAndStorePGroupUsingMode("input");
			possiblyCreateAndStorePGroupUsingMode("output");
		}
	}

	private boolean childReferencesHasChilds() {
		return !metadataChildReferences.isEmpty();
	}

	private void setParametersForCreation(DataGroup dataGroup) {
		DataRecordGroup dataRecordGroup = DataProvider.createRecordGroupFromDataGroup(dataGroup);
		metadataId = dataRecordGroup.getId();
		dataDivider = dataRecordGroup.getDataDivider();
		metadataChildReferences = dataRecordGroup.getChildrenOfTypeAndName(DataGroup.class,
				"childReferences");
	}

	private void possiblyCreateAndStorePGroupUsingMode(String mode) {
		DataRecordGroup pGroup = pGroupFactory
				.factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(authToken, dataDivider,
						metadataId, mode, metadataChildReferences);

		if (pGroupNotInStorage(pGroup.getId())) {
			storeRecord(pGroup);
		}
	}

	private void storeRecord(DataRecordGroup pGroup) {
		DataGroup pGroupGroup = DataProvider.createGroupFromRecordGroup(pGroup);
		// TODO: When CORA validates with validationType change following line.
		// creator.createAndStoreRecord(authToken, "presentation", pGroupGroup);
		creator.createAndStoreRecord(authToken, "presentationGroup", pGroupGroup);
	}

	private boolean pGroupNotInStorage(String pGroupId) {
		try {
			reader.readRecord(authToken, "presentation", pGroupId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	public PGroupFactory onlyForTestGetPGroupFactory() {
		return pGroupFactory;
	}

}
