/*
 * Copyright 2017, 2022 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactoryOldSpy;
import se.uu.ub.cora.metacreator.dependency.SpiderRecordCreatorOldSpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class CollectionVarFromItemCollectionExtFuncTest {
	private SpiderInstanceFactoryOldSpy instanceFactory;
	private String authToken;
	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private CollectionVarFromItemCollectionExtFunc extendedFunctionality;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		instanceFactory = new SpiderInstanceFactoryOldSpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		authToken = "testUser";
		extendedFunctionality = new CollectionVarFromItemCollectionExtFunc();
	}

	@Test
	public void testCollectionDoesNotExist() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("someCollection");
		callExtendedFunctionalityWithGroup(itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 1);

		SpiderRecordCreatorOldSpy spiderRecordCreatorSpy = instanceFactory.spiderRecordCreators
				.get(0);
		assertEquals(spiderRecordCreatorSpy.type, "metadataCollectionVariable");

		DataGroup record = spiderRecordCreatorSpy.record;
		assertEquals(record.getNameInData(), "metadata");
		assertCorrectRecordInfo(record);
		assertCorrectRefCollection(record);
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	private void assertCorrectRecordInfo(DataGroup record) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "someCollectionVar");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "test");
	}

	private void assertCorrectRefCollection(DataGroup record) {
		DataGroup refCollection = record.getFirstGroupWithNameInData("refCollection");
		assertEquals(refCollection.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataItemCollection");
		assertEquals(refCollection.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someCollection");
	}

	@Test
	public void testCollectionAlreadyExist() {
		DataGroup itemCollection = DataCreator.createItemCollectionWithId("alreadyExistCollection");
		callExtendedFunctionalityWithGroup(itemCollection);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);

	}
}
