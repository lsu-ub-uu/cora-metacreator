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

package se.uu.ub.cora.metacreator.textvar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataRecordLinkFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;

@Test
public class PVarConstructorTest {

	private PVarConstructor pVarConstructor;
	private String id;
	private String dataDividerString;

	private DataGroupFactory dataGroupFactory;
	private DataAtomicFactory dataAtomicFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataAtomicFactory = new DataAtomicFactorySpy();
		DataAtomicProvider.setDataAtomicFactory(dataAtomicFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);

		id = "someTextVar";
		dataDividerString = "cora";
		pVarConstructor = PVarConstructor.withTextVarIdAndDataDivider(id, dataDividerString);
	}

	@Test
	public void testCreateInputPVarFromMetadataIdAndDataDivider() {

		assertNotNull(pVarConstructor);
		DataGroup createdPVar = pVarConstructor.createInputPVar();

		assertEquals(createdPVar.getNameInData(), "presentation");

		assertCorrectAttribute(createdPVar);

		assertCorrectRecordInfo(createdPVar, "somePVar");

		assertEquals(createdPVar.getChildren().size(), 4);
		assertCorrectPresentationOf(id, createdPVar);

		assertEquals(createdPVar.getFirstAtomicValueWithNameInData("mode"), "input");
		assertEquals(createdPVar.getFirstAtomicValueWithNameInData("inputType"), "input");

	}

	private void assertCorrectAttribute(DataGroup createdPVar) {
		Map<String, String> attributes = createdPVar.getAttributes();
		assertEquals(attributes.size(), 1);
		assertEquals(attributes.get("type"), "pVar");
	}

	private void assertCorrectRecordInfo(DataGroup createdPVar, String id) {
		DataGroup recordInfo = createdPVar.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);

		DataRecordLink dataDivider = (DataRecordLink) recordInfo
				.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "cora");
	}

	private void assertCorrectPresentationOf(String id, DataGroup createdPVar) {
		DataRecordLink presentationOf = (DataRecordLink) createdPVar
				.getFirstGroupWithNameInData("presentationOf");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"metadataTextVariable");
		assertEquals(presentationOf.getFirstAtomicValueWithNameInData("linkedRecordId"), id);
	}

	@Test
	public void testCreateOutputPVarFromMetadataIdAndDataDivider() {

		assertNotNull(pVarConstructor);
		DataGroup createdPVar = pVarConstructor.createOutputPVar();

		assertEquals(createdPVar.getNameInData(), "presentation");

		assertCorrectAttribute(createdPVar);

		assertCorrectRecordInfo(createdPVar, "someOutputPVar");

		assertEquals(createdPVar.getChildren().size(), 4);
		assertCorrectPresentationOf(id, createdPVar);

		assertEquals(createdPVar.getFirstAtomicValueWithNameInData("mode"), "output");
		assertEquals(createdPVar.getFirstAtomicValueWithNameInData("inputType"), "input");

	}
}
