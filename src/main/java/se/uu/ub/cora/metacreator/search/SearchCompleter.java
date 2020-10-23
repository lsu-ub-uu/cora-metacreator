package se.uu.ub.cora.metacreator.search;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.MetadataCompleterImp;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class SearchCompleter implements ExtendedFunctionality {

    private String implementingTextType;

    public SearchCompleter(String implementingTextType) {
        this.implementingTextType = implementingTextType;
    }

    public static SearchCompleter forTextLinkedRecordType(String textLinkedRecordType) {
        return new SearchCompleter(textLinkedRecordType);
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
