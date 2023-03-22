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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;

public class CollectionItemFactoryImp implements CollectionItemFactory {
	private DataRecordGroup recordGroup;

	@Override
	public DataRecordGroup factorCollectionItemUsingItemCollectionIdAndDataDivider(
			String itemCollectionId, String dataDivider) {
		recordGroup = DataProvider.createRecordGroupUsingNameInData("metadata");
		setBasicRecordGroupInfo(itemCollectionId, dataDivider);
		setNameInData(itemCollectionId);
		return recordGroup;
	}

	private void setBasicRecordGroupInfo(String id, String dataDivider) {
		recordGroup.addAttributeByIdWithValue("type", "collectionItem");
		recordGroup.setId(id);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("genericCollectionItem");
	}

	private void setNameInData(String itemCollectionId) {
		String nameInData = createNameInDataFromId(itemCollectionId);
		DataAtomic nameInDataAtomic = DataProvider.createAtomicUsingNameInDataAndValue("nameInData",
				nameInData);
		recordGroup.addChild(nameInDataAtomic);
	}

	private String createNameInDataFromId(String itemCollectionId) {
		int lastIndexOf = itemCollectionId.lastIndexOf("Item");
		if (-1 == lastIndexOf) {
			lastIndexOf = itemCollectionId.length();
		}
		return itemCollectionId.substring(0, lastIndexOf);
	}
}
