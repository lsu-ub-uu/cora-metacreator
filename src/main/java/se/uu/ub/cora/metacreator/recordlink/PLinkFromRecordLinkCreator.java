/*
 * Copyright 2017, 2022 Uppsala University Library
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
package se.uu.ub.cora.metacreator.recordlink;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;

public class PLinkFromRecordLinkCreator implements ExtendedFunctionality {

	private String authToken;
	private String presentationOf;
	private String dataDivider;
	private PLinkConstructor constructor;
	private String id;

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup recordLinkToCreateFrom = data.dataGroup;

		constructor = new PLinkConstructor();
		setParametersForCreation(recordLinkToCreateFrom);

		possiblyCreateInputPLink();
		possiblyCreateOutputPLink();
	}

	private void setParametersForCreation(DataGroup recordLinkToCreateFrom) {
		id = DataCreatorHelper.extractIdFromDataGroup(recordLinkToCreateFrom);
		presentationOf = DataCreatorHelper.extractIdFromDataGroup(recordLinkToCreateFrom);
		dataDivider = DataCreatorHelper
				.extractDataDividerStringFromDataGroup(recordLinkToCreateFrom);
	}

	private void possiblyCreateInputPLink() {
		String pLinkId = constructIdForPLink();

		if (pLinkIsMissing(pLinkId)) {
			createPCollVarWithIdAndMode(pLinkId, "input");
		}
	}

	private boolean pLinkIsMissing(String pLinkId) {
		try {
			RecordReader reader = SpiderInstanceProvider.getRecordReader();
			reader.readRecord(authToken, "presentationRecordLink", pLinkId);
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	private void createPCollVarWithIdAndMode(String pCollVarId, String mode) {
		DataGroup pCollVar = constructor.constructPLinkWithIdDataDividerPresentationOfAndMode(
				pCollVarId, dataDivider, presentationOf, mode);
		createRecord("presentationRecordLink", pCollVar);
	}

	private String constructIdForPLink() {
		String firstPartOfId = id.substring(0, id.lastIndexOf("Link"));
		return firstPartOfId + "PLink";
	}

	private void createRecord(String recordTypeToCreate, DataGroup dataGroupToCreate) {
		RecordCreator spiderRecordCreatorOutput = SpiderInstanceProvider.getRecordCreator();
		spiderRecordCreatorOutput.createAndStoreRecord(authToken, recordTypeToCreate,
				dataGroupToCreate);
	}

	private void possiblyCreateOutputPLink() {
		String pCollVarId = constructIdForOutputPCollVar();
		if (pLinkIsMissing(pCollVarId)) {
			createPCollVarWithIdAndMode(pCollVarId, "output");
		}
	}

	private String constructIdForOutputPCollVar() {
		String firstPartOfId = id.substring(0, id.indexOf("Link"));
		return firstPartOfId + "OutputPLink";
	}
}
