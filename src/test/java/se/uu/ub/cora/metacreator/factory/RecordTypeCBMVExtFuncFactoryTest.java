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
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metacreator.group.PGroupFactory;
import se.uu.ub.cora.metacreator.group.PGroupFactoryImp;
import se.uu.ub.cora.metacreator.recordtype.GroupFactory;
import se.uu.ub.cora.metacreator.recordtype.GroupFactoryImp;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeCreateGroupsExtFunc;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeMetaCompleter;
import se.uu.ub.cora.metacreator.spy.DependencyProviderSpy;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactory;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class RecordTypeCBMVExtFuncFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private List<ExtendedFunctionalityContext> extFuncContexts;

	@BeforeMethod
	public void setUp() {
		factory = new RecordTypeCBMVExtFuncFactory();
		factory.initializeUsingDependencyProvider(new DependencyProviderSpy());
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		extFuncContexts = factory.getExtendedFunctionalityContexts();

		assertEquals(extFuncContexts.size(), 1);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"recordType", 0);
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
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "search");

		assertEquals(functionalities.size(), 4);
		assertFirstIsTextDefTextExtFuncSetupWithFactory(functionalities);
		assertSecondIsRecordTypeMetaCompleter(functionalities);
		assertThirdIsRecordTypeCreateGroupsWithFactory(functionalities);
		assertFourthIsRecordTypeCreatePGroupsWithFactory(functionalities);
	}

	private void assertFirstIsTextDefTextExtFuncSetupWithFactory(
			List<ExtendedFunctionality> functionalities) {
		TextAndDefTextExtFunc extFunc = (TextAndDefTextExtFunc) functionalities.get(0);
		TextFactory textFactory = extFunc.onlyForTestGetTextFactory();
		assertTrue(textFactory instanceof TextFactoryImp);
	}

	private void assertSecondIsRecordTypeMetaCompleter(
			List<ExtendedFunctionality> functionalities) {
		assertTrue(functionalities.get(1) instanceof RecordTypeMetaCompleter);
	}

	private void assertThirdIsRecordTypeCreateGroupsWithFactory(
			List<ExtendedFunctionality> functionalities) {
		RecordTypeCreateGroupsExtFunc extFunc3 = (RecordTypeCreateGroupsExtFunc) functionalities
				.get(2);
		GroupFactory groupFactory = extFunc3.onlyForTestGetGroupFactory();
		assertTrue(groupFactory instanceof GroupFactoryImp);
	}

	private void assertFourthIsRecordTypeCreatePGroupsWithFactory(
			List<ExtendedFunctionality> functionalities) {
		RecordTypeCreateGroupsExtFunc extFunc4 = (RecordTypeCreateGroupsExtFunc) functionalities
				.get(2);
		PGroupFactory pGroupFactory = extFunc4.onlyForTestGetPGroupFactory();
		assertTrue(pGroupFactory instanceof PGroupFactoryImp);
	}
}
