/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.metacreator.spy.DataGroupSpy;

public class DataGroupFactorySpy implements DataGroupFactory {

	public DataGroupSpy dataGroupSpy;

	@Override
	public DataGroup factorUsingNameInData(String nameInData) {
		dataGroupSpy = new DataGroupSpy(nameInData);
		return dataGroupSpy;
	}

	@Override
	public DataGroup factorAsLinkWithNameInDataTypeAndId(String nameInData, String recordType,
			String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

}
