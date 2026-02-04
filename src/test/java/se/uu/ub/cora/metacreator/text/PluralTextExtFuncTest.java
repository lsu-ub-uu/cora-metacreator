/*
 * Copyright 2017, 2022, 2024, 2026 Uppsala University Library
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

public class PluralTextExtFuncTest {
	private static final String AUTH_TOKEN = "someAuthToken";
	private SpiderInstanceFactorySpy instanceFactory;
	private DataFactorySpy dataFactory;
	private PluralTextExtFunc extendedFunctionality;
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
		extendedFunctionality = PluralTextExtFunc.usingTextFactory(textFactory);
	}

	private void setUpRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataFactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
				() -> dataRecordGroup);

		DataRecordLinkSpy pluralTextLink = new DataRecordLinkSpy();
		pluralTextLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "pluralTextLinkId");

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getId", () -> "someRecordId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> pluralTextLink, DataRecordLink.class, "pluralTextId");

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
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordReader", 1);

		RecordReaderSpy recordReaderTextId = (RecordReaderSpy) instanceFactory.MCR
				.getReturnValue("factorRecordReader", 0);
		recordReaderTextId.MCR.assertParameters("readRecord", 0, AUTH_TOKEN, "text",
				"pluralTextLinkId");
	}

	private void assertReadTextLinks() {
		dataRecordGroup.MCR.assertParameters("containsChildOfTypeAndName", 0, DataRecordLink.class,
				"pluralTextId");
		dataFactory.MCR.assertMethodNotCalled("factorRecordLinkUsingNameInDataAndTypeAndId");

		dataRecordGroup.MCR.assertParameters("getFirstChildOfTypeAndName", 0, DataRecordLink.class,
				"pluralTextId");
		DataRecordLinkSpy pluralLink = (DataRecordLinkSpy) dataRecordGroup.MCR
				.getReturnValue("getFirstChildOfTypeAndName", 0);
		pluralLink.MCR.assertParameters("getLinkedRecordId", 0);
	}

	@Test
	public void testNonExistingTexts() {
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("containsChildOfTypeAndName",
				() -> true);

		RecordReaderSpy recordReaderPluralSpy = new RecordReaderSpy();
		recordReaderPluralSpy.MRV.setAlwaysThrowException("readRecord",
				RecordNotFoundException.withMessage(""));
		instanceFactory.MRV.setReturnValues("factorRecordReader", List.of(recordReaderPluralSpy));

		callExtendedFunctionalityWithGroup(dataRecordGroup);

		assertReadTextLinks();
		assertReadStorage();
		assertCreateTexts();
		assertStoreInStorage();
	}

	private void assertCreateTexts() {
		textFactory.MCR.assertParameters("createTextUsingTextIdAndDataDividerId", 0,
				"pluralTextLinkId", "someDataDivider");
	}

	private void callExtendedFunctionalityWithGroup(DataRecordGroup dataRecordGroup) {
		ExtendedFunctionalityData data = new ExtendedFunctionalityData();
		data.authToken = AUTH_TOKEN;
		data.dataRecordGroup = dataRecordGroup;
		extendedFunctionality.useExtendedFunctionality(data);
	}

	private void assertStoreInStorage() {
		instanceFactory.MCR.assertNumberOfCallsToMethod("factorRecordCreator", 1);

		var textPluralRecordGroup = textFactory.MCR
				.getReturnValue("createTextUsingTextIdAndDataDividerId", 0);
		RecordCreatorSpy recordCreatorTextId = (RecordCreatorSpy) instanceFactory.MCR
				.getReturnValue("factorRecordCreator", 0);
		recordCreatorTextId.MCR.assertParameters("createAndStoreRecord", 0, AUTH_TOKEN, "text",
				textPluralRecordGroup);
	}

	@Test
	public void testNotExistingTextLinks() {
		callExtendedFunctionalityWithGroup(dataRecordGroup);

		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0,
				"pluralTextId", "text", "someRecordId" + "PluralText");
		var createdTextLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		dataRecordGroup.MCR.assertParameters("addChild", 0, createdTextLink);
	}

	@Test
	public void testOnlyForTestGetTextFactory() {
		assertSame(extendedFunctionality.onlyForTestGetTextFactory(), textFactory);
	}
}
