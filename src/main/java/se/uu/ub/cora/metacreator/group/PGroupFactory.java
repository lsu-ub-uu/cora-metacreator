package se.uu.ub.cora.metacreator.group;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;

public interface PGroupFactory {

	DataRecordGroup factorPGroupWithIdDataDividerPresentationOfModeAndChildren(String dataDivider,
			String presentationOf, String mode, List<DataGroup> metadataChildReferences);

}