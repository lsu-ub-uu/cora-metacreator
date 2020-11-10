package se.uu.ub.cora.metacreator.collection;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.RecordCompleter;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class CollectionVariableCompleter extends RecordCompleter implements ExtendedFunctionality {

	public CollectionVariableCompleter(String implementingTextType) {
		super(implementingTextType);
	}

	public static CollectionVariableCompleter forTextLinkedRecordType(String textLinkedRecordType) {
		return new CollectionVariableCompleter(textLinkedRecordType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		this.dataGroup = dataGroup;
		addValuesToDataGroup();
	}
}
