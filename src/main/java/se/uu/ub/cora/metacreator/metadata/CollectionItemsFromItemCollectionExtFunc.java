/*
 * Copyright 2017, 2022, 2023 Uppsala University Library
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

import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class CollectionItemsFromItemCollectionExtFunc implements ExtendedFunctionality {
	private String authToken;
	private DataGroup dataGroup;
	private CollectionItemFactory collectionItemFactory;

	public static CollectionItemsFromItemCollectionExtFunc usingCollectionItemFactory(
			CollectionItemFactory collectionItemFactory) {
		return new CollectionItemsFromItemCollectionExtFunc(collectionItemFactory);
	}

	private CollectionItemsFromItemCollectionExtFunc(CollectionItemFactory collectionItemFactory) {
		this.collectionItemFactory = collectionItemFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		this.dataGroup = data.dataGroup;

		possiblyCreateItems(dataGroup);
	}

	private void possiblyCreateItems(DataGroup dataGroup) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupFromDataGroup(dataGroup);
		if (typeIsItemCollection(recordGroup)) {
			createAndPossiblyStoreItems(recordGroup);
		}
	}

	private boolean typeIsItemCollection(DataRecordGroup recordGroup) {
		Optional<String> attributeValue = recordGroup.getAttributeValue("type");
		return attributeValue.isPresent() && "itemCollection".equals(attributeValue.get());
	}

	private void createAndPossiblyStoreItems(DataRecordGroup recordGroup) {
		DataGroup itemRefs = recordGroup.getFirstGroupWithNameInData("collectionItemReferences");
		List<DataRecordLink> refs = itemRefs.getChildrenOfTypeAndName(DataRecordLink.class, "ref");
		String dataDivider = recordGroup.getDataDivider();
		for (DataRecordLink refLink : refs) {
			possiblyStoreItem(dataDivider, refLink);
		}
	}

	private void possiblyStoreItem(String dataDivider, DataRecordLink refLink) {
		String linkedRecordId = refLink.getLinkedRecordId();

		DataRecordGroup collectionItem = collectionItemFactory
				.factorCollectionItemUsingItemCollectionIdAndDataDivider(linkedRecordId,
						dataDivider);
		if (collectionItemDoesNotExistInStorage(collectionItem)) {
			storeCollectionItem(collectionItem);
		}
	}

	private boolean collectionItemDoesNotExistInStorage(DataRecordGroup collectionItem) {
		String id = collectionItem.getId();
		RecordReader reader = SpiderInstanceProvider.getRecordReader();
		try {
			reader.readRecord(authToken, "metadata", id);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void storeCollectionItem(DataRecordGroup collectionItem) {
		DataGroup dataGroupToStore = DataProvider.createGroupFromRecordGroup(collectionItem);
		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		recordCreator.createAndStoreRecord(authToken, "genericCollectionItem", dataGroupToStore);
	}

	public CollectionItemFactory onlyForTestGetCollectionItemFactory() {
		return collectionItemFactory;
	}
}
