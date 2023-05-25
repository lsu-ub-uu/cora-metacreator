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

import se.uu.ub.cora.metacreator.MetadataIdToPresentationId;
import se.uu.ub.cora.metacreator.MetadataIdToPresentationIdImp;
import se.uu.ub.cora.metacreator.group.PGroupFactoryImp;
import se.uu.ub.cora.metacreator.recordtype.MetadataGroupFactory;
import se.uu.ub.cora.metacreator.recordtype.MetadataGroupFactoryImp;
import se.uu.ub.cora.metacreator.spy.DependencyProviderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactory;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.metacreator.validationtype.ValidationTypeAddMissingLinksExtFunc;
import se.uu.ub.cora.metacreator.validationtype.ValidationTypeCreateGroupsExtFunc;
import se.uu.ub.cora.metacreator.validationtype.ValidationTypeCreatePresentationsExtFunc;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class ValidationTypeCBMVExtFuncFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private List<ExtendedFunctionalityContext> extFuncContexts;

	@BeforeMethod
	public void setUp() {
		SpiderInstanceFactorySpy instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		factory = new ValidationTypeCBMVExtFuncFactory();
		factory.initializeUsingDependencyProvider(new DependencyProviderSpy());
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		extFuncContexts = factory.getExtendedFunctionalityContexts();

		assertEquals(extFuncContexts.size(), 1);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"validationType", 0);
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
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "validationType");

		assertEquals(functionalities.size(), 4);
		assertFirstIsTextDefTextExtFuncSetupWithFactory(functionalities);
		assertSecondIsRecordTypeAddMissingLinks(functionalities);
		assertThirdIsRecordTypeCreateGroupsWithFactory(functionalities);
		assertFourthIsRecordTypeCreatePGroupsWithFactory(functionalities);
	}

	private void assertFirstIsTextDefTextExtFuncSetupWithFactory(
			List<ExtendedFunctionality> functionalities) {
		assertTrue(functionalities.get(0) instanceof ValidationTypeAddMissingLinksExtFunc);

	}

	private void assertSecondIsRecordTypeAddMissingLinks(
			List<ExtendedFunctionality> functionalities) {

		ValidationTypeCreateGroupsExtFunc extFunc = (ValidationTypeCreateGroupsExtFunc) functionalities
				.get(1);
		MetadataGroupFactory groupFactory = extFunc.onlyForTestGetGroupFactory();
		assertTrue(groupFactory instanceof MetadataGroupFactoryImp);
	}

	private void assertThirdIsRecordTypeCreateGroupsWithFactory(
			List<ExtendedFunctionality> functionalities) {
		ValidationTypeCreatePresentationsExtFunc extFunc = (ValidationTypeCreatePresentationsExtFunc) functionalities
				.get(2);
		PGroupFactoryImp pGroupFactory = (PGroupFactoryImp) extFunc.onlyForTestGetPGroupFactory();
		assertTrue(pGroupFactory instanceof PGroupFactoryImp);
		MetadataIdToPresentationId metadataIdToPresentationId = pGroupFactory
				.onlyForTestGetMetadataIdToPresentationId();
		assertTrue(metadataIdToPresentationId instanceof MetadataIdToPresentationIdImp);
	}

	private void assertFourthIsRecordTypeCreatePGroupsWithFactory(
			List<ExtendedFunctionality> functionalities) {
		TextAndDefTextExtFunc extFunc = (TextAndDefTextExtFunc) functionalities.get(3);
		TextFactory textFactory = extFunc.onlyForTestGetTextFactory();
		assertTrue(textFactory instanceof TextFactoryImp);
	}
}
