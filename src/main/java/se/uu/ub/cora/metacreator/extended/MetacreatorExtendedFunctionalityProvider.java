/*
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
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.metacreator.collection.CollectionVarFromItemCollectionCreator;
import se.uu.ub.cora.metacreator.collection.ItemCollectionCompleter;
import se.uu.ub.cora.metacreator.collection.ItemCollectionCreator;
import se.uu.ub.cora.metacreator.collection.PCollVarFromCollectionVarCreator;
import se.uu.ub.cora.metacreator.collectionitem.CollectionItemCompleter;
import se.uu.ub.cora.metacreator.collectionitem.CollectionItemCreator;
import se.uu.ub.cora.metacreator.group.GroupCompleter;
import se.uu.ub.cora.metacreator.recordlink.PLinkFromRecordLinkCreator;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeCreator;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeMetaCompleter;
import se.uu.ub.cora.metacreator.recordtype.SearchFromRecordTypeCreator;
import se.uu.ub.cora.metacreator.search.SearchCompleter;
import se.uu.ub.cora.metacreator.search.SearchCreator;
import se.uu.ub.cora.metacreator.text.PVarFromTextVarCreator;
import se.uu.ub.cora.metacreator.text.TextVarMetaCompleter;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.BaseExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class MetacreatorExtendedFunctionalityProvider extends BaseExtendedFunctionalityProvider {

	private static final String CORA_TEXT = "coraText";

	public MetacreatorExtendedFunctionalityProvider(SpiderDependencyProvider dependencyProvider) {
		super(dependencyProvider);
	}

	@Override
	public List<ExtendedFunctionality> getFunctionalityForCreateBeforeMetadataValidation(
			String recordType) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeMetadataValidation(
				recordType);
		if ("metadataTextVariable".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(TextVarMetaCompleter.forImplementingTextType(CORA_TEXT));
		}
		if ("recordType".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new RecordTypeMetaCompleter());
			list.add(RecordTypeCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataCollectionItem".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new CollectionItemCompleter(CORA_TEXT));
			list.add(CollectionItemCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataItemCollection".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(ItemCollectionCompleter.forImplementingTextType(CORA_TEXT));
			list.add(ItemCollectionCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("search".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(SearchCompleter.forImplementingTextType(CORA_TEXT));
			list.add(SearchCreator.forImplementingTextType(CORA_TEXT));
		}
		if ("metadataGroup".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new GroupCompleter());
		}
		return list;
	}

	protected List<ExtendedFunctionality> ensureListIsRealList(List<ExtendedFunctionality> list) {
		if (Collections.emptyList().equals(list)) {
			return new ArrayList<>();
		}
		return list;
	}

	@Override
	public List<ExtendedFunctionality> getFunctionalityForCreateBeforeReturn(String recordType) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeReturn(recordType);
		if ("metadataTextVariable".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new PVarFromTextVarCreator());
		}
		if ("recordType".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new SearchFromRecordTypeCreator());
		}
		if ("metadataItemCollection".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new CollectionVarFromItemCollectionCreator());
		}
		if ("metadataCollectionVariable".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new PCollVarFromCollectionVarCreator());
		}
		if ("metadataRecordLink".equals(recordType)) {
			list = ensureListIsRealList(list);
			list.add(new PLinkFromRecordLinkCreator());
		}
		return list;
	}
}
