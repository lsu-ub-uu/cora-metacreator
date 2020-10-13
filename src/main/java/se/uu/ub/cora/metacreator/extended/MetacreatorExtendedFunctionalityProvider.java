/*
 * Copyright 2018 Uppsala University Library
 * Copyright 2016 Olov McKie
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

package se.uu.ub.cora.metacreator.extended;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.TextCreator;
import se.uu.ub.cora.metacreator.collection.CollectionVarFromItemCollectionCreator;
import se.uu.ub.cora.metacreator.collection.CollectionVariableCompleter;
import se.uu.ub.cora.metacreator.collection.ItemCollectionCompleter;
import se.uu.ub.cora.metacreator.collection.ItemCollectionCreator;
import se.uu.ub.cora.metacreator.collection.PCollVarFromCollectionVarCreator;
import se.uu.ub.cora.metacreator.collectionitem.CollectionItemCompleter;
import se.uu.ub.cora.metacreator.group.GroupCompleter;
import se.uu.ub.cora.metacreator.group.PGroupFromMetadataGroupCreator;
import se.uu.ub.cora.metacreator.numbervar.NumberVarCompleter;
import se.uu.ub.cora.metacreator.numbervar.PNumVarFromNumberVarCreator;
import se.uu.ub.cora.metacreator.recordlink.PLinkFromRecordLinkCreator;
import se.uu.ub.cora.metacreator.recordlink.RecordLinkCompleter;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeCreator;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeMetaCompleter;
import se.uu.ub.cora.metacreator.recordtype.SearchFromRecordTypeCreator;
import se.uu.ub.cora.metacreator.search.SearchCompleter;
import se.uu.ub.cora.metacreator.search.SearchCreator;
import se.uu.ub.cora.metacreator.textvar.PVarFromTextVarCreator;
import se.uu.ub.cora.metacreator.textvar.TextVarCompleter;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.BaseExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;
import se.uu.ub.cora.storage.RecordStorage;

public class MetacreatorExtendedFunctionalityProvider extends BaseExtendedFunctionalityProvider {

	private static final String RECORD_TYPE = "recordType";
	private static final String CORA_TEXT = "coraText";

	public MetacreatorExtendedFunctionalityProvider(SpiderDependencyProvider dependencyProvider) {
		super(dependencyProvider);
	}

	@Override
	public List<ExtendedFunctionality> getFunctionalityForCreateBeforeMetadataValidation(
			String recordTypeId) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeMetadataValidation(
				recordTypeId);
		String parentId = getParentIdIfExists(recordTypeId);
		if ("metadataTextVariable".equals(recordTypeId)) {
			list.add(TextVarCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(TextCreator.forImplementingTextType(CORA_TEXT));
		}
		if (RECORD_TYPE.equals(recordTypeId)) {
			list.add(new RecordTypeMetaCompleter());
			list.add(RecordTypeCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataCollectionItem".equals(parentId)) {
			list.add(CollectionItemCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(TextCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataItemCollection".equals(recordTypeId)) {
			list.add(ItemCollectionCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(ItemCollectionCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("search".equals(recordTypeId)) {
			list.add(SearchCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(SearchCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataGroup".equals(recordTypeId)) {
			list.add(GroupCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(TextCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataRecordLink".equals(recordTypeId)) {
			list.add(RecordLinkCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(TextCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataCollectionVariable".equals(recordTypeId)) {
			list.add(CollectionVariableCompleter.forTextLinkedRecordType(CORA_TEXT));
			list.add(TextCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataNumberVariable".equals(recordTypeId)) {
			list.add(NumberVarCompleter.forImplementingTextType(CORA_TEXT));
			list.add(TextCreator.forImplementingTextType(CORA_TEXT));
		}

		return list;
	}

	private String getParentIdIfExists(String recordType) {
		DataGroup currentRecordType = getRecordTypeDefinition(recordType);
		if (hasParent(currentRecordType)) {
			return extractParentId(currentRecordType);
		}
		return "";
	}

	private String extractParentId(DataGroup currentRecordType) {
		DataGroup parentGroup = currentRecordType.getFirstGroupWithNameInData("parentId");
		return parentGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private DataGroup getRecordTypeDefinition(String recordType) {
		RecordStorage recordStorage = dependencyProvider.getRecordStorage();
		return recordStorage.read(RECORD_TYPE, recordType);
	}

	private boolean hasParent(DataGroup currentRecordType) {
		return currentRecordType.containsChildWithNameInData("parentId");
	}

	// protected List<ExtendedFunctionality> ensureListExists(List<ExtendedFunctionality> list) {
	// if (Collections.emptyList().equals(list)) {
	// return new ArrayList<>();
	// }
	// return list;
	// }

	@Override
	public List<ExtendedFunctionality> getFunctionalityForCreateBeforeReturn(String recordType) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeReturn(recordType);
		if ("metadataTextVariable".equals(recordType)) {
			list.add(new PVarFromTextVarCreator());
		}
		if (RECORD_TYPE.equals(recordType)) {
			list.add(new SearchFromRecordTypeCreator());
		}
		if ("metadataItemCollection".equals(recordType)) {
			list.add(new CollectionVarFromItemCollectionCreator());
		}
		if ("metadataCollectionVariable".equals(recordType)) {
			list.add(new PCollVarFromCollectionVarCreator());
		}
		if ("metadataRecordLink".equals(recordType)) {
			list.add(new PLinkFromRecordLinkCreator());
		}
		if ("metadataGroup".equals(recordType)) {
			list.add(new PGroupFromMetadataGroupCreator());
		}
		if ("metadataNumberVariable".equals(recordType)) {
			list.add(new PNumVarFromNumberVarCreator());
		}
		return list;
	}
}
