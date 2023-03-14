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
				"factorPGroupWithIdDataDividerPresentationOfModeAndChildren",
				DataRecordGroupSpy::new);
	}

	@Override
	public DataRecordGroup factorPGroupWithIdDataDividerPresentationOfModeAndChildren(
			String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences) {
		return (DataRecordGroup) MCR.addCallAndReturnFromMRV("dataDivider", dataDivider,
				"presentationOf", presentationOf, "mode", mode, "metadataChildReferences",
				metadataChildReferences);
	}

}
