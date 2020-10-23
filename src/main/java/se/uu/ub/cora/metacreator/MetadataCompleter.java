package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;

public interface MetadataCompleter {

	void completeDataGroupWithTexts(DataGroup metadataGroup);

	void completeDataGroupWithLinkedTexts(DataGroup metadataGroup, String string);

}
