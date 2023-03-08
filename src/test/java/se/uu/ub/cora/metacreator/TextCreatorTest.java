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
package se.uu.ub.cora.metacreator;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metacreator.spy.RecordReaderSpy;
import se.uu.ub.cora.metacreator.spy.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.spy.TextFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class TextCreatorTest {
	private static final String AUTH_TOKEN = "someAuthToken";
	private SpiderInstanceFactorySpy instanceFactory;
	private DataFactorySpy dataFactory;
	private TextCreator extendedFunctionality;
	private TextFactorySpy textFactory;
	private DataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		setUpRecordGroup();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		textFactory = new TextFactorySpy();
		extendedFunctionality = TextCreator.usingTextFactory(textFactory);
	}

	private void setUpRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		DataRecordLinkSpy textLink = new DataRecordLinkSpy();
		textLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "textLinkId");
		DataRecordLinkSpy defTextLink = new DataRecordLinkSpy();
		defTextLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "defTextLinkId");

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildWithNameInData",
				() -> textLink, "textId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildWithNameInData",
				() -> defTextLink, "defTextId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstChildOfTypeWithNameAndAttributes", () -> textLink, DataRecordLink.class,
				"textId", new DataAttribute[0]);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstChildOfTypeWithNameAndAttributes", () -> textLink, DataRecordLink.class,
				"defTextId", new DataAttribute[0]);
		// dataRecordGroup.MRV.setDefaultReturnValuesSupplier(
		// "getFirstChildOfTypeWithNameAndAttributes", () -> textLink);

		// dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId",
		// () -> collectionIdFirstPart + COLLECTION_VAR);
		// dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
		// () -> "someDataDivider");
	}

	@Test
	public void testWithExistingTextsInStorage() {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();

		callExtendedFunctionalityWithGroup(dataGroupSpy);

		// dataRecordGroup.MCR.assertParameters("getFirstChildWithNameInData", 0, "textId");
		// DataRecordLinkSpy textLink = (DataRecordLinkSpy) dataRecordGroup.MCR
		// .getReturnValue("getFirstChildWithNameInData", 0);
		dataRecordGroup.MCR.assertParameters("getFirstChildOfTypeWithNameAndAttributes", 0,
				DataRecordLink.class, "textId");
		DataRecordLinkSpy textLink = (DataRecordLinkSpy) dataRecordGroup.MCR
				.getReturnValue("getFirstChildOfTypeWithNameAndAttributes", 0);
		textLink.MCR.assertParameters("getLinkedRecordId", 0);

		dataRecordGroup.MCR.assertParameters("getFirstChildWithNameInData", 1, "defTextId");
		textLink.MCR.assertParameters("getLinkedRecordId", 0);

		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 2);
		RecordReaderSpy recordReaderTextId = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);
		recordReaderTextId.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "text", "textLinkId");

		RecordReaderSpy recordReaderDefTextId = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 1);
		recordReaderDefTextId.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "text",
				"defTextLinkId");

		textFactory.MCR.assertMethodNotCalled("createTextUsingTextIdAndDataDividerId");

		// DataGroup item = DataCreator
		// .createCollectionItemGroupWithIdTextIdDefTextIdAndImplementingTextType("firstItem",
		// "someExistingTextId", "someExistingDefTextId", "textSystemOne");
		// assertEquals(instanceFactory.spiderRecordCreators.size(), 0);
	}

	@Test
	public void testNonExistingTexts() {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		RecordReaderSpy recordReaderSpy = new RecordReaderSpy();
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord", new RecordNotFoundException(""));
		RecordReaderSpy recordReaderDefSpy = new RecordReaderSpy();
		recordReaderDefSpy.MRV.setAlwaysThrowException("readRecord",
				new RecordNotFoundException(""));
		instanceFactory.MRV.setReturnValues("factorRecordReader",
				List.of(recordReaderSpy, recordReaderDefSpy));

		// DataGroup item = DataCreator
		// .createCollectionItemGroupWithIdTextIdDefTextIdAndImplementingTextType("firstItem",
		// "nonExistingText", "nonExistingDefText", "textSystemOne");

		callExtendedFunctionalityWithGroup(dataGroupSpy);

		textFactory.MCR.assertParameters("createTextUsingTextIdAndDataDividerId", 0);

		// assertEquals(instanceFactory.spiderRecordCreators.size(), 2);
		// assertCorrectTextCreatedWithUserIdAndTypeAndId(0, "nonExistingText");
		// assertCorrectTextCreatedWithUserIdAndTypeAndId(1, "nonExistingDefText");
	}

	private void callExtendedFunctionalityWithGroup(DataGroup dataGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataGroup = dataGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	// private void assertCorrectTextCreatedWithUserIdAndTypeAndId(int createdTextNo,
	// String createdIdForText) {
	// SpiderRecordCreatorOldSpy spiderRecordCreator = instanceFactory.spiderRecordCreators
	// .get(createdTextNo);
	// assertEquals(spiderRecordCreator.authToken, AUTH_TOKEN);
	// assertEquals(spiderRecordCreator.type, "textSystemOne");
	// DataGroup createdTextRecord = spiderRecordCreator.record;
	// DataGroup recordInfo = createdTextRecord.getFirstGroupWithNameInData("recordInfo");
	// String id = recordInfo.getFirstAtomicValueWithNameInData("id");
	// assertEquals(id, createdIdForText);
	// }

}
