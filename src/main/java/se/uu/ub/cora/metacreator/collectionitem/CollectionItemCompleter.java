/*
 * Copyright 2017 Uppsala University Library
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
package se.uu.ub.cora.metacreator.collectionitem;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class CollectionItemCompleter implements ExtendedFunctionality {

	private String implementingTextType;

	public CollectionItemCompleter(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	public static CollectionItemCompleter forTextLinkedRecordType(String implementingTextType) {
		return new CollectionItemCompleter(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {

		MetadataCompleterImp metadataCompleter = new MetadataCompleterImp();
		metadataCompleter.completeDataGroupWithLinkedTexts(dataGroup, implementingTextType);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}
}
