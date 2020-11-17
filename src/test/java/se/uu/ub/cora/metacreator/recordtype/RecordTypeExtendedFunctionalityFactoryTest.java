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
package se.uu.ub.cora.metacreator.recordtype;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_METADATA_VALIDATION;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_RETURN;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class RecordTypeExtendedFunctionalityFactoryTest {

	private ExtendedFunctionalityFactory factory;

	@BeforeMethod
	public void setUp() {
		factory = new RecordTypeExtendedFunctionalityFactory();
		factory.initializeUsingDependencyProvider(null);
	}

	@Test
	public void testGetExtendedFunctionalityContexts() {
		List<ExtendedFunctionalityContext> functionalities = factory
				.getExtendedFunctionalityContexts();
		assertEquals(functionalities.size(), 2);
		assertCorrectContextUsingIndexPositionAndRecordType(0, CREATE_BEFORE_METADATA_VALIDATION,
				"recordType");
		assertCorrectContextUsingIndexPositionAndRecordType(1, CREATE_BEFORE_RETURN, "recordType");
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
	public void testCreateBeforeValidation() {
		List<ExtendedFunctionality> functionalities = factory
				.factor(CREATE_BEFORE_METADATA_VALIDATION, "recordType");
		assertEquals(functionalities.size(), 2);
		assertTrue(functionalities.get(0) instanceof RecordTypeMetaCompleter);
		RecordTypeCreator recordTypeCreator = (RecordTypeCreator) functionalities.get(1);
		assertEquals(recordTypeCreator.getImplementingTextType(), "coraText");
	}

	@Test
	public void testCreateBeforeReturn() {
		List<ExtendedFunctionality> functionalities = factory.factor(CREATE_BEFORE_RETURN,
				"recordType");
		assertEquals(functionalities.size(), 1);
		assertTrue(functionalities.get(0) instanceof SearchFromRecordTypeCreator);
	}
}
