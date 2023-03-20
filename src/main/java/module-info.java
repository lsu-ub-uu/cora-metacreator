import se.uu.ub.cora.metacreator.factory.MetadataCBMVExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.MetadataCBRExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.PermissionRoleExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.PermissionRuleExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.RecordTypeCBMVExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.RecordTypeCBRExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.SearchExtFuncFactory;

module se.uu.ub.cora.metacreator {
	requires transitive se.uu.ub.cora.spider;
	requires se.uu.ub.cora.logger;

	provides se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory
			with MetadataCBMVExtFuncFactory, MetadataCBRExtFuncFactory,
			PermissionRoleExtFuncFactory, PermissionRuleExtFuncFactory,
			RecordTypeCBMVExtFuncFactory, RecordTypeCBRExtFuncFactory, SearchExtFuncFactory;
}