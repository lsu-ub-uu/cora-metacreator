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
package se.uu.ub.cora.metacreator.permission;

import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.metacreator.text.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.text.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class PermissionExtendedFunctionalityFactory implements ExtendedFunctionalityFactory {

	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "permissionRole");
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "permissionRule");
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
		// TODO: TextCreator should cover what MetadataGroupTextCompleter does
		// functionalities.add(createMetadataGroupTextCompleter());
		functionalities.add(TextAndDefTextExtFunc.usingTextFactory(new TextFactoryImp()));
		return functionalities;
	}

	private MetadataGroupTextCompleter createMetadataGroupTextCompleter() {
		return MetadataGroupTextCompleter.withMetadataCompleterForTextLinkedRecordType(
				new MetadataCompleterImp(), "coraText");
	}

}
