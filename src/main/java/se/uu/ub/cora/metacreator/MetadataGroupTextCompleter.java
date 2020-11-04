package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class MetadataGroupTextCompleter implements ExtendedFunctionality {

	private MetadataCompleter metadataCompleter;
	private String implementingTextType;

	private MetadataGroupTextCompleter(MetadataCompleter metadataCompleter,
			String implementingTextType) {
		this.metadataCompleter = metadataCompleter;
		this.implementingTextType = implementingTextType;
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		metadataCompleter.completeDataGroupWithLinkedTexts(dataGroup, implementingTextType);
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}

	public static MetadataGroupTextCompleter withMetadataCompleterForTextLinkedRecordType(
			MetadataCompleter metadataCompleter, String implementingTextType) {
		return new MetadataGroupTextCompleter(metadataCompleter, implementingTextType);
	}

	public MetadataCompleter getMetadataCompleter() {
		return metadataCompleter;

	}

}
