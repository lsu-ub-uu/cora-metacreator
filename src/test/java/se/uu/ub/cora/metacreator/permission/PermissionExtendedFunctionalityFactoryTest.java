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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metacreator.text.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.text.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class PermissionExtendedFunctionalityFactoryTest {

	private ExtendedFunctionalityFactory factory;

	@BeforeMethod
	public void setUp() {
		factory = new PermissionExtendedFunctionalityFactory();
		factory.initializeUsingDependencyProvider(null);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		List<ExtendedFunctionalityContext> functionalities = factory
				.getExtendedFunctionalityContexts();
		assertEquals(functionalities.size(), 2);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"permissionRole");
		assertCorrectContextUsingIndexPositionAndRecordType(1, CREATE_BEFORE_METADATA_VALIDATION,
				"permissionRule");
	}

	private void assertCorrectContextUsingIndexPositionAndRecordType(int index,
			ExtendedFunctionalityPosition position, String recordType) {
		ExtendedFunctionalityContext extendedFunctionalityContext = factory
				.getExtendedFunctionalityContexts().get(index);
		assertEquals(extendedFunctionalityContext.position, position);
		assertEquals(extendedFunctionalityContext.recordType, recordType);
		assertEquals(extendedFunctionalityContext.runAsNumber, 0);
	}

	@Test
	public void testCreateBeforeValidationPermissionRole() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "permissionRole");
		assertStandardMetadataCreateBeforeValidation(functionalities);
	}

	private void assertStandardMetadataCreateBeforeValidation(
			List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 2);
		MetadataGroupTextCompleter textCompleter = (MetadataGroupTextCompleter) functionalities
				.get(0);
		assertTrue(textCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);
		assertEquals(textCompleter.getImplementingTextType(), "coraText");

		TextAndDefTextExtFunc textCreator = (TextAndDefTextExtFunc) functionalities.get(1);
		assertTrue(textCreator.onlyForTestGetTextFactory() instanceof TextFactoryImp);
	}

	@Test
	public void testCreateBeforeValidationPermissionRule() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "permissionRule");
		assertStandardMetadataCreateBeforeValidation(functionalities);
	}
}
