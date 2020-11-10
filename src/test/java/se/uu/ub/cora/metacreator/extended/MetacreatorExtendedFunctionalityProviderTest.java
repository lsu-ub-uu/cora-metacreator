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

import static org.testng.Assert.assertEquals;

import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.metacreator.dependency.DependencyProviderSpy;
import se.uu.ub.cora.metacreator.log.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;

public class MetacreatorExtendedFunctionalityProviderTest {
	private MetacreatorExtendedFunctionalityProvider functionalityProvider;
	private LoggerFactorySpy loggerFactorySpy;

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		SpiderDependencyProvider dependencyProvider = new DependencyProviderSpy(new HashMap<>());
		functionalityProvider = new MetacreatorExtendedFunctionalityProvider(dependencyProvider);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		assertEquals(functionalityProvider.getExtendedFunctionalityContexts().size(), 18);
	}

	// @Test
	// public void testGetFunctionalityForCreateBeforeReturn() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataTextVariable");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(functionalityForCreateBeforeReturn.get(0) instanceof PVarFromTextVarCreator);
	//
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn2 = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("appToken");
	// assertEquals(functionalityForCreateBeforeReturn2.size(), 1);
	// assertTrue(functionalityForCreateBeforeReturn2
	// .get(0) instanceof UserUpdaterForAppTokenAsExtendedFunctionality);
	// }

	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnNot() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataTextVariableNOT");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 0);
	// }
	//
	// @Test
	// public void testEnsureListIsRealList() {
	// assertTrue(functionalityProvider
	// .ensureListExists(Collections.emptyList()) instanceof ArrayList);
	// List<ExtendedFunctionality> list = new ArrayList<>();
	// list.add(null);
	// assertEquals(functionalityProvider.ensureListExists(list), list);
	// }
	//
	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnForRecordType() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("recordType");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(
	// functionalityForCreateBeforeReturn.get(0) instanceof SearchFromRecordTypeCreator);
	//
	// }
	//
	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnForItemCollection() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataItemCollection");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(functionalityForCreateBeforeReturn
	// .get(0) instanceof CollectionVarFromItemCollectionCreator);
	//
	// }
	//
	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnForCollectionVariable() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataCollectionVariable");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(functionalityForCreateBeforeReturn
	// .get(0) instanceof PCollVarFromCollectionVarCreator);
	//
	// }
	//
	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnForRecordLink() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataRecordLink");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(functionalityForCreateBeforeReturn.get(0) instanceof PLinkFromRecordLinkCreator);
	//
	// }
	//
	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnForMetadataGroup() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataGroup");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(functionalityForCreateBeforeReturn
	// .get(0) instanceof PGroupFromMetadataGroupCreator);
	//
	// }
	//
	// @Test
	// public void testGetFunctionalityForCreateBeforeReturnForMetadataNumberVariable() {
	// List<ExtendedFunctionality> functionalityForCreateBeforeReturn = functionalityProvider
	// .getFunctionalityForCreateBeforeReturn("metadataNumberVariable");
	// assertEquals(functionalityForCreateBeforeReturn.size(), 1);
	// assertTrue(
	// functionalityForCreateBeforeReturn.get(0) instanceof PNumVarFromNumberVarCreator);
	// }

}
