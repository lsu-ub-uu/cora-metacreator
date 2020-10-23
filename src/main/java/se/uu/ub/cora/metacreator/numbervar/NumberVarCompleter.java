package se.uu.ub.cora.metacreator.numbervar;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class NumberVarCompleter implements ExtendedFunctionality {

	private String implementingTextType;

	public static NumberVarCompleter forImplementingTextType(String implementingTextType) {
		return new NumberVarCompleter(implementingTextType);
	}

	private NumberVarCompleter(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		MetadataCompleterImp completer = new MetadataCompleterImp();
		completer.completeDataGroupWithLinkedTexts(dataGroup, implementingTextType);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}

}
