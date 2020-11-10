/*

 * Copyright 2015, 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.metacreator.dependency;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.search.RecordIndexer;
import se.uu.ub.cora.search.RecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.BasePermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.internal.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.SpiderUploader;
import se.uu.ub.cora.storage.RecordIdGenerator;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StreamStorage;

public class DependencyProviderSpy extends SpiderDependencyProvider {

	public DependencyProviderSpy(Map<String, String> initInfo) {
		super(initInfo);
		// TODO Auto-generated constructor stub
		setRecordStorageProvider(new RecordStorageProviderSpy());
	}

	public RecordStorage recordStorage;
	public SpiderAuthorizator authorizator;
	public BasePermissionRuleCalculator keyCalculator;
	public SpiderUploader uploader;
	public DataValidator dataValidator;
	public DataRecordLinkCollector linkCollector;
	public RecordIdGenerator idGenerator;
	public StreamStorage streamStorage;
	public ExtendedFunctionalityProvider extendedFunctionalityProvider;
	public Authenticator authenticator;

	@Override
	public DataValidator getDataValidator() {
		return dataValidator;
	}

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		return linkCollector;
	}

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		return extendedFunctionalityProvider;
	}

	@Override
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	@Override
	public SpiderAuthorizator getSpiderAuthorizator() {
		// TODO Auto-generated method stub
		return authorizator;
	}

	@Override
	public PermissionRuleCalculator getPermissionRuleCalculator() {
		// TODO Auto-generated method stub
		return keyCalculator;
	}

	@Override
	public RecordSearch getRecordSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroupTermCollector getDataGroupTermCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void tryToInitialize() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readInitInfo() {
		// TODO Auto-generated method stub

	}

}
