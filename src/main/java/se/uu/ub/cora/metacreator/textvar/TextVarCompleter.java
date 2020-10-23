/*
 * Copyright 2016 Olov McKie
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

package se.uu.ub.cora.metacreator.textvar;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public final class TextVarCompleter implements ExtendedFunctionality {

	private String implementingTextType;

	private TextVarCompleter(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	public static TextVarCompleter forTextLinkedRecordType(String implementingTextType) {
		return new TextVarCompleter(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(String userId, DataGroup dataGroup) {
		MetadataCompleterImp completer = new MetadataCompleterImp();
		completer.completeDataGroupWithLinkedTexts(dataGroup, implementingTextType);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}
}
