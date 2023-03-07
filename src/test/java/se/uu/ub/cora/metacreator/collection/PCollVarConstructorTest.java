/*
 * Copyright 2023 Uppsala University Library
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
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataCreatorHelperSpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;

public class PCollVarConstructorTest {
	private DataFactorySpy dataFactory;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private PCollVarConstructor constructor;
	private DataCreatorHelperSpy dataCreatorHelper;

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
		constructor = PCollVarConstructor.usingDataCreatorHelper(dataCreatorHelper);
	}

	@Test
	public void testOnlyForTestGetDataCreatorHelper() throws Exception {
		DataCreatorHelper dataCreatorHelper2 = constructor.onlyForTestGetDataCreatorHelper();
		assertSame(dataCreatorHelper2, dataCreatorHelper);
	}

	@Test
	public void testPCollVarConstructor() {
		DataGroup pCollVar = constructor.constructPCollVarWithIdDataDividerPresentationOfAndMode(
				"somePCollVar", "testSystem", "someCollectionVar", "input");

		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "presentation");
		DataRecordGroupSpy recordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);
		recordGroup.MCR.assertParameters("setId", 0, "somePCollVar");
		recordGroup.MCR.assertParameters("setDataDivider", 0, "testSystem");
		recordGroup.MCR.assertParameters("setValidationType", 0, "presentationCollectionVar");
		recordGroup.MCR.assertParameters("addAttributeByIdWithValue", 0, "type", "pCollVar");

		assertCorrectRecordInfo(pCollVar);
		assertCorrectPresentationOf(pCollVar);

		assertEquals(pCollVar.getNameInData(), "presentation");
		assertEquals(pCollVar.getFirstAtomicValueWithNameInData("mode"), "input");
		DataRecordLink emptyValue = (DataRecordLink) pCollVar
				.getFirstChildWithNameInData("emptyTextId");
		assertEquals(emptyValue.getLinkedRecordType(), "coraText");
		assertEquals(emptyValue.getLinkedRecordId(), "initialEmptyValueText");
		assertEquals(pCollVar.getAttribute("type").getValue(), "pCollVar");

	}

	private void assertCorrectRecordInfo(DataGroup pCollVar) {
		DataGroup recordInfo = pCollVar.getFirstGroupWithNameInData("recordInfo");
		String methodName = "createRecordInfoWithIdAndDataDividerAndValidationType";
		dataCreatorHelper.MCR.assertReturn(methodName, 0, recordInfo);
		dataCreatorHelper.MCR.assertParameters(methodName, 0, "somePCollVar", "testSystem",
				"presentationCollectionVar");
	}

	private void assertCorrectPresentationOf(DataGroup pCollVar) {
		DataRecordLink presentationOf = (DataRecordLink) pCollVar
				.getFirstChildWithNameInData("presentationOf");
		assertEquals(presentationOf.getLinkedRecordType(), "metadataCollectionVariable");
		assertEquals(presentationOf.getLinkedRecordId(), "someCollectionVar");
	}
}
