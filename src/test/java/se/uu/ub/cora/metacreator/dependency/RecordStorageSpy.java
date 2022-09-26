package se.uu.ub.cora.metacreator.dependency;

import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.collected.Link;
import se.uu.ub.cora.data.collected.StorageTerm;
import se.uu.ub.cora.metacreator.DataAtomicSpy;
import se.uu.ub.cora.metacreator.DataGroupSpy;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;

public class RecordStorageSpy implements RecordStorage {

	@Override
	public DataGroup read(String type, String id) {
		if ("countryCollectionItem".equals(id) || "genericCollectionItem".equals(id)) {
			DataGroup dataGroup = new DataGroupSpy("recordType");
			DataGroup parentGroup = new DataGroupSpy("parentId");
			parentGroup.addChild(new DataAtomicSpy("" + "linkedRecordType", "recordType"));
			parentGroup.addChild(new DataAtomicSpy("linkedRecordId", "metadataCollectionItem"));
			dataGroup.addChild(parentGroup);
			return dataGroup;
		}
		return new DataGroupSpy("recordType");
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StorageReadResult readList(List<String> type, DataGroup filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean recordExistsForListOfImplementingRecordTypesAndRecordId(List<String> types,
			String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getTotalNumberOfRecordsForTypes(List<String> types, DataGroup filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void create(String type, String id, DataGroup dataRecord, List<StorageTerm> storageTerms,
			List<Link> links, String dataDivider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(String type, String id, DataGroup dataRecord, List<StorageTerm> storageTerms,
			List<Link> links, String dataDivider) {
		// TODO Auto-generated method stub

	}

}
