/*
 * Copyright 2017, 2018, 2023 Uppsala University Library
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

import java.util.List;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.RecordReader;

public final class PGroupFactoryImp implements PGroupFactory {

	private static final String CHILD_REFERENCE = "childReference";
	private static final String TYPE_PRESENTATION = "presentation";
	private static final String TYPE_TEXT = "text";
	private static final String ATTRIBUTE_TEXT = "text";
	private static final String ATTRIBUTE_PRESENTATION = "presentation";

	private String mode;
	private String authToken;
	private String id;
	private String dataDivider;
	private String presentationOf;

	private int repeatId = 0;
	private MetadataIdToPresentationId metadataIdToPresentationId;
	private DataRecordGroup recordGroup;
	RecordReader recordReader;
	private DataGroup childReferences;

	private PGroupFactoryImp(String authToken,
			MetadataIdToPresentationId metadataIdToPresentationId) {
		this.authToken = authToken;
		this.metadataIdToPresentationId = metadataIdToPresentationId;
		recordReader = SpiderInstanceProvider.getRecordReader();
	}

	public static PGroupFactory usingAuthTokenAndMetadataToPresentationId(String authToken,
			MetadataIdToPresentationId metadataPresentationId) {
		return new PGroupFactoryImp(authToken, metadataPresentationId);
	}

	@Override
	public DataRecordGroup factorPGroupWithIdDataDividerPresentationOfModeAndChildren(
			String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences) {
		this.mode = mode;
		this.dataDivider = dataDivider;
		this.presentationOf = presentationOf;
		return possiblyCreatePGroup(metadataChildReferences);
	}

	private DataRecordGroup possiblyCreatePGroup(List<DataGroup> metadataChildReferences) {
		recordGroup = DataProvider.createRecordGroupUsingNameInData(TYPE_PRESENTATION);
		id = createPGroupId(presentationOf, mode);
		setBasicRecordGroupInfo();
		setPresentationOfLink();
		setMode();

		childReferences = DataProvider.createGroupUsingNameInData("childReferences");
		recordGroup.addChild(childReferences);
		childReferences = addChildrenToChildReferences(metadataChildReferences);

		throwExceptionIfPGroupHasNoChildren(childReferences);
		return recordGroup;
	}

	private String createPGroupId(String presentationOf, String mode) {
		return metadataIdToPresentationId.createPresentationIdUsingMetadataIdAndMode(presentationOf,
				mode);
	}

	private void setBasicRecordGroupInfo() {
		recordGroup.addAttributeByIdWithValue("type", "pGroup");
		recordGroup.setId(id);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("presentationGroup");
	}

	private void setPresentationOfLink() {
		DataRecordLink presentationOfLink = DataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("presentationOf", "metadataGroup",
						presentationOf);
		recordGroup.addChild(presentationOfLink);
	}

	private void setMode() {
		DataAtomic modeAtomic = DataProvider.createAtomicUsingNameInDataAndValue("mode", mode);
		recordGroup.addChild(modeAtomic);
	}

	private DataGroup addChildrenToChildReferences(List<DataGroup> metadataChildReferences) {
		for (DataGroup metadataChildReference : metadataChildReferences) {
			createTextAndPresentationChildReferences(metadataChildReference);
		}
		return childReferences;
	}

	private void createTextAndPresentationChildReferences(DataGroup metadataChildReference) {
		String linkedRecordId = getMetadataRefId(metadataChildReference);
		possiblyCreateTextChildReference(linkedRecordId);
		possiblyCreatePresentationChildReference(linkedRecordId);
	}

	private void possiblyCreatePresentationChildReference(String linkedRecordId) {
		try {
			DataGroup presentationReferenceGroup = createPresentationChildreferenceGroup(
					linkedRecordId);
			childReferences.addChild(presentationReferenceGroup);
		} catch (Exception e) {
			// do nothing
		}
	}

	private void possiblyCreateTextChildReference(String linkedRecordId) {
		// text
		try {
			DataGroup textReferenceGroup = createTextChildReferenceGroup(linkedRecordId);
			childReferences.addChild(textReferenceGroup);
		} catch (Exception e) {
			// do nothing
		}
	}

	private DataGroup createPresentationChildreferenceGroup(String linkedRecordId) {
		String presentationId = metadataIdToPresentationId
				.createPresentationIdUsingMetadataIdAndMode(linkedRecordId, mode);
		ensureChildExistsInStorage(TYPE_PRESENTATION, presentationId);
		return createChildReferenceGroup(TYPE_PRESENTATION, presentationId, ATTRIBUTE_PRESENTATION);
	}

	private DataGroup createTextChildReferenceGroup(String linkedRecordId) {
		String textId = linkedRecordId + "Text";
		ensureChildExistsInStorage(TYPE_TEXT, textId);
		return createChildReferenceGroup(TYPE_TEXT, textId, ATTRIBUTE_TEXT);
	}

	private void ensureChildExistsInStorage(String type, String textId) {
		recordReader.readRecord(authToken, type, textId);
	}

	private DataGroup createChildReferenceGroup(String linkType, String textId,
			String attributeType) {
		DataGroup referenceGroup = createChildReference();
		DataGroup refGroupGroup = createRefGroup();
		DataRecordLink recordLink = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId("ref",
				linkType, textId);
		recordLink.addAttributeByIdWithValue("type", attributeType);
		refGroupGroup.addChild(recordLink);
		referenceGroup.addChild(refGroupGroup);
		return referenceGroup;
	}

	private DataGroup createRefGroup() {
		DataGroup refGroupGroup = DataProvider.createGroupUsingNameInData("refGroup");
		refGroupGroup.setRepeatId("0");
		return refGroupGroup;
	}

	private DataGroup createChildReference() {
		DataGroup referenceGroup = DataProvider.createGroupUsingNameInData(CHILD_REFERENCE);
		referenceGroup.setRepeatId(getRepeatId());
		return referenceGroup;
	}

	private String getMetadataRefId(DataGroup metadataChildReference) {
		DataRecordLink metadataChildReferenceId = metadataChildReference
				.getFirstChildOfTypeAndName(DataRecordLink.class, "ref");
		return metadataChildReferenceId.getLinkedRecordId();
	}

	private String getRepeatId() {
		int currentRepeatId = repeatId;
		repeatId++;
		return String.valueOf(currentRepeatId);
	}

	private void throwExceptionIfPGroupHasNoChildren(DataGroup childReferences) {
		if (!childReferences.containsChildWithNameInData(CHILD_REFERENCE)) {
			throw new DataException("No children were possible to add to presentationGroup for id "
					+ id + " and presentationOf " + presentationOf);
		}
	}

	MetadataIdToPresentationId onlyForTestMetadataIdToPresentationId() {
		return metadataIdToPresentationId;
	}
}
