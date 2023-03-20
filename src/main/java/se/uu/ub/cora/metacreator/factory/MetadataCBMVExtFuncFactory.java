/*
 * Copyright 2020, 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.factory;

import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.metacreator.collection.ItemCollectionCreator;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactory;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;
import se.uu.ub.cora.spider.recordtype.RecordTypeHandler;

public class MetadataCBMVExtFuncFactory implements ExtendedFunctionalityFactory {

	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();
	private SpiderDependencyProvider dependencyProvider;

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataGroup");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataTextVariable");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataNumberVariable");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataCollectionVariable");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataRecordLink");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataItemCollection");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataCollectionItem");
		RecordTypeHandler recordTypeHandler = dependencyProvider
				.getRecordTypeHandler("metadataCollectionItem");
		List<RecordTypeHandler> implementingRecordTypeHandlers = recordTypeHandler
				.getImplementingRecordTypeHandlers();
		for (RecordTypeHandler implementing : implementingRecordTypeHandlers) {
			createContext(CREATE_BEFORE_METADATA_VALIDATION, implementing.getRecordTypeId());
		}
	}

	private void createContext(ExtendedFunctionalityPosition position, String recordType) {
		contexts.add(new ExtendedFunctionalityContext(position, recordType, 0));
	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		return contexts;
	}

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String recordType) {
		List<ExtendedFunctionality> functionalities = new ArrayList<>();
		functionalities.add(createTextAndDefTextExtFunc());
		functionalities.add(new ItemCollectionCreator());
		return functionalities;
	}

	private TextAndDefTextExtFunc createTextAndDefTextExtFunc() {
		TextFactory textFactory = new TextFactoryImp();
		return TextAndDefTextExtFunc.usingTextFactory(textFactory);
	}
}