/*
 * Copyright 2018, 2022 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.metacreator.PVarFactory;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class PVarFromTextVarExtendedFunctionality implements ExtendedFunctionality {
	private String authToken;
	private PVarFactory pVarFactory;

	public static PVarFromTextVarExtendedFunctionality usingPVarFactory(PVarFactory pVarFactory) {
		return new PVarFromTextVarExtendedFunctionality(pVarFactory);
	}

	private PVarFromTextVarExtendedFunctionality(PVarFactory pVarFactory) {
		this.pVarFactory = pVarFactory;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		this.authToken = data.authToken;
		DataGroup dataGroup = data.dataGroup;

		DataRecordGroup recordGroup = DataProvider.createRecordGroupFromDataGroup(dataGroup);

		possiblyCreateInputAndOutputForRecordGroup(recordGroup);
	}

	private void possiblyCreateInputAndOutputForRecordGroup(DataRecordGroup recordGroup) {
		String presentationOf = recordGroup.getId();
		String dataDivider = recordGroup.getDataDivider();
		createAndStorePVarIfNotInStorageSinceBefore(presentationOf, dataDivider, "input");
		createAndStorePVarIfNotInStorageSinceBefore(presentationOf, dataDivider, "output");
	}

	private void createAndStorePVarIfNotInStorageSinceBefore(String presentationOf,
			String dataDivider, String mode) {
		DataRecordGroup recordGroupInput = pVarFactory
				.factorPVarUsingPresentationOfDataDividerAndMode(presentationOf, dataDivider, mode);
		String inputId = recordGroupInput.getId();
		if (pVarDoesNotExistInStorage(inputId)) {
			storeRecord(recordGroupInput);
		}
	}

	private boolean pVarDoesNotExistInStorage(String pVarId) {
		try {
			RecordReader spiderRecordReader = SpiderInstanceProvider.getRecordReader();
			spiderRecordReader.readRecord(authToken, "presentation", pVarId);
		} catch (RecordNotFoundException e) {
			return true;
		}
		return false;
	}

	private void storeRecord(DataRecordGroup recordGroupInput) {
		DataGroup groupInput = DataProvider.createGroupFromRecordGroup(recordGroupInput);
		RecordCreator recordCreator = SpiderInstanceProvider.getRecordCreator();
		recordCreator.createAndStoreRecord(authToken, "presentationVar", groupInput);
	}

	public PVarFactory onlyForTestGetPVarFactory() {
		return pVarFactory;
	}
}
