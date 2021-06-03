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

package se.uu.ub.cora.metacreator.dependency;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.metacreator.DataAtomicSpy;
import se.uu.ub.cora.metacreator.DataGroupSpy;
import se.uu.ub.cora.metacreator.DataRecordSpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SpiderRecordReaderSpy implements RecordReader {
	public List<String> readMetadataIds = new ArrayList<>();
	public List<String> readMetadataTypes = new ArrayList<>();
	public boolean userSuppliedId = true;

	@Override
	public DataRecord readRecord(String userId, String type, String id) {
		readMetadataTypes.add(type);
		if ("textSystemOne".equals(type)) {
			switch (id) {
			case "textIdTextsInStorageTextVarText":
			case "textIdTextsInStorageTextVarDefText":
			case "textIdOnlyTextInStorageTextVarText":
			case "textIdNoPVarsInStorageTextVarPVar":
			case "textIdNoPVarsInStorageTextVarOutputPVar":
			case "myRecordType2Text":
			case "myRecordType2DefText":
			case "someExistingTextId":
			case "someExistingDefTextId":
			case "someExistingText":
			case "someExistingDefText":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentationVar".equals(type)) {
			switch (id) {
			case "textIdInputPVarInStorageTextVarPVar":
			case "textIdOutputPVarInStorageTextVarOutputPVar":
			case "identifierValuePVar":
			case "identifierValueOutputPVar":
			case "somePVar":
			case "someOutputPVar":
			case "searchTitlePVar":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentationNumberVar".equals(type)) {
			switch (id) {
			case "numVarInputPNumVarInStoragePNumVar":
			case "numVarOutputPNumVarInStorageOutputPNumVar":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentationGroup".equals(type)) {
			switch (id) {
			case "myRecordType2OutputPGroup":
			case "myRecordType2PGroup":
			case "myRecordType2NewPGroup":
			case "myRecordType2MenuPGroup":
			case "myRecordType2ListPGroup":
			case "myRecordType2AutocompletePGroup":
			case "someExistingPGroup":
			case "someExistingOutputPGroup":
			case "identifierChildPGroup":
			case "identifierChildOutputPGroup":
			case "recordInfoPGroup":
			case "recordInfoNewPGroup":
			case "recordInfoAutogeneratedNewPGroup":
			case "recordInfoOutputPGroup":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("metadataGroup".equals(type)) {
			switch (id) {
			case "myRecordType2Group":
			case "myRecordType2NewGroup":
				return createRecordForMetadataGroupWithId(id);
			case "myRecordType3Group":
			case "myRecordType3NewGroup":
				return createRecordForMetadataGroupWithIdAndOneTextVarAsChild(id);
			case "myRecordType4Group":
			case "myRecordType4NewGroup":
				return createRecordForMetadataGroupWithIdAndOneTextVarAsChild(id);
			case "myRecordTypeGroup":
			case "myRecordTypeNewGroup":

				return checkIfAskedForOnceBefore(id);
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("search".equals(type)) {
			switch (id) {
			case "myRecordTypeSearch":
				throw new RecordNotFoundException("record not found in stub");
			default:
				return null;
			}
		}
		if ("metadataCollectionItem".equals(type)) {

			switch (id) {
			case "alreadyExistItem":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("metadataCollectionVariable".equals(type)) {

			switch (id) {
			case "alreadyExistCollectionVar":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentationCollectionVar".equals(type)) {

			switch (id) {
			case "someExistingPCollVar":
			case "someExistingOutputPCollVar":
			case "identifierTypePCollVar":
			case "identifierTypeOutputPCollVar":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentationRecordLink".equals(type)) {

			switch (id) {
			case "someExistingPLink":
			case "someExistingOutputPLink":
			case "identifierPLink":
			case "identifierOutputPLink":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentationResourceLink".equals(type)) {

			switch (id) {
			case "identifierPResLink":
			case "identifierOutputPResLink":
				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("presentation".equals(type)) {

			switch (id) {
			case "identifierValuePVar":
			case "identifierValueOutputPVar":
			case "somePVar":
			case "someOutputPVar":
			case "recordInfoPGroup":
			case "recordInfoNewPGroup":
			case "recordInfoAutogeneratedNewPGroup":
			case "recordInfoOutputPGroup":
			case "searchTitlePVar":

				return null;
			default:
				throw new RecordNotFoundException("record not found in stub");
			}
		}
		if ("identifierChildHasNoPresentationPEnding".equals(id)
				|| "identifierChildHasNoPresentationOutputPEnding".equals(id)) {
			throw new RecordNotFoundException("record not found in stub");
		}

		return null;
	}

	private DataRecord checkIfAskedForOnceBefore(String id) {
		// Used for a test where a check first is made and then the metadata is
		// created
		// the second time the group is asked for it needs to exist
		if (readMetadataIds.contains(id)) {
			return createRecordForMetadataGroupWithId(id);
		}
		readMetadataIds.add(id);
		throw new RecordNotFoundException("record not found in stub");
	}

	private DataRecord createRecordForMetadataGroupWithId(String id) {
		DataGroup metadataGroup = DataCreator.createMetadataGroupWithId(id);
		String recordInfoRefId = "recordInfoGroup";
		if (id.contains("New")) {
			recordInfoRefId = "recordInfoNewGroup";
			if (!userSuppliedId) {
				recordInfoRefId = "recordInfoAutogeneratedNewGroup";
			}
		}
		addRecordInfoToChildReferences(metadataGroup, recordInfoRefId);

		return new DataRecordSpy(metadataGroup);
	}

	private DataRecord createRecordForMetadataGroupWithIdAndOneTextVarAsChild(String id) {
		DataGroup metadataGroup = DataCreator
				.createMetadataGroupWithIdAndTextVarAsChildReference(id);
		String recordInfoRefId = "recordInfoGroup";
		if (id.contains("New")) {
			recordInfoRefId = "recordInfoNewGroup";
		}
		addRecordInfoToChildReferences(metadataGroup, recordInfoRefId);

		return new DataRecordSpy(metadataGroup);
	}

	private void addRecordInfoToChildReferences(DataGroup metadataGroup, String recordInfoRefId) {
		DataGroup childReferences = metadataGroup.getFirstGroupWithNameInData("childReferences");
		DataGroup childReference = new DataGroupSpy("childReference");
		childReference.setRepeatId("1");
		childReference.addChild(new DataAtomicSpy("repeatMin", "0"));
		childReference.addChild(new DataAtomicSpy("repeatMax", "1"));

		DataCreator.addRecordLinkWithNameInDataAndLinkedRecordTypeAndLinkedRecordId(childReference,
				"ref", "metadata", recordInfoRefId);

		childReferences.addChild(childReference);
	}
}
