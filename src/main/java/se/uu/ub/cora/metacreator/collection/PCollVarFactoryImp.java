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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class PCollVarFactoryImp implements PCollVarFactory {

	@Override
	public DataRecordGroup factorPCollVarWithIdDataDividerPresentationOfAndMode(String id,
			String dataDivider, String presentationOf, String mode) {
		DataRecordGroup recordGroup = DataProvider.createRecordGroupUsingNameInData("presentation");
		setBasicRecordGroupInfo(recordGroup, id, dataDivider);
		setPresentationOfLink(recordGroup, presentationOf);
		setMode(recordGroup, mode);
		setEmptyTextLink(recordGroup);
		return recordGroup;
	}

	private void setBasicRecordGroupInfo(DataRecordGroup recordGroup, String id,
			String dataDivider) {
		recordGroup.addAttributeByIdWithValue("type", "pCollVar");
		recordGroup.setId(id);
		recordGroup.setDataDivider(dataDivider);
		recordGroup.setValidationType("presentationCollectionVar");
	}

	private void setPresentationOfLink(DataRecordGroup recordGroup, String presentationOf) {
		DataRecordLink presentationOfLink = DataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("presentationOf",
						"metadataCollectionVariable", presentationOf);
		recordGroup.addChild(presentationOfLink);
	}

	private void setMode(DataRecordGroup recordGroup, String mode) {
		DataAtomic modeAtomic = DataProvider.createAtomicUsingNameInDataAndValue("mode", mode);
		recordGroup.addChild(modeAtomic);
	}

	private void setEmptyTextLink(DataRecordGroup recordGroup) {
		DataRecordLink emptyTextLink = DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"emptyTextId", "text", "initialEmptyValueText");
		recordGroup.addChild(emptyTextLink);
	}
}
