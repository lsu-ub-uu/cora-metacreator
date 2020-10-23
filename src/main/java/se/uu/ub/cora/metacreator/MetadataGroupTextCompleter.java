package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class MetadataGroupTextCompleter implements ExtendedFunctionality {

	private MetadataCompleter metadataCompleter;

	public MetadataGroupTextCompleter(MetadataCompleter metadataCompleter,
			String implementingTextType) {
		this.metadataCompleter = metadataCompleter;
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		metadataCompleter.completeDataGroupWithLinkedTexts(dataGroup, "foo");
	}

	public String getImplementingTextType() {
		// TODO continue here
		return null;
	}

}
