import se.uu.ub.cora.metacreator.collection.CollectionExtendedFunctionalityFactory;
import se.uu.ub.cora.metacreator.factory.MetadataCBMVExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.PermissionRoleExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.PermissionRuleExtFuncFactory;
import se.uu.ub.cora.metacreator.factory.SearchExtFuncFactory;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeExtendedFunctionalityFactory;

module se.uu.ub.cora.metacreator {
	requires transitive se.uu.ub.cora.spider;
	requires se.uu.ub.cora.logger;

	provides se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory
			with MetadataCBMVExtFuncFactory, PermissionRoleExtFuncFactory, PermissionRuleExtFuncFactory,
			SearchExtFuncFactory, CollectionExtendedFunctionalityFactory,
			RecordTypeExtendedFunctionalityFactory;
}