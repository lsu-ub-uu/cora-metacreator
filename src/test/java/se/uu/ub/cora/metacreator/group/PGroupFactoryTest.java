/*
 * Copyright 2018, 2023 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metacreator.spy.MetadataIdToPresentationIdSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class PGroupFactoryTest {
	private DataFactorySpy dataFactory;

	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;
	List<DataGroup> metadataChildReferences;
	MetadataIdToPresentationIdSpy metadataIdToPresentationId;

	private PGroupFactory factory;
	String id = "someTestPGroup";
	String dataDivider = "someDataDivider";
	String presentationOf = "someTestGroup";
	String mode = "input";

	private RecordReaderSpy recordReaderSpy;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		recordReaderSpy = new RecordReaderSpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
				() -> recordReaderSpy);

		authToken = "testUser";
		metadataChildReferences = new ArrayList<>();
		metadataIdToPresentationId = new MetadataIdToPresentationIdSpy();
		metadataIdToPresentationId.MRV.setDefaultReturnValuesSupplier(
				"createPresentationIdUsingMetadataIdAndMode", () -> "spyCreatedId");

		DataGroupSpy childRefrencesGroup = new DataGroupSpy();
		childRefrencesGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				() -> true);
		dataFactory.MRV.setSpecificReturnValuesSupplier("factorGroupUsingNameInData",
				() -> childRefrencesGroup, "childReferences");

		factory = PGroupFactoryImp.usingAuthTokenAndMetadataToPresentationId(authToken,
				metadataIdToPresentationId);
	}

	@Test
	public void testGroupConstructorForInput() {
		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) factory
				.factorPGroupWithDataDividerPresentationOfModeAndChildren(dataDivider,
						presentationOf, mode, metadataChildReferences);

		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfo(recordGroup);
		assertCorrectPresentationOf(recordGroup, presentationOf);
		assertCorrectMode(recordGroup, mode);

		assertCorrectChildReferences(recordGroup);
	}

	@Test
	public void testGroupConstructorForInputChildrenTextForFirstChild() {
		DataGroupSpy child1 = createSpiesForChild1();

		factory.factorPGroupWithDataDividerPresentationOfModeAndChildren(dataDivider,
				presentationOf, mode, metadataChildReferences);

		child1.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataRecordLink.class, "ref");

		instanceFactory.MCR.assertParameters("factorRecordReader", 0);
		RecordReaderSpy recordReader = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);
		String expectedTextId = "someLinkedRecordId1" + "Text";
		recordReader.MCR.assertParameters("readRecord", 0, authToken, "text", expectedTextId);
		// childReference refGroup ref (type text, presentation)

		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);
		// childReference
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 1, "childReference");
		DataGroupSpy referenceGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 1);
		childReferencesGroup.MCR.assertParameters("addChild", 0, referenceGroup);
		referenceGroup.MCR.assertParameters("setRepeatId", 0, "0");

		// refGroup
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 2, "refGroup");
		DataGroupSpy refGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 2);
		referenceGroup.MCR.assertParameters("addChild", 0, refGroup);
		refGroup.MCR.assertParameters("setRepeatId", 0, "0");

		// textLink
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 1, "ref",
				"text", expectedTextId);
		DataRecordLinkSpy textLink = (DataRecordLinkSpy) dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 1);
		refGroup.MCR.assertParameters("addChild", 0, textLink);
		textLink.MCR.assertParameters("addAttributeByIdWithValue", 0, "type", "text");
	}

	@Test
	public void testGroupConstructorForInputChildrenPresentationForFirstChild() {
		createSpiesForChild1();

		factory.factorPGroupWithDataDividerPresentationOfModeAndChildren(dataDivider,
				presentationOf, mode, metadataChildReferences);

		instanceFactory.MCR.assertParameters("factorRecordReader", 0);
		RecordReaderSpy recordReader = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);

		metadataIdToPresentationId.MCR.assertParameters(
				"createPresentationIdUsingMetadataIdAndMode", 0, presentationOf, mode);
		Object presentationId = metadataIdToPresentationId.MCR
				.getReturnValue("createPresentationIdUsingMetadataIdAndMode", 0);

		recordReader.MCR.assertParameters("readRecord", 1, authToken, "presentation",
				presentationId);

		// // childReference refGroup ref (type text, presentation)
		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);
		// childReference
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 3, "childReference");
		DataGroupSpy referenceGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 3);
		childReferencesGroup.MCR.assertParameters("addChild", 1, referenceGroup);
		referenceGroup.MCR.assertParameters("setRepeatId", 0, "1");

		// refGroup
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 4, "refGroup");
		DataGroupSpy refGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 4);
		referenceGroup.MCR.assertParameters("addChild", 0, refGroup);
		refGroup.MCR.assertParameters("setRepeatId", 0, "0");

		// presentationLink
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 2, "ref",
				"presentation", presentationId);
		DataRecordLinkSpy presentationLink = (DataRecordLinkSpy) dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 2);
		refGroup.MCR.assertParameters("addChild", 0, presentationLink);
		presentationLink.MCR.assertParameters("addAttributeByIdWithValue", 0, "type",
				"presentation");
	}

	private DataGroupSpy createSpiesForChild1() {
		DataGroupSpy child1 = new DataGroupSpy();
		metadataChildReferences.add(child1);
		DataRecordLinkSpy refLink1 = new DataRecordLinkSpy();
		child1.MRV.setDefaultReturnValuesSupplier("getFirstChildOfTypeAndName", () -> refLink1);
		refLink1.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someLinkedRecordId1");
		return child1;
	}

	@Test
	public void testGroupConstructorForInputChildrenPresentationTextIsMissingInStorage() {
		recordReaderSpy.MRV.setThrowException("readRecord",
				new RecordNotFoundException("Record not found"), authToken, "text",
				"someLinkedRecordId1" + "Text");
		createSpiesForChild1();

		factory.factorPGroupWithDataDividerPresentationOfModeAndChildren(dataDivider,
				presentationOf, mode, metadataChildReferences);

		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);

		childReferencesGroup.MCR.assertNumberOfCallsToMethod("addChild", 1);
	}

	@Test
	public void testGroupConstructorForInputChildrenPresentationPresentationIsMissingInStorage() {
		recordReaderSpy.MRV.setThrowException("readRecord",
				new RecordNotFoundException("Record not found"), authToken, "presentation",
				"spyCreatedId");
		createSpiesForChild1();

		factory.factorPGroupWithDataDividerPresentationOfModeAndChildren(dataDivider,
				presentationOf, mode, metadataChildReferences);

		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);

		childReferencesGroup.MCR.assertNumberOfCallsToMethod("addChild", 1);
	}

	private void assertCorrectRecordGroupCreated(DataRecordGroup recordGroup) {
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, recordGroup);
		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "presentation");
	}

	private void assertCorrectDataInRecordInfo(DataRecordGroupSpy recordGroup) {
		metadataIdToPresentationId.MCR.assertParameters(
				"createPresentationIdUsingMetadataIdAndMode", 0, "someTestGroup", "input");
		var pVarId = metadataIdToPresentationId.MCR
				.getReturnValue("createPresentationIdUsingMetadataIdAndMode", 0);
		recordGroup.MCR.assertParameters("setId", 0, pVarId);
		// recordGroup.MCR.assertParameters("setId", 0, "someTestPGroup");
		recordGroup.MCR.assertParameters("setDataDivider", 0, "someDataDivider");
		recordGroup.MCR.assertParameters("setValidationType", 0, "presentationGroup");
	}

	private void assertCorrectPresentationOf(DataRecordGroupSpy recordGroup,
			String presentationOf) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0,
				"presentationOf", "metadataGroup", presentationOf);
		var presentationOfLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		recordGroup.MCR.assertParameters("addChild", 0, presentationOfLink);
	}

	private void assertCorrectMode(DataRecordGroupSpy recordGroup, String mode) {
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "mode", mode);
		var modeGroup = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		recordGroup.MCR.assertParameters("addChild", 1, modeGroup);
	}

	private void assertCorrectChildReferences(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 0, "childReferences");
		var childReferencesGroup = dataFactory.MCR.getReturnValue("factorGroupUsingNameInData", 0);
		recordGroup.MCR.assertParameters("addChild", 2, childReferencesGroup);
	}

	@Test(expectedExceptions = DataException.class, expectedExceptionsMessageRegExp = ""
			+ "No children were possible to add to presentationGroup for id spyCreatedId and"
			+ " presentationOf someTestGroup")
	public void testGroupConstructorWithNoIdentifiedChildren() {

		DataGroupSpy childRefrencesGroup = new DataGroupSpy();
		childRefrencesGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				() -> false);
		dataFactory.MRV.setSpecificReturnValuesSupplier("factorGroupUsingNameInData",
				() -> childRefrencesGroup, "childReferences");

		createSpiesForChild1();

		factory.factorPGroupWithDataDividerPresentationOfModeAndChildren(dataDivider,
				presentationOf, mode, metadataChildReferences);
	}
}
