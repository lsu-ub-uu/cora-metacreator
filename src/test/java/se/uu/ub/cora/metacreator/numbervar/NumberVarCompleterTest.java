/*
 * Copyright 2016 Olov McKie
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

package se.uu.ub.cora.metacreator.numbervar;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class NumberVarCompleterTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String userId;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		userId = "testUser";
	}

	@Test
	public void testGetimplementingTextType() {
		NumberVarCompleter numberVarCompleter = NumberVarCompleter
				.forImplementingTextType("coraText");
		assertEquals(numberVarCompleter.getImplementingTextType(), "coraText");

	}

	@Test
	public void testWithNoTexts() {
		NumberVarCompleter numberVarCompleter = NumberVarCompleter
				.forImplementingTextType("coraText");

		DataGroup numberVarGroup = DataCreator.createNumberVarUsingIdNameInDataAndDataDivider(
				"noTextsNumberVar", "metadata", "testSystem");

		numberVarCompleter.useExtendedFunctionality(userId, numberVarGroup);

		DataGroup textIdGroup = numberVarGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"noTextsNumberVarText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");

		DataGroup defTextIdGroup = numberVarGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"noTextsNumberVarDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"coraText");
	}

	@Test
	public void testWithTextsInData() {
		NumberVarCompleter numberVarCompleter = NumberVarCompleter
				.forImplementingTextType("coraText");

		DataGroup numberVarGroup = DataCreator.createNumberVarUsingIdNameInDataAndDataDivider(
				"noTextsNumberVar", "metadata", "testSystem");
		addTextsToGroup(numberVarGroup);

		numberVarCompleter.useExtendedFunctionality(userId, numberVarGroup);

		DataGroup textIdGroup = numberVarGroup.getFirstGroupWithNameInData("textId");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someExistingText");
		assertEquals(textIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"someOtherTextType");

		DataGroup defTextIdGroup = numberVarGroup.getFirstGroupWithNameInData("defTextId");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someExistingDefText");
		assertEquals(defTextIdGroup.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"someOtherTextType");

	}

	private void addTextsToGroup(DataGroup numberVarGroup) {
		DataGroup textIdGroup = createTextGroupUsingNameInDataAndTextId("textId",
				"someExistingText");
		numberVarGroup.addChild(textIdGroup);
		DataGroup defTextIdGroup = createTextGroupUsingNameInDataAndTextId("defTextId",
				"someExistingDefText");
		numberVarGroup.addChild(defTextIdGroup);
	}

	private DataGroup createTextGroupUsingNameInDataAndTextId(String nameInData, String textId) {
		DataGroup textIdGroup = DataGroupProvider.getDataGroupUsingNameInData(nameInData);
		textIdGroup.addChild(DataAtomicProvider
				.getDataAtomicUsingNameInDataAndValue("linkedRecordType", "someOtherTextType"));
		textIdGroup.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("linkedRecordId", textId));
		return textIdGroup;
	}

}
