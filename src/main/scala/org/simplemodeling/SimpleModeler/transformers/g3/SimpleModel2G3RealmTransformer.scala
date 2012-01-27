package org.simplemodeling.SimpleModeler.transformers.g3

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.g3._
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

/*
 * @since   Jul. 11, 2011
 * @version Aug. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2G3RealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2JavaRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = G3EntityContext
  type TargetRealmTYPE = G3RealmEntity

  override val target_context = new G3EntityContext(sm.entityContext, sctx)
  val target_java_context = new G3JavaEntityContext(sm.entityContext, sctx)
  override val target_realm = new G3RealmEntity(target_context)  

  def toG3Realm() = transform

  override protected def make_Builder() = {
    new G3Builder 
  }

  override protected def make_Phases(): List[TransformerPhase] = {
    List(new G3Resolve(), new G3MakeCrud())
  }

  override protected def make_Project() {
/*
    make_service_utilities()
    _gaejRealm.traverse(new CrudMaker())
    make_project()
    make_gwt()
    make_atom()
    make_rest()
*/
  }

  class G3Builder extends BuilderBase {
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      val appname = target_context.className(pkg, "Application")
      val app = new G3ApplicationEntity(target_context)
      build_package(app, pkg, ppkg, appname)

      val scriptname = ppkg.term_en
      val script = new G3ScriptEntity(target_context)
      build_package_script(script, pkg, ppkg, scriptname)
/*
      //
      val providername = target_context.className(pkg, "Provider")
      val provider = new AndroidContentProviderEntity(target_context)
      build_package(provider, pkg, ppkg, providername)
      for (m <- module) {
        m.entries += PModuleEntry(providername, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(providername, None, true)
      }
      //
      val contractname = target_context.className(pkg, "Contract")
      val contract = new AndroidContentContractEntity(target_context)
      build_package(contract, pkg, ppkg, contractname)
      for (m <- module) {
        m.entries += PModuleEntry(contractname, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(contractname, None, true)
      }
*/
    }

    override protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      new G3EntityEntity(target_java_context)
    }

    override protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      new G3EntityEntity(target_java_context)
    }

    override protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      new G3EntityEntity(target_java_context)
    }

    override protected def create_Role(entity: SMDomainRole): DomainRoleTYPE = {
      new G3EntityEntity(target_java_context)
    }

    override protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      new G3EntityEntity(target_java_context)
    }

    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new G3EntityEntity(target_java_context)
    }

    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new G3EntityPartEntity(target_java_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): DomainPowertypeTYPE = {
      new G3PowertypeEntity(target_java_context)
    }

    override protected def create_Value(entity: SMDomainValue): DomainValueTYPE = {
      new G3ValueEntity(target_java_context)
    }

    override protected def create_Document(entity: SMDomainDocument): DomainDocumentTYPE = {
      new G3DocumentEntity(target_java_context)
    }

    override protected def create_Service(entity: SMDomainService): DomainServiceTYPE = {
      new G3ServiceEntity(target_java_context)
    }

    // platform specific models
/*
    override protected def create_Command(): Option[PCommanderEntity] = {
      Some(new G3CommandEntity(target_context))
    }

    override protected def create_Component(): Option[PComponentEntity] = {
      Some(new G3ComponentEntity(target_context))
    }

    override protected def create_Controller(): Option[PControllerEntity] = {
      Some(new G3ControllerEntity(target_context))
    }

    override protected def create_Facade(): Option[PFacadeEntity] = {
      Some(new G3FacadeEntity(target_context))
    }

    override protected def create_Factory(): Option[PFactoryEntity] = {
      Some(new G3FactoryEntity(target_context))
    }

    override protected def create_Model(): Option[PModelEntity] = {
      Some(new G3ModelEntity(target_context))
    }

    override protected def create_Module(): Option[PModuleEntity] = {
      Some(new G3ModuleEntity(target_context))
    }

    override protected def create_Repository(): Option[PRepositoryEntity] = {
      Some(new G3RepositoryEntity(target_context))
    }

    override protected def create_View(): Option[PViewEntity] = {
      Some(new G3ViewEntity(target_context))
    }
*/
  }

  class G3Resolve extends ResolvePhase {
  }

  class G3MakeCrud extends CrudMakePhase {
  }
}
