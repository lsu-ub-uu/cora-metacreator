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
package se.uu.ub.cora.metacreator.recordtype;

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
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class SearchFromRecordTypeCreatorTest {

	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private SearchFromRecordTypeCreator extendedFunctionality;

	@BeforeMethod
	public void setup() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		authToken = "someToken";
		extendedFunctionality = new SearchFromRecordTypeCreator();
	}

	@Test
	public void testUseExtendedFunctionality() {
		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordType");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordType");

		callExtendedFunctionalityWithGroup(recordType);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 1);
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = authToken;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	@Test
	public void testUseExtendedFunctionalitySearchAlreadyExists() {
		DataGroup recordType = DataCreator.createDataGroupForRecordTypeWithId("myRecordTypeExists");
		DataCreator.addAllValuesToDataGroup(recordType, "myRecordTypeExists");

		callExtendedFunctionalityWithGroup(recordType);

		assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}
}
