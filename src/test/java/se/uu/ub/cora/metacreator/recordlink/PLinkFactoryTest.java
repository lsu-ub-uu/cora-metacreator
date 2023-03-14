/*
 * Copyright 2017 Uppsala University Library
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
package se.uu.ub.cora.metacreator.recordlink;

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.metacreator.PVarFactory;
import se.uu.ub.cora.metacreator.collection.PCollVarFactoryImp;
import se.uu.ub.cora.metacreator.group.MetadataIdToPresentationId;
import se.uu.ub.cora.metacreator.group.MetadataIdToPresentationIdSpy;

public class PLinkFactoryTest {
	private DataFactorySpy dataFactory;
	private PVarFactory factory;
	private MetadataIdToPresentationIdSpy metadataIdToPresentationId;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
		metadataIdToPresentationId = new MetadataIdToPresentationIdSpy();
		metadataIdToPresentationId.MRV.setDefaultReturnValuesSupplier(
				"createPresentationIdUsingMetadataIdAndMode", () -> "spyCreatedId");

		// factory = PLinkFactoryImp.usingMetadataIdToPresentationId(metadataIdToPresentationId);
		factory = PLinkFactoryImp.usingMetadataIdToPresentationId(metadataIdToPresentationId);
	}

	@Test
	public void testOnlyForTestGetMetadataIdToPresentationId() throws Exception {
		MetadataIdToPresentationId metadataIdToPresentationId2 = ((PCollVarFactoryImp) factory)
				.onlyForTestGetMetadataIdToPresentationId();
		assertSame(metadataIdToPresentationId2, metadataIdToPresentationId);
	}

	// private DataGroupFactory dataGroupFactory;
	// private DataAtomicFactory dataAtomicFactory;
	// private DataRecordLinkFactory dataRecordLinkFactory;
	//
	// @BeforeMethod
	// public void setUp() {
	// dataGroupFactory = new DataGroupFactorySpy();
	// DataGroupProvider.setDataGroupFactory(dataGroupFactory);
	// dataAtomicFactory = new DataAtomicFactorySpy();
	// DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
	// dataRecordLinkFactory = new DataRecordLinkFactorySpy();
	// DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
	// }
	//
	// @Test
	// public void testConstructPLink() {
	// PLinkFactoryImp constructor = new PLinkFactoryImp();
	// DataGroup pCollVar = constructor.constructPLinkWithIdDataDividerPresentationOfAndMode(
	// "somePLink", "testSystem", "someLink", "input");
	//
	// assertCorrectRecordInfo(pCollVar);
	// assertCorrectPresentationOf(pCollVar);
	//
	// assertEquals(pCollVar.getNameInData(), "presentation");
	// assertEquals(pCollVar.getFirstAtomicValueWithNameInData("mode"), "input");
	// assertEquals(pCollVar.getAttribute("type").getValue(), "pRecordLink");
	//
	// }
	//
	// private void assertCorrectRecordInfo(DataGroup pCollVar) {
	// DataGroup recordInfo = pCollVar.getFirstGroupWithNameInData("recordInfo");
	// assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "somePLink");
	// DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
	// assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "testSystem");
	// }
	//
	// private void assertCorrectPresentationOf(DataGroup pCollVar) {
	// DataRecordLink presentationOf = (DataRecordLink) pCollVar
	// .getFirstGroupWithNameInData("presentationOf");
	// assertEquals(presentationOf.getLinkedRecordType(), "metadataRecordLink");
	// assertEquals(presentationOf.getLinkedRecordId(), "someLink");
	// }
}
