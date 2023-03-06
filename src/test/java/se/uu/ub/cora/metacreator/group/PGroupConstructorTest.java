/*
 * Copyright 2018 Uppsala University Library
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
package se.uu.ub.cora.metacreator.group;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.dependency.SpiderInstanceFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataAtomicFactorySpy;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.spy.DataRecordLinkFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;

public class PGroupConstructorTest {
	private SpiderInstanceFactorySpy instanceFactory;
	private String authToken;
	List<DataChild> metadataChildReferences;
	PChildRefConstructorFactorySpy childRefConstructorFactory;

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
		instanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		authToken = "testUser";
		metadataChildReferences = DataCreatorForPresentationsConstructor.createChildren();
		childRefConstructorFactory = new PChildRefConstructorFactorySpy();
	}

	@Test
	public void testGroupConstructorForInput() {
		PGroupConstructor constructor = PGroupConstructor
				.usingAuthTokenAndPChildRefConstructorFactory(authToken,
						childRefConstructorFactory);
		DataGroup pGroup = constructor
				.constructPGroupWithIdDataDividerPresentationOfChildrenAndMode("someTestPGroup",
						"testSystem", "someTestGroup", metadataChildReferences, "input");
		assertEquals(pGroup.getAttribute("type").getValue(), "pGroup");
		assertEquals(pGroup.getNameInData(), "presentation");
		assertCorrectRecordInfo(pGroup);
		assertCorrectPresentationOf(pGroup);

		assertCorrectFactoredChildReferences(pGroup);
		assertEquals(instanceFactory.spiderRecordReaders.size(), 12);
		assertEquals(instanceFactory.spiderRecordReaders.size(), 12);
		assertEquals(childRefConstructorFactory.mode, "input");
		assertEquals(childRefConstructorFactory.factored.size(), 6);
		DataGroup childReferences = pGroup.getFirstGroupWithNameInData("childReferences");
		assertEveryOtherChildrenIsText(childReferences);
		assertEquals(childReferences.getChildren().size(), 11);

	}

	private void assertCorrectFactoredChildReferences(DataGroup pGroup) {
		assertCorrectFactoredByIndexAndMetadataRefId(0, "identifierTypeCollectionVar");
		assertCorrectFactoredByIndexAndMetadataRefId(1, "identifierValueTextVar");
		assertCorrectFactoredByIndexAndMetadataRefId(2, "identifierResLink");
		assertCorrectFactoredByIndexAndMetadataRefId(3, "identifierLink");
		assertCorrectFactoredByIndexAndMetadataRefId(4, "identifierChildGroup");
		assertCorrectFactoredByIndexAndMetadataRefId(5, "identifierChildHasNoPresentationTextVar");
	}

	private void assertEveryOtherChildrenIsText(DataGroup childReferences) {
		assertChildIsText(childReferences, 0);
		assertChildIsText(childReferences, 2);
		assertChildIsText(childReferences, 4);
		assertChildIsText(childReferences, 6);
		assertChildIsText(childReferences, 8);
		assertChildIsText(childReferences, 10);
	}

	private void assertChildIsText(DataGroup childReferences, int index) {
		DataGroup textChild = (DataGroup) childReferences.getChildren().get(index);
		DataGroup refGroup = textChild.getFirstGroupWithNameInData("refGroup");
		DataRecordLink ref = (DataRecordLink) refGroup.getFirstChildWithNameInData("ref");
		assertEquals(ref.getLinkedRecordType(), "coraText");
	}

	private void assertCorrectFactoredByIndexAndMetadataRefId(int index, String metadataRefId) {
		PChildRefConstructorSpy firstFactored = (PChildRefConstructorSpy) childRefConstructorFactory.factored
				.get(index);
		assertEquals(firstFactored.metadataRefId, metadataRefId);
	}

	private void assertCorrectRecordInfo(DataGroup pGroup) {
		DataGroup recordInfo = pGroup.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "someTestPGroup");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "testSystem");
	}

	private void assertCorrectPresentationOf(DataGroup pGroup) {
		DataRecordLink presentationOf = (DataRecordLink) pGroup
				.getFirstChildWithNameInData("presentationOf");
		assertEquals(presentationOf.getLinkedRecordType(), "metadataGroup");
		assertEquals(presentationOf.getLinkedRecordId(), "someTestGroup");
	}

	@Test
	public void testGroupConstructorForOutput() {
		PGroupConstructor constructor = PGroupConstructor
				.usingAuthTokenAndPChildRefConstructorFactory(authToken,
						childRefConstructorFactory);
		DataGroup pGroup = constructor
				.constructPGroupWithIdDataDividerPresentationOfChildrenAndMode("someTestPGroup",
						"testSystem", "someTestGroup", metadataChildReferences, "output");
		assertEquals(pGroup.getAttribute("type").getValue(), "pGroup");
		assertCorrectRecordInfo(pGroup);
		assertCorrectPresentationOf(pGroup);

		assertCorrectFactoredChildReferences(pGroup);
		assertEquals(instanceFactory.spiderRecordReaders.size(), 12);
		assertEquals(childRefConstructorFactory.mode, "output");
		assertEquals(childRefConstructorFactory.factored.size(), 6);
		DataGroup childReferences = pGroup.getFirstGroupWithNameInData("childReferences");
		assertEveryOtherChildrenIsText(childReferences);
		assertEquals(childReferences.getChildren().size(), 11);

	}

	@Test(expectedExceptions = DataException.class)
	public void testGroupConstructorWithNoIdentifiedChildren() {
		PGroupConstructor constructor = PGroupConstructor
				.usingAuthTokenAndPChildRefConstructorFactory(authToken,
						childRefConstructorFactory);
		List<DataChild> childReferences = new ArrayList<DataChild>();

		DataGroup childRef = DataCreatorForPresentationsConstructor
				.createMetadataChildRefWithIdAndRepeatId("identifierChildGroupWithUnclearEnding",
						"5");
		childReferences.add(childRef);

		constructor.constructPGroupWithIdDataDividerPresentationOfChildrenAndMode("someTestPGroup",
				"testSystem", "someTestGroup", childReferences, "output");
	}
}
