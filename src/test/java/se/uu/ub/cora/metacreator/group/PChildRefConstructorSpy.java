/*
 * Copyright 2018 Uppsala University Library
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

public class PChildRefConstructorSpy extends String {

	public String metadataRefId;

	public PChildRefConstructorSpy(String metadataRefId, String mode) {
		this.metadataRefId = metadataRefId;
		this.mode = mode;
	}

	@Override
	protected String getMetadataRefIdEnding() {
		if (metadataRefId.endsWith("CollectionVar")) {
			return "CollectionVar";
		}
		if (metadataRefId.endsWith("TextVar")) {
			return "TextVar";
		}
		if (metadataRefId.endsWith("ResLink")) {
			return "ResLink";
		}
		if (metadataRefId.endsWith("Link")) {
			return "Link";
		}
		if (metadataRefId.endsWith("Group")) {
			return "Group";
		}

		return "";
	}

	@Override
	protected String getPresentationIdEnding() {
		return "PEnding";
	}

	@Override
	protected String getPresentationRecordType() {
		// TODO Auto-generated method stub
		return null;
	}

}
