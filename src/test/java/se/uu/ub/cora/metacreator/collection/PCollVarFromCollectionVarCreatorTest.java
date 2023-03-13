/*
 * Copyright 2017, 2022, 2023 Uppsala University Library
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

import static org.testng.Assert.assertSame;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.spy.PVarFactorySpy;
import se.uu.ub.cora.metacreator.spy.RecordCreatorSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class PCollVarFromCollectionVarCreatorTest {
	private static final String COLLECTION_VAR = "CollectionVar";
	private String authToken;
	private String collectionIdFirstPart = "someCollVarId";

	private DataFactorySpy dataFactory;

	private PVarFactorySpy pCollVarFactory;
	private DataRecordGroupSpy dataRecordGroup;

	private PCollVarFromCollectionVarCreator extendedFunctionality;
	private SpiderInstanceFactorySpy spiderInstanceFactory;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		setUpRecordGroup();
		spiderInstanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);

		authToken = "someAuthToken";
		pCollVarFactory = new PVarFactorySpy();
		extendedFunctionality = PCollVarFromCollectionVarCreator
				.usingPCollVarFactory(pCollVarFactory);
	}

	private void setUpRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId",
				() -> collectionIdFirstPart + COLLECTION_VAR);
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
	}

	@Test
	public void testOnlyForTestGetPCollVarFactory() throws Exception {
		assertSame(extendedFunctionality.onlyForTestGetPCollVarFactory(), pCollVarFactory);
	}

	@Test
	public void testPresentationsExist() throws Exception {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();

		callExtendedFunctionalityWithGroup(dataGroupSpy);

		dataFactory.MCR.assertParameters("factorRecordGroupFromDataGroup", 0, dataGroupSpy);
		spiderInstanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 2);
		RecordReaderSpy recordReaderSpy = (RecordReaderSpy) spiderInstanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);
		recordReaderSpy.MCR.assertParameters("readRecord", 0, authToken, "presentation",
				collectionIdFirstPart + "PCollVar");

		RecordReaderSpy recordReaderOutputSpy = (RecordReaderSpy) spiderInstanceFactory.MCR
				.getReturnValue("factorRecordReader", 1);
		recordReaderOutputSpy.MCR.assertParameters("readRecord", 0, authToken, "presentation",
				collectionIdFirstPart + "OutputPCollVar");
	}

	@Test
	public void testPresentationsDoNotExist() throws Exception {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		RecordReaderSpy recordReaderSpy = new RecordReaderSpy();
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord", new RuntimeException());
		RecordReaderSpy recordReaderOutputSpy = new RecordReaderSpy();
		recordReaderOutputSpy.MRV.setAlwaysThrowException("readRecord", new RuntimeException());
		spiderInstanceFactory.MRV.setReturnValues("factorRecordReader",
				List.of(recordReaderSpy, recordReaderOutputSpy));

		callExtendedFunctionalityWithGroup(dataGroupSpy);

		pCollVarFactory.MCR.assertParameters("factorPVarUsingPresentationOfDataDividerAndMode", 0,
				collectionIdFirstPart + COLLECTION_VAR, "someDataDivider", "input");

		pCollVarFactory.MCR.assertParameters("factorPVarUsingPresentationOfDataDividerAndMode", 1,
				collectionIdFirstPart + COLLECTION_VAR, "someDataDivider", "output");

		spiderInstanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 2);

		RecordCreatorSpy recordCreatorSpy = (RecordCreatorSpy) spiderInstanceFactory.MCR
				.getReturnValue("factorRecordCreator", 0);
		var pCollVarRecordGroup = pCollVarFactory.MCR
				.getReturnValue("factorPVarUsingPresentationOfDataDividerAndMode", 0);
		dataFactory.MCR.assertParameters("factorGroupFromDataRecordGroup", 0, pCollVarRecordGroup);
		var pCollVar = dataFactory.MCR.getReturnValue("factorGroupFromDataRecordGroup", 0);
		recordCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0, authToken,
				"presentationCollectionVar", pCollVar);

		RecordCreatorSpy recordCreatorOutputSpy = (RecordCreatorSpy) spiderInstanceFactory.MCR
				.getReturnValue("factorRecordCreator", 1);
		var pCollVarOutputRecordGroup = pCollVarFactory.MCR
				.getReturnValue("factorPVarUsingPresentationOfDataDividerAndMode", 1);
		dataFactory.MCR.assertParameters("factorGroupFromDataRecordGroup", 1,
				pCollVarOutputRecordGroup);
		var pOutputCollVar = dataFactory.MCR.getReturnValue("factorGroupFromDataRecordGroup", 1);
		recordCreatorOutputSpy.MCR.assertParameters("createAndStoreRecord", 0, authToken,
				"presentationCollectionVar", pOutputCollVar);
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}
}
