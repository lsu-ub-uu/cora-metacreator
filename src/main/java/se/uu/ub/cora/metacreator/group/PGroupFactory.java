/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.metacreator.group;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;

public interface PGroupFactory {

	/**
	 * Factors a presentation metadata group. The presentation should have a field for each
	 * dataChild and a title. Note that Id is provided in this method and it shoul be use as id.
	 * 
	 * @param authToken
	 *            TODO
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
	 * 
	 * @return A DataRecordGroup representing the presentation created.
	 */
	DataRecordGroup factorPGroupUsingAuthTokenIdDataDividerPresentationOfModeAndChildReferences(
			String authToken, String id, String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences);

	/**
	 * Factors a presentation metadata group. The presentation should have a field for each
	 * dataChild and a title.
	 * <p>
	 * Note that Id is missing and should be generated.
	 * 
	 * @param authToken
	 *            TODO
	 * @param dataDivider
	 *            String with the dataDivider.
	 * @param presentationOf
	 *            A String pointing to the group which the presentation presents.
	 * @param mode
	 *            String either input or output.
	 * @param metadataChildReferences
	 *            List with all dataGroups to create field presentation for.
	 * 
	 * @return A DataRecordGroup representing the presentation created.
	 */
	DataRecordGroup factorPGroupUsingAuthTokenDataDividerPresentationOfModeAndChildReferences(
			String authToken, String dataDivider, String presentationOf, String mode,
			List<DataGroup> metadataChildReferences);

}