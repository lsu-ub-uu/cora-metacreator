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

import se.uu.ub.cora.metacreator.MetadataIdToPresentationIdImp;
import se.uu.ub.cora.metacreator.group.PGroupFactory;
import se.uu.ub.cora.metacreator.group.PGroupFactoryImp;
import se.uu.ub.cora.metacreator.recordtype.MetadataGroupFactoryImp;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactory;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.metacreator.validationtype.ValidationTypeAddMissingLinksExtFunc;
import se.uu.ub.cora.metacreator.validationtype.ValidationTypeCreateGroupsExtFunc;
import se.uu.ub.cora.metacreator.validationtype.ValidationTypeCreatePresentationsExtFunc;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class ValidationTypeCBMVExtFuncFactory implements ExtendedFunctionalityFactory {

	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_METADATA_VALIDATION, "validationType");
	}

	private void createContext(ExtendedFunctionalityPosition position, String validationType) {
		contexts.add(new ExtendedFunctionalityContext(position, validationType, 0));
	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		return contexts;
	}

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String validationType) {
		List<ExtendedFunctionality> functionalities = new ArrayList<>();
		functionalities.add(createValidationTypeAddMissingLinks());
		functionalities.add(createValidationTypeCreateGroupsExtFunc());
		functionalities.add(createValidationTypeCreatePresentationsExtFunc());
		functionalities.add(createTextAndDefTextExtFunc());
		return functionalities;
	}

	private TextAndDefTextExtFunc createTextAndDefTextExtFunc() {
		TextFactory textFactory = new TextFactoryImp();
		return TextAndDefTextExtFunc.usingTextFactory(textFactory);
	}

	private ValidationTypeAddMissingLinksExtFunc createValidationTypeAddMissingLinks() {
		return new ValidationTypeAddMissingLinksExtFunc();
	}

	private ValidationTypeCreateGroupsExtFunc createValidationTypeCreateGroupsExtFunc() {
		MetadataGroupFactoryImp groupFactory = new MetadataGroupFactoryImp();
		return ValidationTypeCreateGroupsExtFunc.usingGroupFactory(groupFactory);
	}

	private ExtendedFunctionality createValidationTypeCreatePresentationsExtFunc() {
		PGroupFactory pGroupFactory = PGroupFactoryImp
				.usingMetadataIdToPresentationId(new MetadataIdToPresentationIdImp());
		return ValidationTypeCreatePresentationsExtFunc.usingPGroupFactory(pGroupFactory);
	}
}
