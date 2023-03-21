/*
 * Copyright 2017, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.metadata;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class CollectionVariableFactoryImp implements CollectionVariableFactory {
	private DataRecordGroup recordGroup;

	@Override
	public DataRecordGroup factorCollectionVarUsingItemCollectionIdAndDataDivider(
			String itemCollectionId, String dataDivider) {
		recordGroup = DataProvider.createRecordGroupUsingNameInData("metadata");
		setBasicRecordGroupInfo(itemCollectionId + "Var", dataDivider);
		setRefCollectionLink(itemCollectionId);
		return recordGroup;
	}

	private void setBasicRecordGroupInfo(String id, String dataDivider) {
		recordGroup.addAttributeByIdWithValue("type", "collectionVariable");
		recordGroup.setId(id);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("metadataCollectionVariable");
	}

	private void setRefCollectionLink(String refCollectionId) {
		DataRecordLink itemLink = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"refCollection", "metadata", refCollectionId);
		recordGroup.addChild(itemLink);
	}
}
