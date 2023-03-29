package se.uu.ub.cora.metacreator.group;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class PGroupFactorySpy implements PGroupFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public PGroupFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier(
				"factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences",
				DataRecordGroupSpy::new);
		MRV.setDefaultReturnValuesSupplier(
				"factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences",
				DataRecordGroupSpy::new);
	}

	@Override
	public DataRecordGroup factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(
			String authToken, String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences) {
		return (DataRecordGroup) MCR.addCallAndReturnFromMRV("dataDivider", dataDivider,
				"presentationOf", presentationOf, "mode", mode, "metadataChildReferences",
				metadataChildReferences);
	}

	@Override
	public DataRecordGroup factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences(
			String authToken, String id, String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences) {
		return (DataRecordGroup) MCR.addCallAndReturnFromMRV("id", id, "dataDivider", dataDivider,
				"presentationOf", presentationOf, "mode", mode, "metadataChildReferences",
				metadataChildReferences);
	}

}
