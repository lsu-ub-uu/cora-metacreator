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

import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.metacreator.spy.MetadataIdToPresentationIdSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
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

		authToken = "someAuthToken";
		metadataChildReferences = new ArrayList<>();
		metadataIdToPresentationId = new MetadataIdToPresentationIdSpy();
		metadataIdToPresentationId.MRV.setDefaultReturnValuesSupplier(
				"createPresentationIdUsingMetadataIdAndMode", () -> "spyCreatedId");

		DataGroupSpy childRefrencesGroup = new DataGroupSpy();
		childRefrencesGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
				() -> true);
		dataFactory.MRV.setSpecificReturnValuesSupplier("factorGroupUsingNameInData",
				() -> childRefrencesGroup, "childReferences");

		factory = PGroupFactoryImp.usingMetadataIdToPresentationId(metadataIdToPresentationId);
	}

	@Test
	public void testOnlyForTestGetMetadataIdToPresentationId() throws Exception {
		PGroupFactoryImp factoryImp = (PGroupFactoryImp) factory;
		assertSame(factoryImp.onlyForTestGetMetadataIdToPresentationId(),
				metadataIdToPresentationId);
	}

	@Test
	public void testGroupConstructorForInputWithGeneratedId() {
		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) factory
				.factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(
						authToken, dataDivider, presentationOf, mode, metadataChildReferences);

		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfoWithGeneratedId(recordGroup);
		assertCorrectPresentationOf(recordGroup, presentationOf);
		assertCorrectMode(recordGroup, mode);
		assertCorrectChildReferences(recordGroup);
	}

	private void assertCorrectRecordGroupCreated(DataRecordGroup recordGroup) {
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, recordGroup);
		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "presentation");
	}

	private void assertCorrectDataInRecordInfoWithGeneratedId(DataRecordGroupSpy recordGroup) {
		metadataIdToPresentationId.MCR.assertParameters(
				"createPresentationIdUsingMetadataIdAndMode", 0, "someTestGroup", "input");
		var id = metadataIdToPresentationId.MCR
				.getReturnValue("createPresentationIdUsingMetadataIdAndMode", 0);
		recordGroup.MCR.assertParameters("setId", 0, id);
		recordGroup.MCR.assertParameters("setDataDivider", 0, "someDataDivider");
		recordGroup.MCR.assertParameters("setValidationType", 0, "presentationGroup");
	}

	private void assertCorrectPresentationOf(DataRecordGroupSpy recordGroup,
			String presentationOf) {
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0,
				"presentationOf", "metadata", presentationOf);
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

	@Test
	public void testGroupConstructorForInputWithEnteredId() {
		String id = "someId";
		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) factory
				.factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences(
						authToken, id, dataDivider, presentationOf, mode, metadataChildReferences);

		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfoWithEnteredId(recordGroup, id);
		assertCorrectPresentationOf(recordGroup, presentationOf);
		assertCorrectMode(recordGroup, mode);
		assertCorrectChildReferences(recordGroup);
	}

	private void assertCorrectDataInRecordInfoWithEnteredId(DataRecordGroupSpy recordGroup,
			String id) {
		recordGroup.MCR.assertParameters("setId", 0, id);
		recordGroup.MCR.assertParameters("setDataDivider", 0, "someDataDivider");
		recordGroup.MCR.assertParameters("setValidationType", 0, "presentationGroup");
	}

	private DataGroupSpy createSpiesForChild1() {
		DataGroupSpy childReference = new DataGroupSpy();
		metadataChildReferences.add(childReference);

		DataRecordLinkSpy refLink1 = new DataRecordLinkSpy();
		childReference.MRV.setDefaultReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> refLink1);
		refLink1.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someLinkedRecordId1");
		return childReference;
	}

	@Test
	public void testGroupConstructorForInputChildrenPresentationForFirstChild() {
		createSpiesForChild1();
		DataRecordSpy dataRecordSpy = createDataRecordSpyForMetadataToReadTextId();
		recordReaderSpy.MRV.setSpecificReturnValuesSupplier("readRecord", () -> dataRecordSpy,
				authToken, "metadata", "someLinkedRecordId1");

		factory.factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(authToken,
				dataDivider, presentationOf, mode, metadataChildReferences);

		instanceFactory.MCR.assertParameters("factorRecordReader", 0);
		RecordReaderSpy recordReader = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);

		metadataIdToPresentationId.MCR.assertParameters(
				"createPresentationIdUsingMetadataIdAndMode", 0, presentationOf, mode);
		Object presentationId = metadataIdToPresentationId.MCR
				.getReturnValue("createPresentationIdUsingMetadataIdAndMode", 0);

		// recordReader.MCR.assertParameters("readRecord", 0, authToken, "metadata",
		// "someLinkedRecordId1");
		recordReader.MCR.assertParameters("readRecord", 0, authToken, "presentation",
				presentationId);

		// // childReference refGroup ref (type text, presentation)
		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 0, "childReferences");

		// childReference
		DataGroupSpy presentationReferenceGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 1);
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 1, "childReference");
		presentationReferenceGroup.MCR.assertParameters("setRepeatId", 0, "0");
		childReferencesGroup.MCR.assertParameters("addChild", 0, presentationReferenceGroup);

		// refGroup
		DataGroupSpy presentationRefGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 2);
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 2, "refGroup");
		presentationRefGroup.MCR.assertParameters("setRepeatId", 0, "0");
		presentationReferenceGroup.MCR.assertParameters("addChild", 0, presentationRefGroup);

		// presentationLink
		DataRecordLinkSpy presentationRefLink = (DataRecordLinkSpy) dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 1);
		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 1, "ref",
				"presentation", presentationId);
		presentationRefLink.MCR.assertParameters("addAttributeByIdWithValue", 0, "type",
				"presentation");
		presentationRefGroup.MCR.assertParameters("addChild", 0, presentationRefLink);
	}

	@Test
	public void testGroupConstructorForInputChildrenPresentationTextIsMissingInStorage() {
		recordReaderSpy.MRV.setThrowException("readRecord",
				RecordNotFoundException.withMessage("Record not found"), authToken, "text",
				"someLinkedRecordId1" + "Text");
		createSpiesForChild1();

		factory.factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(authToken,
				dataDivider, presentationOf, mode, metadataChildReferences);

		DataGroupSpy childReferencesGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);

		childReferencesGroup.MCR.assertNumberOfCallsToMethod("addChild", 1);
	}

	private DataRecordSpy createDataRecordSpyForMetadataToReadTextId() {
		DataRecordSpy recordSpy = new DataRecordSpy();
		DataGroupSpy recordGroup = new DataGroupSpy();
		recordSpy.MRV.setDefaultReturnValuesSupplier("getDataGroup", () -> recordGroup);

		DataRecordLinkSpy refLink1 = new DataRecordLinkSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> refLink1);
		refLink1.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "someLinkedTextId1");
		return recordSpy;
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

		factory.factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(authToken,
				dataDivider, presentationOf, mode, metadataChildReferences);
	}
}
