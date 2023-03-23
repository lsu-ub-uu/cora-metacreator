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
package se.uu.ub.cora.metacreator.recordtype;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.spy.MetadataIdToPresentationIdSpy;

public class GroupFactoryTest {

	GroupFactory groupFactory;

	private static final String ID = "someId";
	private static final String NAME_IN_DATA = "someNameInData";
	private static final String DATA_DIVIDER = "someDataDivider";
	private static final String VALIDATION_TYPE = "metadataGroup";
	private static final String CHILD_REF_TO_RECORD_INFO = "someChildRefToRecordInfo";

	MetadataIdToPresentationIdSpy metadataIdToPresentationId;
	private DataFactorySpy dataFactory;

	@BeforeMethod
	public void setUp() {
		groupFactory = new GroupFactoryImp();

		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		metadataIdToPresentationId = new MetadataIdToPresentationIdSpy();
		metadataIdToPresentationId.MRV.setDefaultReturnValuesSupplier(
				"createPresentationIdUsingMetadataIdAndMode", () -> "spyCreatedId");
	}

	@Test
	public void testCreateGroup() throws Exception {
		DataRecordGroup returnDataRecordGroup = callFactory();

		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "metadata");
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, returnDataRecordGroup);
	}

	@Test
	public void testSetRecordInfo() throws Exception {
		callFactory();

		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);
		dataRecordGroup.MCR.assertParameters("setId", 0, ID);
		dataRecordGroup.MCR.assertParameters("setDataDivider", 0, DATA_DIVIDER);
		dataRecordGroup.MCR.assertParameters("setValidationType", 0, VALIDATION_TYPE);
	}

	@Test
	public void testAddChildReferences() throws Exception {
		callFactory();

		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);

		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0, "ref",
				"metadata", CHILD_REF_TO_RECORD_INFO);
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "repeatMin",
				"1");
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 1, "repeatMax",
				"1");
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 0, "childReference");
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 1, "childReferences");

		var recordInfoLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		var repeatMin = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		var repeatMax = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 1);

		DataGroupSpy childReference = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);
		DataGroupSpy childReferences = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 1);

		childReference.MCR.assertParameters("addChild", 0, recordInfoLink);
		childReference.MCR.assertParameters("addChild", 1, repeatMin);
		childReference.MCR.assertParameters("addChild", 2, repeatMax);
		childReference.MCR.assertParameters("setRepeatId", 0, "0");

		childReferences.MCR.assertParameters("addChild", 0, childReference);

		dataRecordGroup.MCR.assertParameters("addChild", 0, childReferences);

		// ExcludePGroupCreation shouldNotBeCalled
		dataRecordGroup.MCR.assertNumberOfCallsToMethod("addChild", 2);
		dataFactory.MCR.assertNumberOfCallsToMethod("factorAtomicUsingNameInDataAndValue", 3);

		// Add nameInData
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 2, "nameInData",
				NAME_IN_DATA);
		var nameInDataAtomic = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue",
				2);
		dataRecordGroup.MCR.assertParameters("addChild", 1, nameInDataAtomic);

		// Add attributeType
		dataRecordGroup.MCR.assertParameters("addAttributeByIdWithValue", 0, "type", "group");
	}

	@Test
	public void testExcludePGroupCreation() throws Exception {
		groupFactory.factorMetadataGroup(DATA_DIVIDER, ID, NAME_IN_DATA, CHILD_REF_TO_RECORD_INFO,
				true);

		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);

		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 3,
				"excludePGroupCreation", "true");
		var excludePGroupCreation = dataFactory.MCR
				.getReturnValue("factorAtomicUsingNameInDataAndValue", 3);
		dataRecordGroup.MCR.assertParameters("addChild", 2, excludePGroupCreation);

		dataFactory.MCR.assertNumberOfCallsToMethod("factorAtomicUsingNameInDataAndValue", 4);
	}

	private DataRecordGroup callFactory() {
		return groupFactory.factorMetadataGroup(DATA_DIVIDER, ID, NAME_IN_DATA,
				CHILD_REF_TO_RECORD_INFO, false);
	}
}
