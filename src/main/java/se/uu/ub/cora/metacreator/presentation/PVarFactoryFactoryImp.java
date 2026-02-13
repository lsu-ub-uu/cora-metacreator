/*
 * Copyright 2023, 2026 Uppsala University Library
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
import se.uu.ub.cora.metacreator.MetadataIdToPresentationIdImp;

public class PVarFactoryFactoryImp implements PVarFactoryFactory {

	@Override
	public Optional<PVarFactory> factorUsingRecordGroup(DataRecordGroup dataRecordGroup) {
		System.err.println("Herre?");
		if (isNotMetadata(dataRecordGroup)) {
			return Optional.empty();
		}
		return createPVarFactoryForType(dataRecordGroup);
	}

	private boolean isNotMetadata(DataRecordGroup dataRecordGroup) {
		String nameInData = dataRecordGroup.getNameInData();
		return !"metadata".equals(nameInData);
	}

	private Optional<PVarFactory> createPVarFactoryForType(DataRecordGroup dataRecordGroup) {
		String attributeValue = getValueForAttributeType(dataRecordGroup);
		MetadataIdToPresentationIdImp mIdToPId = new MetadataIdToPresentationIdImp();
		switch (attributeValue) {
		case "textVariable":
			return Optional.of(PTextVarFactoryImp.usingMetadataIdToPresentationId(mIdToPId));
		case "numberVariable":
			return Optional.of(PNumVarFactoryImp.usingMetadataIdToPresentationId(mIdToPId));
		case "collectionVariable":
			return Optional.of(PCollVarFactoryImp.usingMetadataIdToPresentationId(mIdToPId));
		case "recordLink":
			return Optional.of(PLinkFactoryImp.usingMetadataIdToPresentationId(mIdToPId));
		case "anyTypeRecordLink":
			return Optional.of(PLinkFactoryImp.usingMetadataIdToPresentationId(mIdToPId));
		default:
			return Optional.empty();
		}
	}

	private String getValueForAttributeType(DataRecordGroup dataRecordGroup) {
		Optional<String> type = dataRecordGroup.getAttributeValue("type");
		if (type.isPresent()) {
			return type.get();
		}
		return "unknownType";
	}

}
