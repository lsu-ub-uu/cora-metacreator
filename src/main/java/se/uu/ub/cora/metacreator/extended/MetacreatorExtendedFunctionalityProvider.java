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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.TextCreator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class MetacreatorExtendedFunctionalityProvider implements ExtendedFunctionalityFactory {

	private static final String RECORD_TYPE = "recordType";
	private static final String CORA_TEXT = "coraText";

	public MetacreatorExtendedFunctionalityProvider(SpiderDependencyProvider dependencyProvider) {
		// super(dependencyProvider);
	}

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public List<ExtendedFunctionality> getFunctionalityForCreateBeforeMetadataValidation(
	// String recordTypeId) {
	// List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeMetadataValidation(
	// recordTypeId);
	// String parentId = getParentIdIfExists(recordTypeId);
	// if ("metadataTextVariable".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// addCompleterAndCreatorOfCoraTextToList(list);
	// }
	// if (RECORD_TYPE.equals(recordTypeId)) {
	// list = ensureListExists(list);
	// list.add(new RecordTypeMetaCompleter());
	// list.add(RecordTypeCreator.forImplementingTextType(CORA_TEXT));
	// }
	// if ("metadataCollectionItem".equals(parentId)) {
	// list = ensureListExists(list);
	// addCompleterAndCreatorOfCoraTextToList(list);
	// }
	// if ("metadataItemCollection".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// list.add(MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
	// new MetadataCompleterImp(), CORA_TEXT));
	// list.add(ItemCollectionCreator.forImplementingTextType(CORA_TEXT));
	// }
	// if ("search".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// list.add(MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
	// new MetadataCompleterImp(), CORA_TEXT));
	// list.add(SearchCreator.forImplementingTextType(CORA_TEXT));
	// }
	// if ("metadataGroup".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// list.add(GroupCompleter.forTextLinkedRecordType(CORA_TEXT));
	// list.add(TextCreator.forImplementingTextType(CORA_TEXT));
	// }GroupExtendedFunctionalityFactory
	// if ("metadataRecordLink".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// list.add(RecordLinkCompleter.forTextLinkedRecordType(CORA_TEXT));
	// list.add(TextCreator.forImplementingTextType(CORA_TEXT));
	// }
	// if ("metadataCollectionVariable".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// list.add(CollectionVariableCompleter.forTextLinkedRecordType(CORA_TEXT));
	// list.add(TextCreator.forImplementingTextType(CORA_TEXT));
	// }
	// if ("metadataNumberVariable".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// addCompleterAndCreatorOfCoraTextToList(list);
	// }
	// if ("permissionRole".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// addCompleterAndCreatorOfCoraTextToList(list);
	// }
	// if ("permissionRule".equals(recordTypeId)) {
	// list = ensureListExists(list);
	// addCompleterAndCreatorOfCoraTextToList(list);
	// }

	// return list;
	// }

	private void addCompleterAndCreatorOfCoraTextToList(Collection<ExtendedFunctionality> list) {
		list.add(MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
				new MetadataCompleterImp(), CORA_TEXT));
		list.add(TextCreator.forImplementingTextType(CORA_TEXT));
	}

	// private String getParentIdIfExists(String recordType) {
	// DataGroup currentRecordType = getRecordTypeDefinition(recordType);
	// if (hasParent(currentRecordType)) {
	// return extractParentId(currentRecordType);
	// }
	// return "";
	// }

	private String extractParentId(DataGroup currentRecordType) {
		DataGroup parentGroup = currentRecordType.getFirstGroupWithNameInData("parentId");
		return parentGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	// private DataGroup getRecordTypeDefinition(String recordType) {
	// RecordStorage recordStorage = dependencyProvider.getRecordStorage();
	// return recordStorage.read(RECORD_TYPE, recordType);
	// }

	private boolean hasParent(DataGroup currentRecordType) {
		return currentRecordType.containsChildWithNameInData("parentId");
	}

	protected List<ExtendedFunctionality> ensureListExists(List<ExtendedFunctionality> list) {
		if (Collections.emptyList().equals(list)) {
			return new ArrayList<>();
		}
		return list;
	}

	// @Override
	// public List<ExtendedFunctionality> getFunctionalityForCreateBeforeReturn(String recordType) {
	// List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeReturn(recordType);
	// if ("metadataTextVariable".equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new PVarFromTextVarCreator());
	// }
	// if (RECORD_TYPE.equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new SearchFromRecordTypeCreator());
	// }
	// if ("metadataItemCollection".equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new CollectionVarFromItemCollectionCreator());
	// }
	// if ("metadataCollectionVariable".equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new PCollVarFromCollectionVarCreator());
	// }
	// if ("metadataRecordLink".equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new PLinkFromRecordLinkCreator());
	// }
	// if ("metadataGroup".equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new PGroupFromMetadataGroupCreator());
	// }
	// if ("metadataNumberVariable".equals(recordType)) {
	// list = ensureListExists(list);
	// list.add(new PNumVarFromNumberVarCreator());
	// }
	// return list;
	// }

}
