package se.uu.ub.cora.metacreator.permissionrole;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class PermissionRoleCompleter implements ExtendedFunctionality {

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup dataGroup) {
		// MetadataCompleter completer = new MetadataCompleter();
		if (dataGroup.getFirstChildWithNameInData("textId") == null) {
			dataGroup.addChild(DataGroupProvider.getDataGroupUsingNameInData("textId"));
			dataGroup.addChild(DataGroupProvider.getDataGroupUsingNameInData("defTextId"));
		}

	}

}
