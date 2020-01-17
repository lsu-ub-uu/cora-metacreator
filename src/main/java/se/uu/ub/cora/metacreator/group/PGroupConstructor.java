/*
 * Copyright 2017, 2018 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataCreatorHelper;
import se.uu.ub.cora.metacreator.PresentationChildReference;
import se.uu.ub.cora.metacreator.RecordIdentifier;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.SpiderRecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public final class PGroupConstructor {

	private static final String CHILD_REFERENCE = "childReference";
	private static final String LINKED_RECORD_ID = "linkedRecordId";
	private static final String PRESENTATION = "presentation";

	private String mode;
	private String authToken;
	private String id;
	private String dataDivider;
	private String presentationOf;

	private int repeatId = 0;
	private PChildRefConstructorFactory pChildRefConstructorFactory;

	private PGroupConstructor(String authToken,
			PChildRefConstructorFactory pChildRefConstructorFactory) {
		this.authToken = authToken;
		this.pChildRefConstructorFactory = pChildRefConstructorFactory;
	}

	public static PGroupConstructor usingAuthTokenAndPChildRefConstructorFactory(String authToken,
			PChildRefConstructorFactory constructorFactory) {
		return new PGroupConstructor(authToken, constructorFactory);
	}

	public DataGroup constructPGroupWithIdDataDividerPresentationOfChildrenAndMode(String id,
			String dataDivider, String presentationOf, List<DataElement> metadataChildReferences,
			String mode) {
		this.mode = mode;
		this.id = id;
		this.dataDivider = dataDivider;
		this.presentationOf = presentationOf;
		return possiblyCreatePGroup(metadataChildReferences);

	}

	private DataGroup possiblyCreatePGroup(List<DataElement> metadataChildReferences) {
		DataGroup childReferences = createChildReferencesDataGroup(metadataChildReferences);
		throwExceptionIfPGroupHasNoChildren(childReferences);
		return constructPGroupWithChildReferences(childReferences);
	}

	private DataGroup createChildReferencesDataGroup(List<DataElement> metadataChildReferences) {
		List<PresentationChildReference> childReferenceList = createChildren(
				metadataChildReferences);

		DataGroup childReferences = DataGroupProvider
				.getDataGroupUsingNameInData("childReferences");
		createChildReferences(childReferences, childReferenceList);
		return childReferences;
	}

	private List<PresentationChildReference> createChildren(
			List<DataElement> metadataChildReferences) {
		List<PresentationChildReference> presentationChildren = new ArrayList<>();

		for (DataElement metadataChildReference : metadataChildReferences) {
			try {
				PChildRefConstructor constructor = getConstructorFromMetadataChild(
						(DataGroup) metadataChildReference);

				List<PresentationChildReference> possibleChildren = possiblyAddChildReferenceAndText(
						(DataGroup) metadataChildReference, constructor);
				presentationChildren.addAll(possibleChildren);
			} catch (DataException e) {
				// do nothing
			}
		}

		return presentationChildren;
	}

	private PChildRefConstructor getConstructorFromMetadataChild(DataGroup metadataChildReference) {
		return pChildRefConstructorFactory.factor(metadataChildReference, mode);
	}

	private String getMetadataRefId(DataGroup metadataChildReference) {
		DataGroup metadataRef = metadataChildReference.getFirstGroupWithNameInData("ref");
		return metadataRef.getFirstAtomicValueWithNameInData(LINKED_RECORD_ID);
	}

	private void createChildReferences(DataGroup childReferences,
			List<PresentationChildReference> childReferenceList) {
		for (PresentationChildReference childRef : childReferenceList) {
			try {
				possiblyCreateChildReference(childReferences, childRef);
			} catch (RecordNotFoundException e) {
				// do nothing
			}
		}
	}

	private List<PresentationChildReference> possiblyAddChildReferenceAndText(
			DataGroup metadataChildReference, PChildRefConstructor constructor) {

		List<PresentationChildReference> possibleChildren = new ArrayList<>();

		PresentationChildReference textChildReference = createChildReferenceForText(
				metadataChildReference);
		possibleChildren.add(textChildReference);
		PresentationChildReference childReference = constructor.getChildRef();
		possibleChildren.add(childReference);
		return possibleChildren;
	}

	private void possiblyCreateChildReference(DataGroup childReferences,
			PresentationChildReference childRef) {
		ensurePChildExists(childRef.recordIdentifier);
		DataGroup childReference = createChildReference(childRef);
		childReferences.addChild(childReference);
	}

	private PresentationChildReference createChildReferenceForText(
			DataGroup metadataChildReference) {
		String metadataRefId = getMetadataRefId(metadataChildReference);

		RecordIdentifier presRef = RecordIdentifier.usingTypeAndId("coraText",
				metadataRefId + "Text");
		DataGroup ref = createRef(presRef);
		ref.addAttributeByIdWithValue("type", "text");
		return PresentationChildReference.usingRefGroupAndRecordIdentifier(ref, presRef);
	}

	private DataGroup createChildReference(PresentationChildReference childRef) {
		DataGroup refGroup = createRefGroup();
		refGroup.addChild(childRef.ref);

		DataGroup childReference = DataGroupProvider.getDataGroupUsingNameInData(CHILD_REFERENCE);
		childReference.setRepeatId(getRepeatId());
		childReference.addChild(refGroup);
		return childReference;
	}

	private String getRepeatId() {
		int currentRepeatId = repeatId;
		repeatId++;
		return String.valueOf(currentRepeatId);
	}

	private DataGroup createRefGroup() {
		DataGroup refGroup = DataGroupProvider.getDataGroupUsingNameInData("refGroup");
		refGroup.setRepeatId("0");
		return refGroup;
	}

	private void ensurePChildExists(RecordIdentifier pChild) {
		SpiderRecordReader reader = SpiderInstanceProvider.getSpiderRecordReader();
		reader.readRecord(authToken, pChild.type, pChild.id);
	}

	private DataGroup createRef(RecordIdentifier presRef) {
		DataRecordLink ref = createRefDataGroup();
		ref.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("linkedRecordType",
				presRef.type));
		ref.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue(LINKED_RECORD_ID,
				presRef.id));
		return ref;
	}

	private DataRecordLink createRefDataGroup() {
		DataRecordLink ref = DataRecordLinkProvider.getDataRecordLinkUsingNameInData("ref");
		ref.addAttributeByIdWithValue("type", PRESENTATION);
		return ref;
	}

	private void throwExceptionIfPGroupHasNoChildren(DataGroup childReferences) {
		if (!childReferences.containsChildWithNameInData(CHILD_REFERENCE)) {
			throw new DataException("No children were possible to add to presentationGroup for id"
					+ id + " and presentationOf " + presentationOf);
		}
	}

	private DataGroup constructPGroupWithChildReferences(DataElement childReferences) {
		DataGroup pGroup = constructPGroup();
		addChildReferencesToPGroup(childReferences, pGroup);
		return pGroup;
	}

	private void addChildReferencesToPGroup(DataElement childReferences, DataGroup pGroup) {
		pGroup.addChild(childReferences);
		pGroup.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("mode", mode));
		createAndAddRecordInfoWithIdAndDataDivider(pGroup);
		createAndAddPresentationOf(pGroup);
	}

	private DataGroup constructPGroup() {
		DataGroup pGroup = DataGroupProvider.getDataGroupUsingNameInData(PRESENTATION);
		pGroup.addAttributeByIdWithValue("type", "pGroup");
		return pGroup;
	}

	private void createAndAddRecordInfoWithIdAndDataDivider(DataGroup pGroup) {
		DataGroup recordInfo = DataCreatorHelper.createRecordInfoWithIdAndDataDivider(id,
				dataDivider);
		pGroup.addChild(recordInfo);
	}

	private void createAndAddPresentationOf(DataGroup pGroup) {
		DataRecordLink presentationOfGroup = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("presentationOf", "metadataGroup",
						presentationOf);
		pGroup.addChild(presentationOfGroup);
	}

	public PChildRefConstructorFactory getPChildRefConstructorFactory() {
		return pChildRefConstructorFactory;
	}
}
