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
package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataCreatorHelperImp;

public class CollectionVariableConstructor {

	protected DataGroup collectionVar;

	public DataGroup constructCollectionVarWithIdNameInDataDataDividerAndRefCollection(String id,
			String nameInData, String dataDivider, String refCollection) {

		return createDataGroup(id, nameInData, dataDivider, refCollection);
	}

	private DataGroup createDataGroup(String id, String nameInData, String dataDivider,
			String refCollection) {
		createDataGroup();
		addChildren(id, nameInData, dataDivider, refCollection);
		return collectionVar;
	}

	private void createDataGroup() {
		collectionVar = DataGroupProvider.getDataGroupUsingNameInData("metadata");
		collectionVar.addAttributeByIdWithValue("type", "collectionVariable");
	}

	private void addChildren(String id, String nameInData, String dataDivider,
			String refCollection) {
		createAndAddRecordInfo(id, dataDivider, collectionVar);

		addAtomicValues(nameInData);
		addRefCollection(refCollection);
	}

	private void addAtomicValues(String nameInData) {
		collectionVar.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("nameInData", nameInData));
	}

	private void createAndAddRecordInfo(String id, String dataDivider, DataGroup collectionVar) {
		DataGroup recordInfo = DataCreatorHelperImp.createRecordInfoWithIdAndDataDividerAndValidationType(id,
				dataDivider, "someValidationTypeId");
		collectionVar.addChild(recordInfo);
	}

	private void addRefCollection(String refCollection) {
		DataRecordLink refCollectionGroup = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("refCollection",
						"metadataItemCollection", refCollection);
		collectionVar.addChild(refCollectionGroup);
	}

}
