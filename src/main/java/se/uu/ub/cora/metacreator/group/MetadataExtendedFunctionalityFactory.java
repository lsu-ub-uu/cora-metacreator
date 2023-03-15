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
package se.uu.ub.cora.metacreator.group;

import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_RETURN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.TextCreator;
import se.uu.ub.cora.metacreator.TextFactoryImp;
import se.uu.ub.cora.metacreator.numbervar.PNumVarFromNumberVarCreator;
import se.uu.ub.cora.metacreator.recordlink.PLinkFromRecordLinkCreator;
import se.uu.ub.cora.metacreator.textvar.PVarFromTextVarExtendedFunctionality;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class MetadataExtendedFunctionalityFactory implements ExtendedFunctionalityFactory {

	private static final String METADATA_NUMBER_VARIABLE = "metadataNumberVariable";
	private static final String METADATA_RECORD_LINK = "metadataRecordLink";
	private static final String METADATA_TEXT_VARIABLE = "metadataTextVariable";
	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "metadataGroup");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, METADATA_TEXT_VARIABLE);
		createContext(CREATE_BEFORE_METADATA_VALIDATION, METADATA_NUMBER_VARIABLE);
		createContext(CREATE_BEFORE_METADATA_VALIDATION, METADATA_RECORD_LINK);

		createContext(CREATE_BEFORE_RETURN, "metadataGroup");
		createContext(CREATE_BEFORE_RETURN, METADATA_TEXT_VARIABLE);
		createContext(CREATE_BEFORE_RETURN, METADATA_NUMBER_VARIABLE);
		createContext(CREATE_BEFORE_RETURN, METADATA_RECORD_LINK);
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
		if (CREATE_BEFORE_METADATA_VALIDATION == position) {
			return createBeforeMetadataValidation();
		}
		return createBeforeReturn(recordType);
	}

	private List<ExtendedFunctionality> createBeforeMetadataValidation() {
		List<ExtendedFunctionality> functionalities = new ArrayList<>();
		functionalities.add(createMetadataGroupTextCompleter());
		functionalities.add(TextCreator.usingTextFactory(new TextFactoryImp()));
		return functionalities;
	}

	private MetadataGroupTextCompleter createMetadataGroupTextCompleter() {
		return MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
				new MetadataCompleterImp(), "coraText");
	}

	private List<ExtendedFunctionality> createBeforeReturn(String recordType) {
		if (METADATA_TEXT_VARIABLE.equals(recordType)) {
			return createBeforeReturnForMetadataTextVariable();
		}
		if (METADATA_RECORD_LINK.equals(recordType)) {
			return createBeforeReturnForMetadataRecordLink();
		}
		if (METADATA_NUMBER_VARIABLE.equals(recordType)) {
			return createBeforeReturnForMetadataNumberVariable();
		}
		return createBeforeReturnForMetadataGroup();
	}

	private List<ExtendedFunctionality> createBeforeReturnForMetadataTextVariable() {
		return Collections.singletonList(new PVarFromTextVarExtendedFunctionality());
	}

	private List<ExtendedFunctionality> createBeforeReturnForMetadataRecordLink() {
		return Collections.singletonList(new PLinkFromRecordLinkCreator());
	}

	private List<ExtendedFunctionality> createBeforeReturnForMetadataNumberVariable() {
		return Collections.singletonList(new PNumVarFromNumberVarCreator());
	}

	private List<ExtendedFunctionality> createBeforeReturnForMetadataGroup() {
		return Collections.singletonList(new PGroupFromMetadataGroupCreator());
	}

}
