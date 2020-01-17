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

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.PresentationChildReference;
import se.uu.ub.cora.metacreator.RecordIdentifier;

public abstract class PChildRefConstructor {

	protected DataGroup metadataChildReference;
	protected String mode;

	public final PresentationChildReference getChildRef() {
		String id = getId();
		DataGroup ref = createRefUsingId(id);
		RecordIdentifier recordIdentifier = RecordIdentifier
				.usingTypeAndId(getPresentationRecordType(), id);
		return PresentationChildReference.usingRefGroupAndRecordIdentifier(ref, recordIdentifier);
	}

	private String getId() {
		String metadataRefId = getMetadataRefId(metadataChildReference);
		return constructIdFromMetadataRefId(metadataRefId);
	}

	String getMetadataRefId(DataGroup metadataChildReference) {
		DataGroup metadataRef = metadataChildReference.getFirstGroupWithNameInData("ref");
		return metadataRef.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	protected final String constructIdFromMetadataRefId(String metadataRefId) {
		String id = metadataRefId.substring(0, metadataRefId.indexOf(getMetadataRefIdEnding()));

		id += possibleOutputString();
		id += getPresentationIdEnding();
		return id;
	}

	private DataGroup createRefUsingId(String id) {
		DataRecordLink ref = createRefAsDataGroupWihAttribute();
		addLinkedRecordTypeAndRecordIdToRef(id, ref);
		return ref;
	}

	private DataRecordLink createRefAsDataGroupWihAttribute() {
		DataRecordLink ref = DataRecordLinkProvider.getDataRecordLinkUsingNameInData("ref");
		ref.addAttributeByIdWithValue("type", "presentation");
		return ref;
	}

	private void addLinkedRecordTypeAndRecordIdToRef(String id, DataGroup ref) {
		ref.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("linkedRecordType",
				getPresentationRecordType()));
		ref.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("linkedRecordId", id));
	}

	protected final String possibleOutputString() {
		if ("output".equals(mode)) {
			return "Output";
		}
		return "";
	}

	protected abstract String getMetadataRefIdEnding();

	protected abstract String getPresentationIdEnding();

	protected abstract String getPresentationRecordType();

	public String getMode() {
		// for test
		return mode;
	}

	public DataGroup getMetadataChildReference() {
		// for test
		return metadataChildReference;
	}

}
