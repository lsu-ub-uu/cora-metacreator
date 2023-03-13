import se.uu.ub.cora.metacreator.collection.CollectionExtendedFunctionalityFactory;
import se.uu.ub.cora.metacreator.group.MetadataExtendedFunctionalityFactory;
import se.uu.ub.cora.metacreator.permission.PermissionExtendedFunctionalityFactory;
import se.uu.ub.cora.metacreator.recordtype.RecordTypeExtendedFunctionalityFactory;
import se.uu.ub.cora.metacreator.search.SearchExtendedFunctionalityFactory;

module se.uu.ub.cora.metacreator {
	requires transitive se.uu.ub.cora.spider;
	requires se.uu.ub.cora.logger;

	provides se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory
			with CollectionExtendedFunctionalityFactory, MetadataExtendedFunctionalityFactory,
			PermissionExtendedFunctionalityFactory, RecordTypeExtendedFunctionalityFactory,
			SearchExtendedFunctionalityFactory;
}