package se.uu.ub.cora.metacreator.group;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.RecordCompleter;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class GroupCompleter extends RecordCompleter implements ExtendedFunctionality {

	public GroupCompleter(String implementingTextType) {
		super(implementingTextType);
	}

	public static GroupCompleter forTextLinkedRecordType(String linkedRecordType) {
		return new GroupCompleter(linkedRecordType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		addValuesToDataGroup();
	}
}
