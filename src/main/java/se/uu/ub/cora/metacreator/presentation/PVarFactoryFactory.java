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

import java.util.Optional;

import se.uu.ub.cora.data.DataRecordGroup;

/**
 * PVarFactoryFactory factors a {@link PVarFactory} based on the type of metadataVariable supplied.
 * 
 */
public interface PVarFactoryFactory {
	/**
	 * factorUsingRecordGroup factors a {@link PVarFactory} based on the type of metadataVariable
	 * supplied. If the entered {@link DataRecordGroup} is not a metadata group or a
	 * metadataVariable an empty Optional returned.
	 * 
	 * @param dataRecordGroup
	 *            A {@link DataRecordGroup} to factor a PVarFactory for
	 * @return An {@link Optional} with the factored {@link PVarFactory}
	 */
	Optional<PVarFactory> factorUsingRecordGroup(DataRecordGroup dataRecordGroup);

}
