/*
 * Copyright 2020 Uppsala University Library
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
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.recordtype.RecordTypeHandler;

public class RecordTypeHandlerSpy implements RecordTypeHandler {

	public List<RecordTypeHandler> recordTypeHandlers = new ArrayList<>();
	public String recordTypeId;

	@Override
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildOfBinary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean representsTheRecordTypeDefiningSearches() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean representsTheRecordTypeDefiningRecordTypes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasLinkedSearch() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSearchId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldAutoGenerateId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNewMetadataId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMetadataId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup getMetadataGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCombinedIdsUsingRecordId(String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublicForRead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartReadConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartWriteConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartCreateConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Constraint> getRecordPartReadConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getRecordPartWriteConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getRecordPartCreateWriteConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RecordTypeHandler> getImplementingRecordTypeHandlers() {
		return recordTypeHandlers;
	}

	@Override
	public List<String> getListOfImplementingRecordTypeIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRecordTypeId() {
		return recordTypeId;
	}

	@Override
	public boolean storeInArchive() {
		// TODO Auto-generated method stub
		return false;
	}

}
