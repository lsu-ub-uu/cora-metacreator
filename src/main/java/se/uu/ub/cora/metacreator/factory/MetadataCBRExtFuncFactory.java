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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.metacreator.MetadataIdToPresentationIdImp;
import se.uu.ub.cora.metacreator.group.PGroupFactory;
import se.uu.ub.cora.metacreator.group.PGroupFactoryImp;
import se.uu.ub.cora.metacreator.group.PGroupFromMetadataGroupExtFunc;
import se.uu.ub.cora.metacreator.metadata.ColVarFromItemCollectionExtFunc;
import se.uu.ub.cora.metacreator.metadata.CollectionVariableFactory;
import se.uu.ub.cora.metacreator.metadata.CollectionVariableFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PVarFactoryFactory;
import se.uu.ub.cora.metacreator.presentation.PVarFactoryFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PVarFromVarExtFunc;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class MetadataCBRExtFuncFactory implements ExtendedFunctionalityFactory {

	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(ExtendedFunctionalityPosition.CREATE_BEFORE_ENHANCE, "metadata");
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
		functionalities.add(createPVarFromVarExtFunc());
		functionalities.add(createColVarFRomItemCollectionExtFunc());
		functionalities.add(cratePGroupFromMetadataGroupCreator());
		return functionalities;
	}

	private PVarFromVarExtFunc createPVarFromVarExtFunc() {
		PVarFactoryFactory pVarFFactory = new PVarFactoryFactoryImp();
		return PVarFromVarExtFunc.usingPVarFactoryFactory(pVarFFactory);
	}

	private ColVarFromItemCollectionExtFunc createColVarFRomItemCollectionExtFunc() {
		CollectionVariableFactory colVarFactory = new CollectionVariableFactoryImp();
		return ColVarFromItemCollectionExtFunc.usingCollectionVariableFactory(colVarFactory);
	}

	private PGroupFromMetadataGroupExtFunc cratePGroupFromMetadataGroupCreator() {
		MetadataIdToPresentationIdImp metadataIdToPresentationId = new MetadataIdToPresentationIdImp();
		PGroupFactory pGroupFactory = PGroupFactoryImp
				.usingMetadataIdToPresentationId(metadataIdToPresentationId);
		return PGroupFromMetadataGroupExtFunc.usingPGroupFactory(pGroupFactory);
	}
}
