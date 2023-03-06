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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataCreatorHelperSpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;

public class TextConstructorTest {
	private DataFactorySpy dataFactory;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private DataCreatorHelperSpy dataCreatorHelper;

	private TextConstructor textConstructor;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);

		dataCreatorHelper = new DataCreatorHelperSpy();
		textConstructor = TextConstructor.usingDataCreatorHelper(dataCreatorHelper);
	}

	@Test
	public void testOnlyForTestGetDataCreatorHelper() throws Exception {
		DataCreatorHelper creatorHelper = textConstructor.onlyForTestGetDataCreatorHelper();
		assertSame(creatorHelper, dataCreatorHelper);
	}

	@Test
	public void testCreateTextsFromMetadataId() {
		DataGroup createdText = textConstructor
				.createTextUsingTextIdAndDataDividerIdAndValidationTypeId("someTextVar",
						"someDataDivider", "coraText");
		assertEquals(createdText.getNameInData(), "text");

		assertEquals(createdText.getChildren().size(), 3);

		assertCorrectRecordInfo(createdText);

		DataGroup textPart1 = (DataGroup) createdText.getChildren().get(1);
		assertEquals(textPart1.getChildren().size(), 1);
		assertEquals(textPart1.getAttributes().size(), 2);
		assertEquals(textPart1.getAttribute("type").getValue(), "default");
		assertEquals(textPart1.getAttribute("lang").getValue(), "sv");
		assertEquals(textPart1.getFirstAtomicValueWithNameInData("text"), "Text för:someTextVar");

		DataGroup textPart2 = (DataGroup) createdText.getChildren().get(2);
		assertEquals(textPart2.getChildren().size(), 1);
		assertEquals(textPart2.getAttributes().size(), 2);
		assertEquals(textPart2.getAttribute("type").getValue(), "alternative");
		assertEquals(textPart2.getAttribute("lang").getValue(), "en");
		assertEquals(textPart2.getFirstAtomicValueWithNameInData("text"), "Text for:someTextVar");
	}

	private void assertCorrectRecordInfo(DataGroup createdText) {
		DataGroup recordInfo = createdText.getFirstGroupWithNameInData("recordInfo");
		String methodName = "createRecordInfoWithIdAndDataDividerAndValidationType";
		dataCreatorHelper.MCR.assertReturn(methodName, 0, recordInfo);
		dataCreatorHelper.MCR.assertParameters(methodName, 0, "someTextVar", "someDataDivider",
				"coraText");
	}
}
