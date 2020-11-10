package se.uu.ub.cora.metacreator.search;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.metacreator.RecordCreatorHelper;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;

public class SearchCreator implements ExtendedFunctionality {
	private String implementingTextType;

	public SearchCreator(String implementingTextType) {
		this.implementingTextType = implementingTextType;
	}

	public static SearchCreator forImplementingTextType(String implementingTextType) {
		return new SearchCreator(implementingTextType);
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
