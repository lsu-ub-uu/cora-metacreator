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

import se.uu.ub.cora.metacreator.recordtype.SearchFromRecordTypeExtFunc;
import se.uu.ub.cora.metacreator.recordtype.SearchGroupFactory;
import se.uu.ub.cora.metacreator.recordtype.SearchGroupFactoryImp;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class RecordTypeCBRExtFuncFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private List<ExtendedFunctionalityContext> extFuncContexts;

	@BeforeMethod
	public void setUp() {
		factory = new RecordTypeCBRExtFuncFactory();
		factory.initializeUsingDependencyProvider(null);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		extFuncContexts = factory.getExtendedFunctionalityContexts();

		assertEquals(extFuncContexts.size(), 1);
		assertCorrectContextUsingIndexPositionAndRecordType(0,
				ExtendedFunctionalityPosition.CREATE_BEFORE_ENHANCE, "recordType", 0);
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
				.factor(ExtendedFunctionalityPosition.CREATE_BEFORE_ENHANCE, "search");

		assertEquals(functionalities.size(), 1);
		SearchFromRecordTypeExtFunc extFunc = (SearchFromRecordTypeExtFunc) functionalities.get(0);
		SearchGroupFactory searchGroupFactory = extFunc.onlyForTestGetSearchGroupFactory();
		assertTrue(searchGroupFactory instanceof SearchGroupFactoryImp);
	}
}
