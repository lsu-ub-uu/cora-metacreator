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
package se.uu.ub.cora.metacreator.collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_RETURN;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.metacreator.dependency.DependencyProviderSpy;
import se.uu.ub.cora.metacreator.dependency.RecordTypeHandlerSpy;
import se.uu.ub.cora.metacreator.log.LoggerFactorySpy;
import se.uu.ub.cora.metacreator.presentation.PCollVarFactoryImp;
import se.uu.ub.cora.metacreator.presentation.PCollVarFromCollectionVarCreator;
import se.uu.ub.cora.metacreator.text.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.text.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.text.TextAndDefTextExtFunc;
import se.uu.ub.cora.metacreator.text.TextFactoryImp;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class CollectionExtendedFunctionalityFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private DependencyProviderSpy dependencyProvider;

	@BeforeMethod
	public void setUp() {
		LoggerFactorySpy loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		factory = new CollectionExtendedFunctionalityFactory();
		dependencyProvider = new DependencyProviderSpy(null);
		factory.initializeUsingDependencyProvider(dependencyProvider);
	}

	@Test
	public void testGetExtendedFunctionalityContextsDefault() {
		assertEquals(factory.getExtendedFunctionalityContexts().size(), 5);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataCollectionItem");
		assertCorrectContextUsingIndexPositionAndRecordType(1, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataItemCollection");
		assertCorrectContextUsingIndexPositionAndRecordType(2, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataCollectionVariable");
		assertCorrectContextUsingIndexPositionAndRecordType(3, CREATE_BEFORE_RETURN,
				"metadataItemCollection");
		assertCorrectContextUsingIndexPositionAndRecordType(4, CREATE_BEFORE_RETURN,
				"metadataCollectionVariable");
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		factory = new CollectionExtendedFunctionalityFactory();
		dependencyProvider = new DependencyProviderSpy(null);
		addImplementingRecordTypeToSpy("genericCollectionItem");
		addImplementingRecordTypeToSpy("someCollectionItem");
		factory.initializeUsingDependencyProvider(dependencyProvider);

		assertEquals(factory.getExtendedFunctionalityContexts().size(), 7);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataCollectionItem");
		assertCorrectContextUsingIndexPositionAndRecordType(1, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataItemCollection");
		assertCorrectContextUsingIndexPositionAndRecordType(2, CREATE_BEFORE_METADATA_VALIDATION,
				"metadataCollectionVariable");
		assertCorrectContextUsingIndexPositionAndRecordType(3, CREATE_BEFORE_RETURN,
				"metadataItemCollection");
		assertCorrectContextUsingIndexPositionAndRecordType(4, CREATE_BEFORE_RETURN,
				"metadataCollectionVariable");
		assertCorrectContextUsingIndexPositionAndRecordType(5, CREATE_BEFORE_METADATA_VALIDATION,
				"genericCollectionItem");
		assertCorrectContextUsingIndexPositionAndRecordType(6, CREATE_BEFORE_METADATA_VALIDATION,
				"someCollectionItem");
	}

	private void addImplementingRecordTypeToSpy(String recordTypeIdToAdd) {
		RecordTypeHandlerSpy recordTypeHandlerSpy1 = new RecordTypeHandlerSpy();
		recordTypeHandlerSpy1.recordTypeId = recordTypeIdToAdd;
		dependencyProvider.recordTypeHandlerSpy.recordTypeHandlers.add(recordTypeHandlerSpy1);
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
	public void testFactorCreateBeforeValidationForCollectionItem() {
		List<ExtendedFunctionality> functionality = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataCollectionItem");
		MetadataGroupTextCompleter groupTextCompleter = (MetadataGroupTextCompleter) functionality
				.get(0);
		assertTrue(groupTextCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);
		assertEquals(groupTextCompleter.getImplementingTextType(), "coraText");

		assertTrue(groupTextCompleter instanceof MetadataGroupTextCompleter);

		TextAndDefTextExtFunc textCreator = (TextAndDefTextExtFunc) functionality.get(1);
		assertTrue(textCreator.onlyForTestGetTextFactory() instanceof TextFactoryImp);
	}

	@Test
	public void testFactorCreateBeforeValidationForItemCollection() {
		List<ExtendedFunctionality> functionality = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataItemCollection");
		MetadataGroupTextCompleter groupTextCompleter = (MetadataGroupTextCompleter) functionality
				.get(0);
		assertTrue(groupTextCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);
		assertEquals(groupTextCompleter.getImplementingTextType(), "coraText");
		ItemCollectionCreator itemCollectionCreator = (ItemCollectionCreator) functionality.get(1);
		assertEquals(itemCollectionCreator.getImplementingTextType(), "coraText");
		assertEquals(functionality.size(), 2);
	}

	@Test
	public void testFactorCreateBeforeValidationForCollectionVariable() {
		List<ExtendedFunctionality> functionality = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "metadataCollectionVariable");

		MetadataGroupTextCompleter groupTextCompleter = (MetadataGroupTextCompleter) functionality
				.get(0);
		assertTrue(groupTextCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);

		TextAndDefTextExtFunc textCreator = (TextAndDefTextExtFunc) functionality.get(1);
		assertTrue(textCreator.onlyForTestGetTextFactory() instanceof TextFactoryImp);
	}

	@Test
	public void testFactorCreateBeforeReturnForItemCollection() {
		List<ExtendedFunctionality> functionality = factory.factor(CREATE_BEFORE_RETURN,
				"metadataItemCollection");

		assertTrue(functionality.get(0) instanceof CollectionVarFromItemCollectionCreator);
	}

	@Test
	public void testFactorCreateBeforeReturnforCollectionVariable() {
		List<ExtendedFunctionality> functionality = factory.factor(CREATE_BEFORE_RETURN,
				"metadataCollectionVariable");
		PCollVarFromCollectionVarCreator extendedFunctionality = (PCollVarFromCollectionVarCreator) functionality
				.get(0);
		assertTrue((extendedFunctionality)
				.onlyForTestGetPCollVarFactory() instanceof PCollVarFactoryImp);

	}

}
