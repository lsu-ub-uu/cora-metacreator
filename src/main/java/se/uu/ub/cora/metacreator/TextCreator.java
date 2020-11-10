package se.uu.ub.cora.metacreator;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class TextCreator implements ExtendedFunctionality {

	private String implementingTextType;

	public TextCreator(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	public static TextCreator forImplementingTextType(String implementingTextType) {
		return new TextCreator(implementingTextType);
	}

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		RecordCreatorHelper recordCreatorHelper = RecordCreatorHelper
				.withAuthTokenDataGroupAndImplementingTextType(authToken, dataGroup,
						implementingTextType);
		recordCreatorHelper.createTextsIfMissing();
	}

	public String getImplementingTextType() {
		return implementingTextType;
	}
}
