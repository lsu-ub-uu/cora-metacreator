package se.uu.ub.cora.metacreator.dependency;

import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.record.IncomingLinksReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SpiderDataRecordIncomingLinksReaderSpy implements IncomingLinksReader {
	public DataRecord record;

	@Override
	public DataList readIncomingLinks(String userId, String type, String id) {
		if (null == record) {
			throw new RecordNotFoundException("Record not found");
		}
		return null;
	}
}
