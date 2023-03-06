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

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataCreatorHelperImp;

public final class PVarConstructor {
	private String textVarId;
	private String dataDividerString;

	private PVarConstructor(String textVarId, String dataDividerString) {
		this.textVarId = textVarId;
		this.dataDividerString = dataDividerString;
	}

	public static PVarConstructor withTextVarIdAndDataDivider(String textVarId,
			String dataDividerString) {
		return new PVarConstructor(textVarId, dataDividerString);
	}

	public DataGroup createInputPVar() {
		String pVar = "PVar";
		String pVarId = constructPVarIdWithEnding(pVar);
		String mode = "input";
		return createPVarWithIdAndMode(pVarId, mode);
	}

	private String constructPVarIdWithEnding(String pVar) {
		String prefix = textVarId.substring(0, textVarId.indexOf("TextVar"));
		return prefix + pVar;
	}

	private DataGroup createPVarWithIdAndMode(String pVarId, String mode) {
		DataGroup pVarGroup = DataGroupProvider.getDataGroupUsingNameInData("presentation");

		pVarGroup.addAttributeByIdWithValue("type", "pVar");

		pVarGroup.addChild(createRecordInfoWithIdAndDataDividerRecordId(pVarId));

		pVarGroup.addChild(createPresentationOf());

		pVarGroup.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("mode", mode));
		pVarGroup.addChild(
				DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("inputType", "input"));

		return pVarGroup;
	}

	private DataRecordLink createPresentationOf() {
		return DataRecordLinkProvider.getDataRecordLinkAsLinkUsingNameInDataTypeAndId(
				"presentationOf", "metadataTextVariable", textVarId);
	}

	private DataGroup createRecordInfoWithIdAndDataDividerRecordId(String pVarId) {
		return DataCreatorHelperImp.createRecordInfoWithIdAndDataDividerAndValidationType(pVarId, dataDividerString, "someValidationTypeId");
	}

	public DataGroup createOutputPVar() {
		String pVarId = constructPVarIdWithEnding("OutputPVar");
		String mode = "output";
		return createPVarWithIdAndMode(pVarId, mode);
	}
}
