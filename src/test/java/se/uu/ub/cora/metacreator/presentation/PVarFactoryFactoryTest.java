/*
 * Copyright 2023, 2026 Uppsala University Library
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
package se.uu.ub.cora.metacreator.presentation;

import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.MetadataIdToPresentationIdImp;

public class PVarFactoryFactoryTest {
	private PVarFactoryFactory fFactory;
	private DataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	private void beforeMethod() {
		fFactory = new PVarFactoryFactoryImp();
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "metadata");
	}

	@Test
	public void testNoFactoryShouldBeCreated() {
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "NOTmetadata");

		Optional<PVarFactory> pVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertTrue(pVarFactory.isEmpty());
	}

	@Test
	public void testNotMetadataShouldReturnEmptyOptional() {
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "NOTmetadata");
		setUpTypeAttributeWithValue("textVariable");

		Optional<PVarFactory> pVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertTrue(pVarFactory.isEmpty());
	}

	@Test
	public void testNameMetadataAndTypeUnknowShouldReturnEmptyOptional() {
		Optional<PVarFactory> pVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertTrue(pVarFactory.isEmpty());
	}

	@Test
	public void testNameMetadataAndTypeShouldReturnAPTextVarFactory() {
		setUpTypeAttributeWithValue("textVariable");

		Optional<PVarFactory> oPVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertOptionalPVarFactoryIsPresent(oPVarFactory);
		PTextVarFactoryImp pVarFactoryImp = (PTextVarFactoryImp) oPVarFactory.get();
		assertTrue(pVarFactoryImp
				.onlyForTestGetMetadataIdToPresentationId() instanceof MetadataIdToPresentationIdImp);
	}

	private void setUpTypeAttributeWithValue(String attributeTypeValue) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of(attributeTypeValue), "type");
	}

	private void assertOptionalPVarFactoryIsPresent(Optional<PVarFactory> oPVarFactory) {
		assertTrue(oPVarFactory.isPresent());
	}

	@Test
	public void testNameMetadataAndTypeShouldReturnAPNumberVarFactory() {
		setUpTypeAttributeWithValue("numberVariable");

		Optional<PVarFactory> oPVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertOptionalPVarFactoryIsPresent(oPVarFactory);
		assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePNumVarFactoryImp(oPVarFactory);
	}

	private void assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePNumVarFactoryImp(
			Optional<PVarFactory> oPVarFactory) {
		PNumVarFactoryImp pVarFactoryImp = (PNumVarFactoryImp) oPVarFactory.get();
		assertTrue(pVarFactoryImp
				.onlyForTestGetMetadataIdToPresentationId() instanceof MetadataIdToPresentationIdImp);
	}

	@Test
	public void testNameMetadataAndTypeShouldReturnAPCollectionVarFactory() {
		setUpTypeAttributeWithValue("collectionVariable");

		Optional<PVarFactory> oPVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertOptionalPVarFactoryIsPresent(oPVarFactory);
		assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePCollVarFactoryImp(oPVarFactory);
	}

	private void assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePCollVarFactoryImp(
			Optional<PVarFactory> oPVarFactory) {
		PCollVarFactoryImp pVarFactoryImp = (PCollVarFactoryImp) oPVarFactory.get();
		assertTrue(pVarFactoryImp
				.onlyForTestGetMetadataIdToPresentationId() instanceof MetadataIdToPresentationIdImp);
	}

	@Test
	public void testNameMetadataAndTypeShouldReturnAPRecordLinkFactory() {
		setUpTypeAttributeWithValue("recordLink");

		Optional<PVarFactory> oPVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertOptionalPVarFactoryIsPresent(oPVarFactory);
		assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePRecordLinkFactoryImp(
				oPVarFactory);
	}

	@Test
	public void testNameMetadataAndTypeShouldReturnAPRecordLinkFactory_ForAnyTypeRecordLink() {
		setUpTypeAttributeWithValue("anyTypeRecordLink");

		Optional<PVarFactory> oPVarFactory = fFactory.factorUsingRecordGroup(dataRecordGroup);

		assertOptionalPVarFactoryIsPresent(oPVarFactory);
		assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePRecordLinkFactoryImp(
				oPVarFactory);
	}

	private void assertFactoryHasAddedMetadataIdToPresentationIdAndIsOfTypePRecordLinkFactoryImp(
			Optional<PVarFactory> oPVarFactory) {
		PLinkFactoryImp pVarFactoryImp = (PLinkFactoryImp) oPVarFactory.get();
		assertTrue(pVarFactoryImp
				.onlyForTestGetMetadataIdToPresentationId() instanceof MetadataIdToPresentationIdImp);
	}

}
