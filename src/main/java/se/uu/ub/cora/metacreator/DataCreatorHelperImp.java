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
package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;

@Deprecated
public class DataCreatorHelperImp implements DataCreatorHelper {
	private static final String VALIDATION_TYPE = "validationType";
	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String RECORD_INFO = "recordInfo";

	@Override
	public String extractDataDividerIdFromDataGroup(DataGroup dataRecordGroup) {
		DataGroup recordInfo = dataRecordGroup.getFirstGroupWithNameInData(RECORD_INFO);
		DataRecordLink dataDivider = (DataRecordLink) recordInfo
				.getFirstChildWithNameInData(DATA_DIVIDER);
		return dataDivider.getLinkedRecordId();
	}

	@Override
	public DataGroup createRecordInfoWithIdAndDataDividerAndValidationType(String id,
			String dataDividerLinkedRecordId, String validationTypeId) {
		DataGroup recordInfo = DataProvider.createGroupUsingNameInData(RECORD_INFO);
		recordInfo.addChild(createId(id));
		recordInfo.addChild(createDataDivider(dataDividerLinkedRecordId));
		recordInfo.addChild(createValidationType(validationTypeId));
		return recordInfo;
	}

	private DataAtomic createId(String id) {
		return DataProvider.createAtomicUsingNameInDataAndValue(ID, id);
	}

	private DataRecordLink createDataDivider(String dataDividerLinkedRecordId) {
		return DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(DATA_DIVIDER, "system",
				dataDividerLinkedRecordId);
	}

	private DataRecordLink createValidationType(String validationTypeId) {
		return DataProvider.createRecordLinkUsingNameInDataAndTypeAndId(VALIDATION_TYPE,
				VALIDATION_TYPE, validationTypeId);
	}

	@Override
	public String extractIdFromDataGroup(DataGroup dataRecordGroup) {
		DataGroup recordInfo = dataRecordGroup.getFirstGroupWithNameInData(RECORD_INFO);
		return recordInfo.getFirstAtomicValueWithNameInData(ID);
	}

}
