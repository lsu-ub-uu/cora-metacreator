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
package se.uu.ub.cora.metacreator.recordlink;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataCreatorHelperImp;

public class PLinkConstructor {

	DataGroup constructPLinkWithIdDataDividerPresentationOfAndMode(String id, String dataDivider,
			String presentationOf, String mode) {

		DataGroup pLink = DataGroupProvider.getDataGroupUsingNameInData("presentation");
		createAndAddRecordInfo(id, dataDivider, pLink);

		createAndAddPresentationOf(presentationOf, pLink);
		pLink.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("mode", mode));
		pLink.addAttributeByIdWithValue("type", "pRecordLink");
		return pLink;
	}

	private void createAndAddRecordInfo(String id, String dataDivider, DataGroup pCollVar) {
		DataGroup recordInfo = DataCreatorHelperImp.createRecordInfoWithIdAndDataDividerAndValidationType(id,
				dataDivider, "someValidationTypeId");
		pCollVar.addChild(recordInfo);
	}

	private void createAndAddPresentationOf(String presentationOf, DataGroup pCollVar) {
		DataRecordLink presentationOfGroup = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("presentationOf",
						"metadataRecordLink", presentationOf);
		pCollVar.addChild(presentationOfGroup);
	}

}
