package se.uu.ub.cora.metacreator.spy;

import java.util.List;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.Unique;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordTypeHandlerSpy implements RecordTypeHandler {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordTypeHandlerSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getImplementingRecordTypeHandlers",
				() -> List.of(new RecordTypeHandlerSpy()));
		MRV.setDefaultReturnValuesSupplier("getRecordTypeId", String::new);
	}

	@Override
	public boolean representsTheRecordTypeDefiningSearches() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean representsTheRecordTypeDefiningRecordTypes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasLinkedSearch() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSearchId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldAutoGenerateId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCreateDefinitionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefinitionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublicForRead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartReadConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartWriteConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartCreateConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRecordTypeId() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public boolean storeInArchive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Constraint> getReadRecordPartConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getCreateWriteRecordPartConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getUpdateWriteRecordPartConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateDefinitionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Unique> getUniqueDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean useVisibility() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean usePermissionUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNextId() {
		// TODO Auto-generated method stub
		return null;
	}

}
