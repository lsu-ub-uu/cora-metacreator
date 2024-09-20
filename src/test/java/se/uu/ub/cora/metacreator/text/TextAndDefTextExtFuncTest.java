/*
 * Copyright 2017, 2022, 2024 Uppsala University Library
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
package se.uu.ub.cora.metacreator.text;

import static org.testng.Assert.assertSame;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metacreator.spy.TextFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.spies.RecordCreatorSpy;
import se.uu.ub.cora.spider.spies.RecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class TextAndDefTextExtFuncTest {
	private static final String AUTH_TOKEN = "someAuthToken";
	private SpiderInstanceFactorySpy instanceFactory;
	private DataFactorySpy dataFactory;
	private TextAndDefTextExtFunc extendedFunctionality;
	private TextFactorySpy textFactory;
	private DataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		setUpRecordGroup();

		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);

		textFactory = new TextFactorySpy();
		extendedFunctionality = TextAndDefTextExtFunc.usingTextFactory(textFactory);
	}

	private void setUpRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		DataRecordLinkSpy textLink = new DataRecordLinkSpy();
		textLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "textLinkId");

		DataRecordLinkSpy defTextLink = new DataRecordLinkSpy();
		defTextLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> "defTextLinkId");

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getId", () -> "someRecordId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> textLink, DataRecordLink.class, "textId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> defTextLink, DataRecordLink.class, "defTextId");

		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
	}

	@Test
	public void testWithExistingTextsInStorage() {
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("containsChildOfTypeAndName",
				() -> true);

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		assertReadTextLinks();
		assertReadStorage();

		textFactory.MCR.assertMethodNotCalled("createTextUsingTextIdAndDataDividerId");
	}

	private void assertReadStorage() {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 2);

		RecordReaderSpy recordReaderTextId = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);
		recordReaderTextId.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "text", "textLinkId");

		RecordReaderSpy recordReaderDefTextId = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 1);
		recordReaderDefTextId.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "text",
				"defTextLinkId");
	}

	private void assertReadTextLinks() {
		dataRecordGroup.MCR.assertParameters("containsChildOfTypeAndName", 0, DataRecordLink.class,
				"textId");
		dataFactory.MCR.assertMethodNotCalled("factorRecordLinkUsingNameInDataAndTypeAndId");

		dataRecordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataRecordLink.class,
				"textId");
		DataRecordLinkSpy textLink = (DataRecordLinkSpy) dataRecordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);
		textLink.MCR.assertParameters("getLinkedRecordId", 0);

		dataRecordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 1, DataRecordLink.class,
				"defTextId");
		DataRecordLinkSpy defTextId = (DataRecordLinkSpy) dataRecordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);
		defTextId.MCR.assertParameters("getLinkedRecordId", 0);
	}

	@Test
	public void testNonExistingTexts() {
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("containsChildOfTypeAndName",
				() -> true);

		RecordReaderSpy recordReaderSpy = new RecordReaderSpy();
		recordReaderSpy.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage(""));
		RecordReaderSpy recordReaderDefSpy = new RecordReaderSpy();
		recordReaderDefSpy.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage(""));
		instanceFactory.MRV.setReturnValues("factorRecordReader",
				List.of(recordReaderSpy, recordReaderDefSpy));

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		assertReadTextLinks();
		assertReadStorage();
		assertCreateTexts();
		assertStoreInStorage();
	}

	private void assertCreateTexts() {
		textFactory.MCR.assertParameters("createTextUsingTextIdAndDataDividerId", 0, "textLinkId",
				"someDataDivider");
		textFactory.MCR.assertParameters("createTextUsingTextIdAndDataDividerId", 1,
				"defTextLinkId", "someDataDivider");
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroup dataRecordGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataRecordGroup = dataRecordGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	private void assertStoreInStorage() {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 2);

		var textRecordGroup = textFactory.MCR
				.getReturnValue("createTextUsingTextIdAndDataDividerId", 0);
		RecordCreatorSpy recordCreatorTextId = (RecordCreatorSpy) instanceFactory.MCR
				.getReturnValue("factorRecordCreator", 0);
		recordCreatorTextId.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "text",
				textRecordGroup);

		var defTextRecordGroup = textFactory.MCR
				.getReturnValue("createTextUsingTextIdAndDataDividerId", 1);
		RecordCreatorSpy recordCreatorDefTextId = (RecordCreatorSpy) instanceFactory.MCR
				.getReturnValue("factorRecordCreator", 1);
		recordCreatorDefTextId.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "text",
				defTextRecordGroup);
	}

	@Test
	public void testNotExistingTextLinks() throws Exception {
		callExtendedFunctionalityWithGroup(dataRecordGroup);

		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0, "textId",
				"text", "someRecordId" + "Text");
		var createdTextLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		dataRecordGroup.MCR.assertParameters("addChild", 0, createdTextLink);

		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 1,
				"defTextId", "text", "someRecordId" + "DefText");
		var createdDefTextLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 1);
		dataRecordGroup.MCR.assertParameters("addChild", 1, createdDefTextLink);
	}

	@Test
	public void testOnlyForTestGetTextFactory() throws Exception {
		assertSame(extendedFunctionality.onlyForTestGetTextFactory(), textFactory);
	}
}
