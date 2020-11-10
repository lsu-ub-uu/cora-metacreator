package se.uu.ub.cora.metacreator.recordlink;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.RecordCompleter;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class RecordLinkCompleter extends RecordCompleter implements ExtendedFunctionality {

	public RecordLinkCompleter(String implementingTextType) {
		super(implementingTextType);
	}

	public static RecordLinkCompleter forTextLinkedRecordType(String implementingTextType) {
		return new RecordLinkCompleter(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		addValuesToDataGroup();
	}

}
