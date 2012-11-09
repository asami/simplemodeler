package org.simplemodeling.SimpleModeler.transformers.java

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.java6._
import org.simplemodeling.SimpleModeler.transformers.SimpleModel2JavaRealmTransformerBase
import org.goldenport.value._
import org.goldenport.service.GServiceContext
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.goldenport.entities.fs.FileStoreEntity
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.UJavaString
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap
import org.goldenport.entity.content.ResourceContent

/*
 * @since   Dec. 12, 2011
 *  version Dec. 14, 2011
 *  version Oct. 26, 2012
 * @version Nov. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2Java6RealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2JavaRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = Java6EntityContext
  type TargetRealmTYPE = Java6RealmEntity

  override val target_context = new Java6EntityContext(sm.entityContext, sctx)
  override val target_realm = new Java6RealmEntity(target_context)  
  target_context.setModel(sm)
  target_context.setPlatform(target_realm)

  def toJavaRealm() = transform

  override protected def make_Builder() = {
    new JavaBuilder 
  }

  override protected def make_Phases(): List[TransformerPhase] = {
    List(new JavaResolve(), new JavaMakeCrud())
  }

  override protected def make_Project() {
    target_realm.setContent("/build.sbt", new ResourceContent("/org/simplemodeling/SimpleModeler/entities/java6/build.sbt", target_context))
    target_realm.setContent("/pom.xml", new ResourceContent("/org/simplemodeling/SimpleModeler/entities/java6/pom.xml", target_context))
/*
    make_service_utilities()
    _gaejRealm.traverse(new CrudMaker())
    make_project()
    make_gwt()
    make_atom()
    make_rest()
*/
  }

  class JavaBuilder extends BuilderBase {
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      val appname = target_context.className(pkg, "Application")
      val app = new Java6ApplicationEntity(target_context)
      build_object_for_package(app, pkg, ppkg, appname)
    }

    override protected def create_Trait(entity: SMDomainTrait): DomainTraitTYPE = {
      if (entity != null && entity.isDocument) {
        new Java6DocumentTraitEntity(target_context)
      } else {
        new Java6TraitEntity(target_context)
      }
    }

    override protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      new Java6EntityEntity(target_context)
    }

    override protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      new Java6EntityEntity(target_context)
    }

    override protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      new Java6EntityEntity(target_context)
    }

    override protected def create_Role(entity: SMDomainRole): DomainRoleTYPE = {
      new Java6EntityEntity(target_context)
    }

    override protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      new Java6EntityEntity(target_context)
    }

    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new Java6EntityEntity(target_context)
    }

    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new Java6EntityPartEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): Option[DomainPowertypeTYPE] = {
      val r = new Java6PowertypeEntity(target_context)
      r.modelPowertype = entity
      Some(r)
    }

    override protected def create_Value(entity: SMDomainValue): Option[DomainValueTYPE] = {
      Some(new Java6ValueEntity(target_context))
    }

    override protected def create_Document(entity: SMDomainDocument): Option[DomainDocumentTYPE] = {
      Some(new Java6DocumentEntity(target_context))
    }

    override protected def create_Service(entity: SMDomainService): Option[DomainServiceTYPE] = {
      Some(new Java6ServiceEntity(target_context))
    }

    // platform specific models
    override protected def create_Agent(): Option[PAgentEntity] = {
      None
    }

    override protected def create_Command(): Option[PCommanderEntity] = {
      Some(new Java6CommanderEntity(target_context))
    }

    override protected def create_Component(): Option[PComponentEntity] = {
      None
    }

    override protected def create_Context(): Option[PContextEntity] = {
      Some(new Java6ContextEntity(target_context))
    }

    override protected def create_Controller(): Option[PControllerEntity] = {
      Some(new Java6ControllerEntity(target_context))
    }

    override protected def create_ErrorModel(): Option[PErrorModelEntity] = {
      Some(new Java6ErrorModelEntity(target_context))
    }

    override protected def create_Facade(): Option[PFacadeEntity] = {
      Some(new Java6FacadeEntity(target_context))
    }

    override protected def create_Factory(): Option[PFactoryEntity] = {
      Some(new Java6FactoryEntity(target_context))
    }

    override protected def create_Model(): Option[PModelEntity] = {
      Some(new Java6ModelEntity(target_context))
    }

    override protected def create_Module(): Option[PModuleEntity] = {
      Some(new Java6ModuleEntity(target_context))
    }

    override protected def create_Repository(): Option[PRepositoryEntity] = {
      Some(new Java6RepositoryEntity(target_context))
    }

    override protected def create_EntityService(): Option[PServiceEntity] = {
      Some(new Java6RepositoryServiceEntity(target_context))
    }

    override protected def create_EventService(): Option[PServiceEntity] = {
      Some(new Java6EventServiceEntity(target_context))
    }

    override protected def create_View(): Option[PViewEntity] = {
      None
    }
  }

  class JavaResolve extends ResolvePhase {
  }

  class JavaMakeCrud extends CrudMakePhase {
  }
}
