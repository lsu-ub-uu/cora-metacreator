package se.uu.ub.cora.metacreator.recordtype;

import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class GroupFactorySpy implements GroupFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public GroupFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorMetadataGroup", DataRecordGroupSpy::new);
	}

	@Override
	public DataRecordGroup factorMetadataGroup(String dataDivider, String id, String nameInData,
			String childRefRecordInfoId, boolean excludePGroupCreation) {
		return (DataRecordGroup) MCR.addCallAndReturnFromMRV("dataDivider", dataDivider, "id", id,
				"nameInData", nameInData, "childRefRecordInfoId", childRefRecordInfoId,
				"excludePGroupCreation", excludePGroupCreation);
	}

}
