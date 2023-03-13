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

import se.uu.ub.cora.spider.record.DataException;

public class MetadataIdToPresentationIdImp implements MetadataIdToPresentationId {
	private String metadataId;
	private String mode;

	@Override
	public String createPresentationIdUsingMetadataIdAndMode(String metadataId, String mode) {
		this.metadataId = metadataId;
		this.mode = mode;

		if (metadataId.endsWith("TextVar")) {
			return constructPresentationId("TextVar", "PVar");
		}
		if (metadataId.endsWith("NumberVar")) {
			return constructPresentationId("NumberVar", "PNumVar");
		}
		if (metadataId.endsWith("CollectionVar")) {
			return constructPresentationId("CollectionVar", "PCollVar");
		}
		if (metadataId.endsWith("ResLink")) {
			return constructPresentationId("ResLink", "PResLink");
		}
		if (metadataId.endsWith("Link")) {
			return constructPresentationId("Link", "PLink");
		}
		if (metadataId.endsWith("Group")) {
			return constructPresentationId("Group", "PGroup");
		}
		throw new DataException("Not possible to construct presentationId from metadataId");
	}

	private String constructPresentationId(String suffix, String presentationSuffix) {
		String modeString = calculateModeString();

		String metadataIdWithoutSuffix = metadataId.substring(0, metadataId.lastIndexOf(suffix));
		return metadataIdWithoutSuffix + modeString + presentationSuffix;
	}

	private String calculateModeString() {
		if (mode.equals("output")) {
			return "Output";
		}
		return "";
	}
}
