/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.record.DataException;

public class MetadataIdToPresentationIdTest {
	MetadataIdToPresentationId toId;

	@BeforeMethod
	public void setUp() {
		toId = new MetadataIdToPresentationIdImp();
	}

	@Test
	public void testCreateIdForTextVar() {
		assertUsingMetadataIdModeAndResult("xyzTextVar", "input", "xyzPVar");
		assertUsingMetadataIdModeAndResult("xyzTextVarzyxTextVar", "input", "xyzTextVarzyxPVar");
		assertUsingMetadataIdModeAndResult("xyzTextVar", "output", "xyzOutputPVar");
	}

	private void assertUsingMetadataIdModeAndResult(String metadataId, String mode,
			String expectedResult) {
		String result = toId.createPresentationIdUsingMetadataIdAndMode(metadataId, mode);
		assertEquals(result, expectedResult);
	}

	@Test
	public void testCreateIdForNumberVar() {
		assertUsingMetadataIdModeAndResult("xyzNumberVar", "input", "xyzPNumVar");
		assertUsingMetadataIdModeAndResult("xyzNumberVar", "output", "xyzOutputPNumVar");
	}

	@Test
	public void testCreateIdForCollectionVar() {
		assertUsingMetadataIdModeAndResult("xyzCollectionVar", "input", "xyzPCollVar");
		assertUsingMetadataIdModeAndResult("xyzCollectionVar", "output", "xyzOutputPCollVar");
	}

	@Test
	public void testCreateIdForResLink() {
		assertUsingMetadataIdModeAndResult("xyzResLink", "input", "xyzPResLink");
		assertUsingMetadataIdModeAndResult("xyzResLink", "output", "xyzOutputPResLink");
	}

	@Test
	public void testCreateIdForRecordLink() {
		assertUsingMetadataIdModeAndResult("xyzLink", "input", "xyzPLink");
		assertUsingMetadataIdModeAndResult("xyzLink", "output", "xyzOutputPLink");
	}

	@Test
	public void testCreateIdForGroup() {
		assertUsingMetadataIdModeAndResult("xyzGroup", "input", "xyzPGroup");
		assertUsingMetadataIdModeAndResult("xyzGroup", "output", "xyzOutputPGroup");
	}

	@Test(expectedExceptions = DataException.class, expectedExceptionsMessageRegExp = ""
			+ "Not possible to construct presentationId from metadataId")
	public void testTypeNotFound() {
		assertUsingMetadataIdModeAndResult("xyzUnknownType", "input", "xyzPNumVar");
	}
}
