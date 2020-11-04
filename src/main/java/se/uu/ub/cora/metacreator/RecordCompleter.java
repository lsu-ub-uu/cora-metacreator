package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;

public abstract class RecordCompleter {
    protected String implementingTextType;
    protected DataGroup dataGroup;

    public RecordCompleter(String implementingTextType) {
        this.implementingTextType = implementingTextType;
    }

    protected void addValuesToDataGroup() {
        addTexts();
    }

    private void addTexts() {
        MetadataCompleterImp completer = new MetadataCompleterImp();
        completer.completeDataGroupWithLinkedTexts(dataGroup, implementingTextType);
    }

    public String getImplementingTextType() {
        return implementingTextType;
    }
}
