package se.uu.ub.cora.metacreator.recordtype;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.metacreator.group.MetadataIdToPresentationIdSpy;

public class GroupFactoryTest {

	GroupFactory groupFactory;

	private static final String NAME_IN_DATA = "someNameInData";
	private static final String ID = "someId";
	private static final String AUTH_TOKEN = "someAuthToken";
	private static final String DATA_DIVIDER = "someDataDivider";
	private static final String VALIDATION_TYPE = "someValidationType";
	private static final String CHILD_REF_TO_RECORD_INFO = "someChildRefToRecordInfo";

	MetadataIdToPresentationIdSpy metadataIdToPresentationId;
	private DataFactorySpy dataFactory;

	// private SpiderInstanceFactorySpy instanceFactory;
	// List<DataGroup> metadataChildReferences;
	//
	// private PGroupFactory factory;
	// String id = "someTestPGroup";
	// String dataDivider = "someDataDivider";
	// String presentationOf = "someTestGroup";
	// String mode = "input";
	//
	// private RecordReaderSpy recordReaderSpy;

	@BeforeMethod
	public void setUp() {
		groupFactory = GroupFactory.withDataDividerAndValidationType(DATA_DIVIDER, VALIDATION_TYPE);

		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		metadataIdToPresentationId = new MetadataIdToPresentationIdSpy();
		metadataIdToPresentationId.MRV.setDefaultReturnValuesSupplier(
				"createPresentationIdUsingMetadataIdAndMode", () -> "spyCreatedId");

		// instanceFactory = new SpiderInstanceFactorySpy();
		// SpiderInstanceProvider.setSpiderInstanceFactory(instanceFactory);
		// recordReaderSpy = new RecordReaderSpy();
		// instanceFactory.MRV.setDefaultReturnValuesSupplier("factorRecordReader",
		// () -> recordReaderSpy);
		//
		// authToken = "testUser";
		// metadataChildReferences = new ArrayList<>();
		//
		// DataGroupSpy childRefrencesGroup = new DataGroupSpy();
		// childRefrencesGroup.MRV.setDefaultReturnValuesSupplier("containsChildWithNameInData",
		// () -> true);
		// dataFactory.MRV.setSpecificReturnValuesSupplier("factorGroupUsingNameInData",
		// () -> childRefrencesGroup, "childReferences");
		//
		// factory = PGroupFactoryImp.usingAuthTokenAndMetadataToPresentationId(authToken,
		// metadataIdToPresentationId);
	}

	@Test
	public void testCreateGroup() throws Exception {
		DataRecordGroup returnDataRecordGroup = callFactory();

		dataFactory.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, NAME_IN_DATA);
		dataFactory.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, returnDataRecordGroup);
	}

	@Test
	public void testSetRecordInfo() throws Exception {
		callFactory();

		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);
		dataRecordGroup.MCR.assertParameters("setId", 0, ID);
		dataRecordGroup.MCR.assertParameters("setDataDivider", 0, DATA_DIVIDER);
		dataRecordGroup.MCR.assertParameters("setValidationType", 0, VALIDATION_TYPE);
	}

	@Test
	public void testAddChildReferences() throws Exception {
		callFactory();

		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);

		dataFactory.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", 0, "ref",
				"metadata", CHILD_REF_TO_RECORD_INFO);
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "repeatMin",
				"1");
		dataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 1, "repeatMax",
				"1");
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 0, "childReference");
		dataFactory.MCR.assertParameters("factorGroupUsingNameInData", 1, "childReferences");

		var recordInfoLink = dataFactory.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", 0);
		var repeatMin = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
		var repeatMax = dataFactory.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 1);

		DataGroupSpy childReference = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 0);
		DataGroupSpy childReferences = (DataGroupSpy) dataFactory.MCR
				.getReturnValue("factorGroupUsingNameInData", 1);

		childReference.MCR.assertParameters("addChild", 0, recordInfoLink);
		childReference.MCR.assertParameters("addChild", 1, repeatMin);
		childReference.MCR.assertParameters("addChild", 2, repeatMax);
		childReference.MCR.assertParameters("setRepeatId", 0, "0");

		childReferences.MCR.assertParameters("addChild", 0, childReference);

		dataRecordGroup.MCR.assertParameters("addChild", 0, childReferences);
	}

	private DataRecordGroup callFactory() {
		Optional<String> attributeType = Optional.empty();
		return groupFactory.factorDataGroup(ID, NAME_IN_DATA, CHILD_REF_TO_RECORD_INFO,
				attributeType);
	}

	@Test
	public void testAddOptionalAttributeType() throws Exception {
		Optional<String> attributeType = Optional.of("someOptionalType");

		groupFactory.factorDataGroup(ID, NAME_IN_DATA, CHILD_REF_TO_RECORD_INFO, attributeType);

		DataRecordGroupSpy dataRecordGroup = (DataRecordGroupSpy) dataFactory.MCR
				.getReturnValue("factorRecordGroupUsingNameInData", 0);

		dataRecordGroup.MCR.assertParameters("addAttributeByIdWithValue", 0, "type",
				attributeType.get());
	}

}
