/*
 * Copyright 2021, 2022 Uppsala University Library
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
package se.uu.ub.cora.metacreator.text;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

@Deprecated
public class MetadataGroupTextCompleter implements ExtendedFunctionality {

	private MetadataCompleter metadataCompleter;
	private String implementingTextType;

	public static MetadataGroupTextCompleter withMetadataCompleterForTextLinkedRecordType(
			MetadataCompleter metadataCompleter, String implementingTextType) {
		return new MetadataGroupTextCompleter(metadataCompleter, implementingTextType);
	}

	private MetadataGroupTextCompleter(MetadataCompleter metadataCompleter,
			String implementingTextType) {
		this.metadataCompleter = metadataCompleter;
		this.implementingTextType = implementingTextType;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		DataGroup dataGroup = data.dataGroup;
		metadataCompleter.completeDataGroupWithLinkedTexts(dataGroup, implementingTextType);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}

	public MetadataCompleter getMetadataCompleter() {
		return metadataCompleter;

	}

}
