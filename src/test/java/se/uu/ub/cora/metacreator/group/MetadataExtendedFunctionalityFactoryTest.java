/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.metacreator.group;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_RETURN;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.TextCreator;
import se.uu.ub.cora.metacreator.TextFactoryImp;
import se.uu.ub.cora.metacreator.numbervar.PNumVarFromNumberVarCreator;
import se.uu.ub.cora.metacreator.recordlink.PLinkFromRecordLinkCreator;
import se.uu.ub.cora.metacreator.textvar.PVarFromTextVarCreator;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class MetadataExtendedFunctionalityFactoryTest {

	private ExtendedFunctionalityFactory factory;

	@BeforeMethod
	public void setUp() {
		factory = new MetadataExtendedFunctionalityFactory();
		factory.initializeUsingDependencyProvider(null);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		List<ExtendedFunctionalityContext> contexts = factory.getExtendedFunctionalityContexts();
		assertEquals(contexts.size(), 8);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataGroup");
		assertCorrectContextUsingIndexPositionAndRecordType(1, CREATE_BEFORE_RETURN,
				"metadataGroup");
		assertCorrectContextUsingIndexPositionAndRecordType(2, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataTextVariable");
		assertCorrectContextUsingIndexPositionAndRecordType(3, CREATE_BEFORE_RETURN,
				"metadataTextVariable");
		assertCorrectContextUsingIndexPositionAndRecordType(4, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataRecordLink");
		assertCorrectContextUsingIndexPositionAndRecordType(5, CREATE_BEFORE_RETURN,
				"metadataRecordLink");
		assertCorrectContextUsingIndexPositionAndRecordType(6, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataNumberVariable");
		assertCorrectContextUsingIndexPositionAndRecordType(7, CREATE_BEFORE_RETURN,
				"metadataNumberVariable");
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
	public void testCreateBeforeValidationMetadataGroup() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataGroup");

		assertStandardMetadataCreateBeforeValidation(functionalities);
	}

	private void assertStandardMetadataCreateBeforeValidation(
			List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 2);
		MetadataGroupTextCompleter extendedFunctionality = (MetadataGroupTextCompleter) functionalities
				.get(0);
		assertTrue(extendedFunctionality.getMetadataCompleter() instanceof MetadataCompleterImp);
		assertEquals(extendedFunctionality.getImplementingTextType(), "coraText");

		TextCreator textCreator = (TextCreator) functionalities.get(1);
		assertTrue(textCreator.onlyForTestGetTextFactory() instanceof TextFactoryImp);
	}

	@Test
	public void testCreateBeforeValidationMetadataTextVariable() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataTextVariable");

		assertStandardMetadataCreateBeforeValidation(functionalities);
	}

	@Test
	public void testCreateBeforeValidationMetadataRecordLink() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataRecordLink");

		assertStandardMetadataCreateBeforeValidation(functionalities);
	}

	@Test
	public void testCreateBeforeValidationMetadataNumberVariable() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataNumberVariable");

		assertStandardMetadataCreateBeforeValidation(functionalities);
	}

	@Test
	public void testCreateBeforeReturnMetadataGroup() {
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_RETURN,
				"metadataGroup");

		assertEquals(functionalities.size(), 1);
		assertTrue(functionalities.get(0) instanceof PGroupFromMetadataGroupCreator);
	}

	@Test
	public void testCreateBeforeReturnMetadataTextVariable() {
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_RETURN,
				"metadataTextVariable");

		assertEquals(functionalities.size(), 1);
		assertTrue(functionalities.get(0) instanceof PVarFromTextVarCreator);
	}

	@Test
	public void testCreateBeforeReturnMetadataRecordLink() {
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_RETURN,
				"metadataRecordLink");

		assertEquals(functionalities.size(), 1);
		assertTrue(functionalities.get(0) instanceof PLinkFromRecordLinkCreator);
	}

	@Test
	public void testCreateBeforeReturnMetadataNumberVariable() {
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_RETURN,
				"metadataNumberVariable");

		assertEquals(functionalities.size(), 1);
		assertTrue(functionalities.get(0) instanceof PNumVarFromNumberVarCreator);
	}

}
