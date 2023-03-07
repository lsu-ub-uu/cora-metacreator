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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.metacreator.spy.DataRecordOldSpy;
import se.uu.ub.cora.spider.record.RecordUpdater;

public class SpiderRecordUpdaterOldSpy implements RecordUpdater {

	public String userId;
	public String type;
	public String id;
	public DataGroup record;

	@Override
	public DataRecord updateRecord(String userId, String type, String id, DataGroup record) {
		this.userId = userId;
		this.type = type;
		this.id = id;
		this.record = record;
		return new DataRecordOldSpy(record);
	}

}
