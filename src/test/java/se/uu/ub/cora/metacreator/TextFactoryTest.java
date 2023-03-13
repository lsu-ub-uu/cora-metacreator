/*
 * Copyright 2016, 2023 Olov McKie
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class TextFactoryTest {
	private DataFactorySpy dataFactory;
	private TextFactory factory;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		factory = new TextFactoryImp();
	}

	@Test
	public void testTextFactory() {
		DataRecordGroup text = factory.createTextUsingTextIdAndDataDividerId("someTextId",
				"someDataDivider");

		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) text;
		assertCorrectRecordGroupCreated(recordGroup);
		assertCorrectDataInRecordInfo(recordGroup);
		assertCorrectTextPartSv(recordGroup);
		assertCorrectTextPartEn(recordGroup);
	}

	private void assertCorrectRecordGroupCreated(DataRecordGroup recordGroup) {
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, recordGroup);
		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "text");
	}

	private void assertCorrectDataInRecordInfo(DataRecordGroupSpy recordGroup) {
		recordGroup.MCR.assertParameters("setId", 0, "someTextId");
		recordGroup.MCR.assertParameters("setDataDivider", 0, "someDataDivider");
		recordGroup.MCR.assertParameters("setValidationType", 0, "coraText");
	}

	private void assertCorrectTextPartSv(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 0, "textPart");
		DataGroupSpy textPartGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);
		textPartGroup.MCR.assertParameters("addAttributeByIdWithValue", 0, "type", "default");
		textPartGroup.MCR.assertParameters("addAttributeByIdWithValue", 1, "lang", "sv");

		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "text",
				"Text för:someTextId");
		var textSv = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		textPartGroup.MCR.assertParameters("addChild", 0, textSv);
		recordGroup.MCR.assertParameters("addChild", 0, textPartGroup);
	}

	private void assertCorrectTextPartEn(DataRecordGroupSpy recordGroup) {
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 1, "textPart");
		DataGroupSpy textPartGroup = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 1);
		textPartGroup.MCR.assertParameters("addAttributeByIdWithValue", 0, "type", "alternative");
		textPartGroup.MCR.assertParameters("addAttributeByIdWithValue", 1, "lang", "en");

		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 1, "text",
				"Text for:someTextId");
		var textSv = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 1);
		textPartGroup.MCR.assertParameters("addChild", 0, textSv);
		recordGroup.MCR.assertParameters("addChild", 1, textPartGroup);
	}
}
