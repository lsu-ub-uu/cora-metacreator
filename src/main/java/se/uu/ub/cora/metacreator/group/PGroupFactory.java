package se.uu.ub.cora.metacreator.group;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;

public interface PGroupFactory {

	/**
	 * Factors a presentation metadata group. The presentation should have a field for each
	 * dataChild and a title. Note that Id is provided in this method and it shoul be use as id.
	 * 
	 * @param id
	 *            String with the desired id for the presentation.
	 * @param dataDivider
	 *            String with the dataDivider.
	 * @param presentationOf
	 *            A String pointing to the group which the presentation presents.
	 * @param mode
	 *            String either input or output.
	 * @param metadataChildReferences
	 *            List with all dataGroups to create field presentation for.
	 * @return A DataRecordGroup representing the presentation created.
	 */
	DataRecordGroup factorPGroupWithIdDataDividerPresentationOfModeAndChildren(String id,
			String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences);

	/**
	 * Factors a presentation metadata group. The presentation should have a field for each
	 * dataChild and a title.
	 * <p>
	 * Note that Id is missing and should be generated.
	 * 
	 * @param dataDivider
	 *            String with the dataDivider.
	 * @param presentationOf
	 *            A String pointing to the group which the presentation presents.
	 * @param mode
	 *            String either input or output.
	 * @param metadataChildReferences
	 *            List with all dataGroups to create field presentation for.
	 * @return A DataRecordGroup representing the presentation created.
	 */
	DataRecordGroup factorPGroupWithDataDividerPresentationOfModeAndChildren(String dataDivider,
			String presentationOf, String mode, List<DataGroup> metadataChildReferences);

}