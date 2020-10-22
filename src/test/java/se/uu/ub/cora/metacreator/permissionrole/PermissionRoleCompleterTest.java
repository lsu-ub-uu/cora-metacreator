package se.uu.ub.cora.metacreator.permissionrole;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.metacreator.recordtype.DataGroupFactorySpy;
import se.uu.ub.cora.metacreator.testdata.DataCreator;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class PermissionRoleCompleterTest {

	@BeforeMethod
	public void setUp() {
		DataGroupFactory dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
	}

	@Test
	public void testCallPermissionRoleCompleter() {

		ExtendedFunctionality permissionRoleCompleter = new PermissionRoleCompleter();

	}

	@Test
	public void testWithTextsInData() {
		PermissionRoleCompleter permissionRoleCompleter = new PermissionRoleCompleter();

		String textIdNameInData = "textId";
		String textIdLinkedRecordId = "somePermissionRoleText";
		String defTextIdNameInData = "defTextId";
		String defTextIdLinkedRecordId = "somePermissionRoleDefText";

		DataGroup permissionRoleGroup = DataCreator.createPermissionRoleGroupWithTextIdAndDefTextId(
				textIdLinkedRecordId, defTextIdLinkedRecordId);

		int numberOfChildrenBeforeCall = permissionRoleGroup.getChildren().size();

		permissionRoleCompleter.useExtendedFunctionality("authToken", permissionRoleGroup);

		assertEquals(permissionRoleGroup.getChildren().size(), numberOfChildrenBeforeCall);

		assertPermissionRoleGroupContainsLinkedRecordIdWithNameInData(permissionRoleGroup,
				textIdLinkedRecordId, textIdNameInData);

		assertPermissionRoleGroupContainsLinkedRecordIdWithNameInData(permissionRoleGroup,
				defTextIdLinkedRecordId, defTextIdNameInData);

	}

	@Test
	public void testWithOutTextsInData() {

		PermissionRoleCompleter permissionRoleCompleter = new PermissionRoleCompleter();
		DataGroup permissionRoleGroupWithoutTexts = DataCreator
				.createPermissionRoleGroupWithoutTexts();
		permissionRoleCompleter.useExtendedFunctionality("authToken",
				permissionRoleGroupWithoutTexts);
		assertEquals(permissionRoleGroupWithoutTexts.getChildren().size(), 2);
		DataElement textIdChild = permissionRoleGroupWithoutTexts
				.getFirstChildWithNameInData("textId");
		assertNotNull(textIdChild);
		assertNotNull(textIdChild.getAttribute("linkedRecordType"));
		assertNotNull(permissionRoleGroupWithoutTexts.getFirstChildWithNameInData("defTextId"));

	}

	// TODO: check what to do if one of the fields exists?

	// {
	// "name": "textId",
	// "children": [
	// {
	// "name": "linkedRecordType",
	// "value": "coraText"
	// },
	// {
	// "name": "linkedRecordId",
	// "value": "bennisPermissionRoleText"
	// }
	// ]
	// },
	// {
	// "name": "defTextId",
	// "children": [
	// {
	// "name": "linkedRecordType",
	// "value": "coraText"
	// },
	// {
	// "name": "linkedRecordId",
	// "value": "bennisPermissionRoleDefText"
	// }
	// ]
	// }

	private void assertPermissionRoleGroupContainsLinkedRecordIdWithNameInData(
			DataGroup permissionRoleGroup, String linkedRecordId, String nameInData) {
		DataGroup textIdGroupAfterCall = (DataGroup) permissionRoleGroup
				.getFirstChildWithNameInData(nameInData);

		assertEquals(textIdGroupAfterCall.getFirstAtomicValueWithNameInData("linkedRecordId"),
				linkedRecordId);
	}

}
