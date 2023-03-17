package se.uu.ub.cora.metacreator.text;

import se.uu.ub.cora.data.DataGroup;

@Deprecated
public interface MetadataCompleter {

	void completeDataGroupWithLinkedTexts(DataGroup metadataGroup, String string);

}
