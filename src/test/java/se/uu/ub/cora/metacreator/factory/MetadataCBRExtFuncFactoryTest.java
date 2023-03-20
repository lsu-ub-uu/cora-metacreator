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
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_RETURN;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metacreator.collection.CollectionVarFromItemCollectionCreator;
import se.uu.ub.cora.metacreator.group.PGroupFactory;
import se.uu.ub.cora.metacreator.group.PGroupFactoryImp;
import se.uu.ub.cora.metacreator.group.PGroupFromMetadataGroupCreator;
import se.uu.ub.cora.metacreator.presentation.PVarFactoryFactory;
import se.uu.ub.cora.metacreator.presentation.PVarFactoryFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PVarFromVarExtFunc;
import se.uu.ub.cora.metacreator.spy.DependencyProviderSpy;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class MetadataCBRExtFuncFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private List<ExtendedFunctionalityContext> extFuncContexts;
	private DependencyProviderSpy dependencyProvider;

	@BeforeMethod
	public void setUp() {
		factory = new MetadataCBRExtFuncFactory();
		dependencyProvider = new DependencyProviderSpy();
		factory.initializeUsingDependencyProvider(dependencyProvider);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		extFuncContexts = factory.getExtendedFunctionalityContexts();

		assertEquals(extFuncContexts.size(), 5);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_RETURN,
				"metadataGroup", 0);
		assertCorrectContextUsingIndexPositionAndRecordType(1, CREATE_BEFORE_RETURN,
				"metadataTextVariable", 0);
		assertCorrectContextUsingIndexPositionAndRecordType(2, CREATE_BEFORE_RETURN,
				"metadataNumberVariable", 0);
		assertCorrectContextUsingIndexPositionAndRecordType(3, CREATE_BEFORE_RETURN,
				"metadataCollectionVariable", 0);
		assertCorrectContextUsingIndexPositionAndRecordType(4, CREATE_BEFORE_RETURN,
				"metadataRecordLink", 0);
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
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_RETURN,
				"metadata");

		assertEquals(functionalities.size(), 3);

		PVarFromVarExtFunc extFunc = (PVarFromVarExtFunc) functionalities.get(0);
		PVarFactoryFactory pVarFFactory = extFunc.onlyForTestGetPVarFactoryFactory();
		assertTrue(pVarFFactory instanceof PVarFactoryFactoryImp);

		assertTrue(functionalities.get(1) instanceof CollectionVarFromItemCollectionCreator);

		PGroupFromMetadataGroupCreator extFunc3 = (PGroupFromMetadataGroupCreator) functionalities
				.get(2);
		PGroupFactory pGroupFactory = extFunc3.onlyForTestGetPGroupFactory();
		assertTrue(pGroupFactory instanceof PGroupFactoryImp);
	}
}
