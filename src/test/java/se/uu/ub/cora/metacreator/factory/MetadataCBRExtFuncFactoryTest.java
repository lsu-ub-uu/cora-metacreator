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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metacreator.MetadataIdToPresentationIdImp;
import se.uu.ub.cora.metacreator.group.PGroupFactoryImp;
import se.uu.ub.cora.metacreator.group.PGroupFromMetadataGroupExtFunc;
import se.uu.ub.cora.metacreator.metadata.ColVarFromItemCollectionExtFunc;
import se.uu.ub.cora.metacreator.metadata.CollectionVariableFactory;
import se.uu.ub.cora.metacreator.metadata.CollectionVariableFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PVarFactoryFactory;
import se.uu.ub.cora.metacreator.presentation.PVarFactoryFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PVarFromVarExtFunc;
import se.uu.ub.cora.metacreator.spy.DependencyProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;

public class MetadataCBRExtFuncFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private List<ExtendedFunctionalityContext> extFuncContexts;
	private DependencyProviderSpy dependencyProvider;

	@BeforeMethod
	public void setUp() {
		SpiderInstanceFactorySpy instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		factory = new MetadataCBRExtFuncFactory();
		dependencyProvider = new DependencyProviderSpy();
		factory.initializeUsingDependencyProvider(dependencyProvider);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		extFuncContexts = factory.getExtendedFunctionalityContexts();

		assertEquals(extFuncContexts.size(), 1);
		assertCorrectContextUsingIndexPositionAndRecordType(0,
				ExtendedFunctionalityPosition.CREATE_BEFORE_ENHANCE, "metadata", 0);
	}

	private void assertCorrectContextUsingIndexPositionAndRecordType(int index,
			ExtendedFunctionalityPosition position, String recordType, int runAsNumber) {
		ExtendedFunctionalityContext extFuncContext = extFuncContexts.get(index);
		assertEquals(extFuncContext.position, position);
		assertEquals(extFuncContext.recordType, recordType);
		assertEquals(extFuncContext.runAsNumber, runAsNumber);
	}

	@Test
	public void testCreateBeforeValidation() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(ExtendedFunctionalityPosition.CREATE_BEFORE_ENHANCE, "metadata");

		assertEquals(functionalities.size(), 3);
		assertFirstIsPVarFromVarTextExtFuncSetupWithFactory(functionalities);
		assertSecondIsColVarFromItemCollectionExtFuncSetupWithFactory(functionalities);
		assertThirdIsPGroupFromMetadataGroupExtFuncSetupWithFactory(functionalities);
	}

	private void assertFirstIsPVarFromVarTextExtFuncSetupWithFactory(
			List<ExtendedFunctionality> functionalities) {
		PVarFromVarExtFunc extFunc = (PVarFromVarExtFunc) functionalities.get(0);
		PVarFactoryFactory pVarFFactory = extFunc.onlyForTestGetPVarFactoryFactory();
		assertTrue(pVarFFactory instanceof PVarFactoryFactoryImp);
	}

	private void assertSecondIsColVarFromItemCollectionExtFuncSetupWithFactory(
			List<ExtendedFunctionality> functionalities) {
		ColVarFromItemCollectionExtFunc extFunc2 = (ColVarFromItemCollectionExtFunc) functionalities
				.get(1);
		CollectionVariableFactory colVarFactory = extFunc2.onlyForTestGetColVarFactory();
		assertTrue(colVarFactory instanceof CollectionVariableFactoryImp);
	}

	private void assertThirdIsPGroupFromMetadataGroupExtFuncSetupWithFactory(
			List<ExtendedFunctionality> functionalities) {
		PGroupFromMetadataGroupExtFunc extFunc3 = (PGroupFromMetadataGroupExtFunc) functionalities
				.get(2);
		PGroupFactoryImp pGroupFactory = (PGroupFactoryImp) extFunc3.onlyForTestGetPGroupFactory();
		assertTrue(pGroupFactory
				.onlyForTestGetMetadataIdToPresentationId() instanceof MetadataIdToPresentationIdImp);
	}
}
