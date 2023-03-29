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
package se.uu.ub.cora.metacreator.presentation;

import se.uu.ub.cora.data.DataRecordGroup;

/**
 * PVarFactory is used to create {@link DataRecordGroup}s describing presentations for
 * metadataVariables.
 */
public interface PVarFactory {
	/**
	 * factorPVarUsingPresentationOfDataDividerAndMode creates a {@link DataRecordGroup} describing
	 * a presentation for the metadataVariable with the supplied id (presentationOf)
	 * 
	 * @param presentationOf
	 *            A String with the id of the metadataVariable to create a presentation for
	 * @param dataDivider
	 *            A String with the dataDivider to set in the created {@link DataRecordGroup}
	 * @param mode
	 *            A String with mode for the presentation (input/output)
	 * @return A newly created {@link DataRecordGroup} containing information about a presentation
	 *         for the metadataVariable with the supplied id (presentationOf)
	 */
	DataRecordGroup factorPVarUsingPresentationOfDataDividerAndMode(String presentationOf,
			String dataDivider, String mode);

}