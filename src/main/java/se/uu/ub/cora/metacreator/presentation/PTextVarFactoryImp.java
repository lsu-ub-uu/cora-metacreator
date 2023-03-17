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
package se.uu.ub.cora.metacreator.presentation;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.metacreator.MetadataIdToPresentationId;

/**
 * PTextVarFactoryImp is used to create {@link DataRecordGroup}s describing presentations for
 * metadataVariables of type TextVariable.
 */

public final class PTextVarFactoryImp implements PVarFactory {
	private MetadataIdToPresentationId metadataIdToPresentationId;

	public PTextVarFactoryImp(MetadataIdToPresentationId metadataIdToPresentationId) {
		this.metadataIdToPresentationId = metadataIdToPresentationId;
	}

	public static PTextVarFactoryImp usingMetadataIdToPresentationId(
			MetadataIdToPresentationId metadataIdToPresentationId) {
		return new PTextVarFactoryImp(metadataIdToPresentationId);
	}

	@Override
	public DataRecordGroup factorPVarUsingPresentationOfDataDividerAndMode(String presentationOf,
			String dataDivider, String mode) {
		DataRecordGroup recordGroup = createRecordGroup();
		String pVarId = createPVarId(presentationOf, mode);
		setBasicRecordGroupInfo(recordGroup, pVarId, dataDivider);
		setPresentationOfLink(recordGroup, presentationOf);
		setMode(recordGroup, mode);
		setInputType(recordGroup);
		return recordGroup;
	}

	private DataRecordGroup createRecordGroup() {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupUsingNameInData("presentation");
		recordGroup.addAttributeByIdWithValue("type", "pVar");
		return recordGroup;
	}

	private String createPVarId(String presentationOf, String mode) {
		return metadataIdToPresentationId.createPresentationIdUsingMetadataIdAndMode(presentationOf,
				mode);
	}

	private void setBasicRecordGroupInfo(DataRecordGroup recordGroup, String pVarId,
			String dataDivider) {
		recordGroup.setId(pVarId);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("presentationVar");
	}

	private void setPresentationOfLink(DataRecordGroup recordGroup, String presentationOf) {
		DataRecordLink presentationOfLink = DataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("presentationOf", "metadata",
						presentationOf);
		recordGroup.addChild(presentationOfLink);
	}

	private void setMode(DataRecordGroup recordGroup, String mode) {
		DataAtomic modeAtomic = DataProvider.createAtomicUsingNameInDataAndValue("mode", mode);
		recordGroup.addChild(modeAtomic);
	}

	private void setInputType(DataRecordGroup recordGroup) {
		DataAtomic modeAtomic = DataProvider.createAtomicUsingNameInDataAndValue("inputType",
				"input");
		recordGroup.addChild(modeAtomic);
	}

	public MetadataIdToPresentationId onlyForTestGetMetadataIdToPresentationId() {
		return metadataIdToPresentationId;
	}
}
