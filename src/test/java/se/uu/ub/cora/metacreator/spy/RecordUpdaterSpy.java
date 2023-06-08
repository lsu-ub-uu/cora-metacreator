package se.uu.ub.cora.metacreator.spy;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.spider.record.RecordUpdater;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordUpdaterSpy implements RecordUpdater {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordUpdaterSpy() {
		MCR.useMRV(MRV);

		MRV.setDefaultReturnValuesSupplier("updateRecord", DataRecordSpy::new);
	}

	@Override
	public DataRecord updateRecord(String authToken, String type, String id, DataGroup record) {
		return (DataRecord) MCR.addCallAndReturnFromMRV("authToken", authToken, "type", type, "id",
				id, "record", record);
	}

}
