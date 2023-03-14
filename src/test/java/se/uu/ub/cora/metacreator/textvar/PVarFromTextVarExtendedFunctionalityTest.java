/*
 * Copyright 2016 Olov McKie
 * Copyright 2022 Uppsala University Library
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

package se.uu.ub.cora.metacreator.textvar;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.PVarFactory;
import se.uu.ub.cora.metacreator.spy.PVarFactorySpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class PVarFromTextVarExtendedFunctionalityTest {
	private DataFactorySpy dataFactory;
	private PVarFactorySpy pVarFactory;
	private DataGroupSpy dataGroup;
	private DataRecordGroupSpy dataRecordGroup;
	private SpiderInstanceFactorySpy spiderInstanceFactory;

	// private SpiderInstanceFactoryOldSpy instanceFactory;
	private String authToken;

	// private DataGroupFactory dataGroupFactory;
	// private DataAtomicFactory dataAtomicFactory;
	// private DataRecordLinkFactory dataRecordLinkFactory;
	private ExtendedFunctionality extendedFunctionality;
	private ExtendedFunctionalityData data;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		pVarFactory = new PVarFactorySpy();

		dataGroup = new DataGroupSpy();
		setUpRecordGroup();

		spiderInstanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);

		// dataGroupFactory = new DataGroupFactorySpy();
		// DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		// dataAtomicFactory = new DataAtomicFactorySpy();
		// DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		// dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		// DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		// instanceFactory = new SpiderInstanceFactoryOldSpy();
		// SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		authToken = "testUser";

		data = createExtendedFunctionalityWithDataGroupSpy();

		extendedFunctionality = PVarFromTextVarExtendedFunctionality.usingPVarFactory(pVarFactory);
	}

	private void setUpRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someVariableId");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
	}

	private ExtendedFunctionalityData createExtendedFunctionalityWithDataGroupSpy() {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		return data;
	}

	@Test
	public void testOnlyForTestGetPVarFactory() throws Exception {
		PVarFactory pVarFactory2 = ((PVarFromTextVarExtendedFunctionality) extendedFunctionality)
				.onlyForTestGetPVarFactory();
		assertSame(pVarFactory2, pVarFactory);
	}

	@Test
	public void testGroupChangedToRecordGroup() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, dataGroup);
	}

	@Test
	public void testInputIsCreated() throws Exception {
		extendedFunctionality.useExtendedFunctionality(data);
		var id = dataRecordGroup.MCR.getReturnValue("getId", 0);
		var dataDivider = dataRecordGroup.MCR.getReturnValue("getDataDivider", 0);

		pVarFactory.MCR.assertParameters("factorPVarUsingPresentationOfDataDividerAndMode", 0, id,
				dataDivider, "input");
	}

	// @Test
	// public void testNoExistingPVars() {
	// DataGroup textVarGroup = DataCreator.createTextVarGroupWithIdAndTextIdAndDefTextId(
	// "textIdNoPVarsInStorageTextVar", "textIdNoPVarsInStorageTextVarText",
	// "textIdNoPVarsInStorageTextVarDefText");
	//
	// callExtendedFunctionalityWithGroup(textVarGroup);
	//
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
	// assertCorrectPVarCreatedWithUserIdAndTypeAndId(0, "textIdNoPVarsInStoragePVar");
	// assertCorrectPVarCreatedWithUserIdAndTypeAndId(1, "textIdNoPVarsInStorageOutputPVar");
	// }
	//
	// private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
	// ExtendedFunctionalityData data = new ExtendedFunctionalityData();
	// data.authToken = authToken;
	// data.dataGroup = dataGroup;
	// extendedFunctionality.useExtendedFunctionality(data);
	// }
	//
	// private void assertCorrectPVarCreatedWithUserIdAndTypeAndId(int createdPVarNo,
	// String createdIdForPVar) {
	// SpiderRecordCreatorOldSpy spiderRecordCreator1 = instanceFactory.spiderRecordCreators
	// .get(createdPVarNo);
	// assertEquals(spiderRecordCreator1.authToken, authToken);
	// assertEquals(spiderRecordCreator1.type, "presentationVar");
	// DataGroup createdTextRecord = spiderRecordCreator1.record;
	// DataGroup recordInfo = createdTextRecord.getFirstGroupWithNameInData("recordInfo");
	// String id = recordInfo.getFirstAtomicValueWithNameInData("id");
	// assertEquals(id, createdIdForPVar);
	// }
	//
	// @Test
	// public void testExistingInputPVar() {
	// DataGroup textVarGroup = DataCreator.createTextVarGroupWithIdAndTextIdAndDefTextId(
	// "textIdInputPVarInStorageTextVar", "textIdInputPVarInStorageTextVarText",
	// "textIdInputPVarInStorageTextVarDefText");
	//
	// callExtendedFunctionalityWithGroup(textVarGroup);
	//
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 1);
	// assertCorrectPVarCreatedWithUserIdAndTypeAndId(0, "textIdInputPVarInStorageOutputPVar");
	// }
	//
	// @Test
	// public void testExistingOutputPVar() {
	// DataGroup textVarGroup = DataCreator.createTextVarGroupWithIdAndTextIdAndDefTextId(
	// "textIdOutputPVarInStorageTextVar", "textIdOutputPVarInStorageTextVarText",
	// "textIdOutputPVarInStorageTextVarDefText");
	//
	// callExtendedFunctionalityWithGroup(textVarGroup);
	//
	// assertEquals(instanceFactory.spiderRecordCreators.size(), 1);
	// assertCorrectPVarCreatedWithUserIdAndTypeAndId(0, "textIdOutputPVarInStoragePVar");
	// }
}
