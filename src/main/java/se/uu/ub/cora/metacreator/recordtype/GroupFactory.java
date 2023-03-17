package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataRecordGroup;

public interface GroupFactory {

	DataRecordGroup factorMetadataGroup(String dataDivider, String id, String nameInData, String childRefRecordInfoId, boolean excludePGroupCreation);

}