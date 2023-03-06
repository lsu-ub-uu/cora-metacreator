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
package se.uu.ub.cora.metacreator.numbervar;

import se.uu.ub.cora.data.DataAtomicProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.metacreator.DataCreatorHelperImp;

public class PNumVarConstructor {

	private String numberVarId;
	private String dataDividerString;

	private PNumVarConstructor(String numberVarId, String dataDividerString) {
		this.numberVarId = numberVarId;
		this.dataDividerString = dataDividerString;
	}

	public static PNumVarConstructor withTextVarIdAndDataDivider(String numberVarId,
			String dataDividerString) {
		return new PNumVarConstructor(numberVarId, dataDividerString);
	}

	public DataGroup createInputPNumVar() {
		String pVarId = constructPNumVarIdWithEnding("PNumVar");
		return createNumVarUsingIdAndMode(pVarId, "input");
	}

	private String constructPNumVarIdWithEnding(String suffix) {
		String prefix = numberVarId.substring(0, numberVarId.indexOf("NumberVar"));
		return prefix + suffix;
	}

	private DataGroup createNumVarUsingIdAndMode(String pVarId, String mode) {
		DataGroup pNumDataGroup = createPNumVarDataGroup();
		createAndAddRecordInfoToPNumGroupUsingId(pNumDataGroup, pVarId);
		createAndAddPresentationOf(pNumDataGroup);
		pNumDataGroup
				.addChild(DataAtomicProvider.getDataAtomicUsingNameInDataAndValue("mode", mode));
		return pNumDataGroup;
	}

	private DataGroup createPNumVarDataGroup() {
		DataGroup pNumDataGroup = DataGroupProvider.getDataGroupUsingNameInData("presentation");
		pNumDataGroup.addAttributeByIdWithValue("type", "pNumVar");
		return pNumDataGroup;
	}

	private void createAndAddRecordInfoToPNumGroupUsingId(DataGroup pNumDataGroup, String pVarId) {
		DataGroup recordInfo = DataCreatorHelperImp.createRecordInfoWithIdAndDataDividerAndValidationType(pVarId,
				dataDividerString, "someValidationTypeId");
		pNumDataGroup.addChild(recordInfo);
	}

	private void createAndAddPresentationOf(DataGroup pNumDataGroup) {
		DataRecordLink presentationOf = DataRecordLinkProvider
				.getDataRecordLinkAsLinkUsingNameInDataTypeAndId("presentationOf",
						"metadataNumberVariable", numberVarId);
		pNumDataGroup.addChild(presentationOf);
	}

	public DataGroup createOutputPNumVar() {
		String pVarId = constructPNumVarIdWithEnding("OutputPNumVar");
		return createNumVarUsingIdAndMode(pVarId, "output");
	}

}
