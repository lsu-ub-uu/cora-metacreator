/*
 * Copyright 2017, 2018, 2019 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.metacreator.MetadataGroupTextCompleter;
import se.uu.ub.cora.metacreator.TextCreator;
import se.uu.ub.cora.metacreator.collection.CollectionVariableCompleter;
import se.uu.ub.cora.metacreator.collection.ItemCollectionCreator;
import se.uu.ub.cora.metacreator.dependency.DependencyProviderSpy;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.group.GroupCompleter;
import se.uu.ub.cora.metacreator.log.LoggerFactorySpy;
import se.uu.ub.cora.metacreator.recordlink.RecordLinkCompleter;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeCreator;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeMetaCompleter;
import se.uu.ub.cora.metacreator.search.SearchCreator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class MetacreatorExtendedBeforeMetadataValidationTest {
	private static final String CORA_TEXT = "coraText";
	private MetacreatorExtendedFunctionalityProvider functionalityProvider;
	private SpiderInstanceFactorySpy instanceFactory;
	private LoggerFactorySpy loggerFactorySpy;

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		SpiderDependencyProvider dependencyProvider = new DependencyProviderSpy(new HashMap<>());
		functionalityProvider = new MetacreatorExtendedFunctionalityProvider(dependencyProvider);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForTextVariable() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataTextVariable");

		assertListContainsCompleterAndCreatorOfCoraText(
				functionalityForCreateBeforeMetadataValidation);
	}

	private void assertListContainsCompleterAndCreatorOfCoraText(
			List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation) {
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);
		MetadataGroupTextCompleter textCompleter = (MetadataGroupTextCompleter) functionalityForCreateBeforeMetadataValidation
				.get(0);
		assertEquals(textCompleter.getImplementingTextType(), CORA_TEXT);
		assertTrue(textCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);

		TextCreator textCreator = (TextCreator) functionalityForCreateBeforeMetadataValidation
				.get(1);
		assertEquals(textCreator.getImplementingTextType(), CORA_TEXT);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForNumberVariable() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataNumberVariable");
		assertListContainsCompleterAndCreatorOfCoraText(
				functionalityForCreateBeforeMetadataValidation);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationNot() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataTextVariableNOT");
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 0);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForSearch() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("search");
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);
		MetadataGroupTextCompleter textCompleter = (MetadataGroupTextCompleter) functionalityForCreateBeforeMetadataValidation
				.get(0);
		assertEquals(textCompleter.getImplementingTextType(), CORA_TEXT);
		assertTrue(textCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);

		assertTrue(functionalityForCreateBeforeMetadataValidation.get(1) instanceof SearchCreator);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForRecordType() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("recordType");
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);
		assertTrue(functionalityForCreateBeforeMetadataValidation
				.get(0) instanceof RecordTypeMetaCompleter);
		RecordTypeCreator recordTypeCreator = (RecordTypeCreator) functionalityForCreateBeforeMetadataValidation
				.get(1);
		assertEquals(recordTypeCreator.getImplementingTextType(), CORA_TEXT);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForCollectionItem() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("genericCollectionItem");
		assertListContainsCompleterAndCreatorOfCoraText(
				functionalityForCreateBeforeMetadataValidation);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForCountryCollectionItem() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("countryCollectionItem");

		assertListContainsCompleterAndCreatorOfCoraText(
				functionalityForCreateBeforeMetadataValidation);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForItemCollection() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataItemCollection");

		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);
		MetadataGroupTextCompleter textCompleter = (MetadataGroupTextCompleter) functionalityForCreateBeforeMetadataValidation
				.get(0);
		assertEquals(textCompleter.getImplementingTextType(), CORA_TEXT);
		assertTrue(textCompleter.getMetadataCompleter() instanceof MetadataCompleterImp);

		ItemCollectionCreator itemCollectionCreator = (ItemCollectionCreator) functionalityForCreateBeforeMetadataValidation
				.get(1);
		assertEquals(itemCollectionCreator.getImplementingTextType(), CORA_TEXT);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForMetadataGroup() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataGroup");
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);

		GroupCompleter groupCompleter = (GroupCompleter) functionalityForCreateBeforeMetadataValidation
				.get(0);
		assertEquals(groupCompleter.getImplementingTextType(), CORA_TEXT);

		TextCreator textCreator = (TextCreator) functionalityForCreateBeforeMetadataValidation
				.get(1);
		assertEquals(textCreator.getImplementingTextType(), CORA_TEXT);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForMetadataRecordLink() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataRecordLink");
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);

		RecordLinkCompleter recordLinkCompleter = (RecordLinkCompleter) functionalityForCreateBeforeMetadataValidation
				.get(0);
		assertEquals(recordLinkCompleter.getImplementingTextType(), CORA_TEXT);

		TextCreator textCreator = (TextCreator) functionalityForCreateBeforeMetadataValidation
				.get(1);
		assertEquals(textCreator.getImplementingTextType(), CORA_TEXT);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForCollectionVariable() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("metadataCollectionVariable");
		assertEquals(functionalityForCreateBeforeMetadataValidation.size(), 2);

		CollectionVariableCompleter collectionVarCompleter = (CollectionVariableCompleter) functionalityForCreateBeforeMetadataValidation
				.get(0);
		assertEquals(collectionVarCompleter.getImplementingTextType(), CORA_TEXT);

		TextCreator textCreator = (TextCreator) functionalityForCreateBeforeMetadataValidation
				.get(1);
		assertEquals(textCreator.getImplementingTextType(), CORA_TEXT);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForPermissionRole() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("permissionRole");

		assertListContainsCompleterAndCreatorOfCoraText(
				functionalityForCreateBeforeMetadataValidation);
	}

	@Test
	public void testGetFunctionalityForCreateBeforeMetadataValidationForPermissionRule() {
		List<ExtendedFunctionality> functionalityForCreateBeforeMetadataValidation = functionalityProvider
				.getFunctionalityForCreateBeforeMetadataValidation("permissionRule");

		assertListContainsCompleterAndCreatorOfCoraText(
				functionalityForCreateBeforeMetadataValidation);
	}
}
