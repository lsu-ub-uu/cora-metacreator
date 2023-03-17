/*
 * Copyright 2020 Uppsala University Library
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

import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_RETURN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.metacreator.permission.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.presentation.PCollVarFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PCollVarFromCollectionVarCreator;
import se.uu.ub.cora.metacreator.text.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.text.TextCreator;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;
import se.uu.ub.cora.spider.recordtype.RecordTypeHandler;

public class CollectionExtendedFunctionalityFactory implements ExtendedFunctionalityFactory {

	private static final String METADATA_COLLECTION_VARIABLE = "metadataCollectionVariable";
	private static final String METADATA_ITEM_COLLECTION = "metadataItemCollection";
	private static final String CORA_TEXT = "coraText";
	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();
	private SpiderDependencyProvider dependencyProvider;

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataCollectionItem");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, METADATA_ITEM_COLLECTION);
		createContext(CREATE_BEFORE_METADATA_VALIDATION, METADATA_COLLECTION_VARIABLE);
		createContext(CREATE_BEFORE_RETURN, METADATA_ITEM_COLLECTION);
		createContext(CREATE_BEFORE_RETURN, METADATA_COLLECTION_VARIABLE);
		RecordTypeHandler recordTypeHandler = dependencyProvider
				.getRecordTypeHandler("metadataCollectionItem");
		List<RecordTypeHandler> implementingRecordTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();
		for (RecordTypeHandler implementing : implementingRecordTypeHandlers) {
			createContext(CREATE_BEFORE_METADATA_VALIDATION, implementing.getRecordTypeId());
		}
	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		return contexts;
	}

	private void createContext(ExtendedFunctionalityPosition position, String recordType) {
		contexts.add(new ExtendedFunctionalityContext(position, recordType, 0));
	}

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String recordType) {
		if (CREATE_BEFORE_RETURN == position) {
			return createBeforeReturn(recordType);
		}
		return createBeforeMetadataValidation(recordType);
	}

	private List<ExtendedFunctionality> createBeforeReturn(String recordType) {
		if (METADATA_COLLECTION_VARIABLE.equals(recordType)) {
			return createBeforeReturnForMetadataCollectionVariable();
		}
		return createBeforeReturnForMetadataItemCollection();
	}

	private List<ExtendedFunctionality> createBeforeReturnForMetadataCollectionVariable() {
		PCollVarFactoryImp pCollVarFactory = new PCollVarFactoryImp();
		return Collections.singletonList(
				PCollVarFromCollectionVarCreator.usingPCollVarFactory(pCollVarFactory));
	}

	private List<ExtendedFunctionality> createBeforeReturnForMetadataItemCollection() {
		return Collections.singletonList(new CollectionVarFromItemCollectionCreator());
	}

	private List<ExtendedFunctionality> createBeforeMetadataValidation(String recordType) {
		List<ExtendedFunctionality> asList = new ArrayList<>();
		asList.add(createMetadataGroupTextCompleter());
		asList.add(createBeforeMetadataValidationForRecordType(recordType));
		return asList;
	}

	private MetadataGroupTextCompleter createMetadataGroupTextCompleter() {
		return MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
				new MetadataCompleterImp(), CORA_TEXT);
	}

	private ExtendedFunctionality createBeforeMetadataValidationForRecordType(String recordType) {
		if (METADATA_ITEM_COLLECTION.equals(recordType)) {
			return new ItemCollectionCreator();
		}
		return TextCreator.usingTextFactory(new TextFactoryImp());
	}

}
