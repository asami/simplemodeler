package org.simplemodeling.SimpleModeler.transformers.extjs

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.extjs._
import org.simplemodeling.SimpleModeler.transformers.SimpleModel2ProgramRealmTransformerBase
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
 * @since   Mar. 31, 2011
 * @version Apr. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2ExtjsRealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2ProgramRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = ExtjsEntityContext
  type TargetRealmTYPE = ExtjsRealmEntity

  override val defaultFileSuffix = "js"
  override val target_context = new ExtjsEntityContext(sm.entityContext, sctx)
  override val target_realm = new ExtjsRealmEntity(target_context)  
  useEntityDocument = false
  useValue = false
  usePowertype = false

  def toExtjsRealm() = transform

  override protected def make_Builder() = {
    new ExtjsBuilder 
  }

  override protected def make_Phases(): List[TransformerPhase] = {
//    List(new ExtjsBuilder2(), new ExtjsResolve(), new ExtjsMakeCrud())
    List(new ExtjsResolve(), new ExtjsMakeCrud())
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

  class ExtjsBuilder extends BuilderBase {
/*
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      val appname = target_context.className(pkg, "Application")
      val app = new ExtjsApplicationEntity(target_context)
      build_package(app, pkg, ppkg, appname)
      //
      val providername = target_context.className(pkg, "Provider")
      val provider = new ExtjsContentProviderEntity(target_context)
      build_package(provider, pkg, ppkg, providername)
      for (m <- module) {
        m.entries += PModuleEntry(providername, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(providername, None, true)
      }
      //
      val contractname = target_context.className(pkg, "Contract")
      val contract = new ExtjsContentContractEntity(target_context)
      build_package(contract, pkg, ppkg, contractname)
      for (m <- module) {
        m.entries += PModuleEntry(contractname, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(contractname, None, true)
      }
    }
*/
/*
    override protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      new ExtjsEntityEntity(target_context)
    }

    override protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      new ExtjsEntityEntity(target_context)
    }

    override protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      new ExtjsEntityEntity(target_context)
    }

    override protected def create_Role(entity: SMDomainRole): DomainRoleTYPE = {
      new ExtjsEntityEntity(target_context)
    }

    override protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      new ExtjsEntityEntity(target_context)
    }
*/
    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new ExtjsEntityEntity(target_context)
    }

    override protected def make_Entities(entity: SMDomainEntity): List[PObjectEntity] = {
      List(new ExtjsEntityGridEntity(target_context),
          new ExtjsEntityViewFormEntity(target_context),
          new ExtjsEntityEditFormEntity(target_context),
          new ExtjsEntityStoreEntity(target_context),
          new ExtjsEntityControllerEntity(target_context))
    }
 
    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new ExtjsEntityPartEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): DomainPowertypeTYPE = {
      val r = new ExtjsPowertypeEntity(target_context)
      r.modelPowertype = entity
      r
    }

    override protected def create_Value(entity: SMDomainValue): DomainValueTYPE = {
      new ExtjsValueEntity(target_context)
    }

    override protected def create_Document(entity: SMDomainDocument): DomainDocumentTYPE = {
      new ExtjsDocumentEntity(target_context)
    }

    override protected def create_Service(entity: SMDomainService): DomainServiceTYPE = {
      new ExtjsServiceEntity(target_context)
    }

    // platform specific models
    override protected def create_Controller(): Option[PControllerEntity] = {
      Some(new ExtjsMainControllerEntity(target_context))
    }

    override protected def create_View(): Option[PViewEntity] = {
      Some(new ExtjsViewportEntity(target_context))
    }
/*
    Main Controller
    Entity specific controller
    Entity specific view
        - grid
        - form view
        - form edit
    Entity specific store
    Entity model [done]
    Entity list navigation
*/
/*
    override protected def create_Agent(): Option[PAgentEntity] = {
      Some(new ExtjsAgentEntity(target_context))
    }

    override protected def create_Command(): Option[PCommanderEntity] = {
      Some(new ExtjsCommanderEntity(target_context))
    }

    override protected def create_Component(): Option[PComponentEntity] = {
      Some(new ExtjsComponentEntity(target_context))
    }

    override protected def create_Context(): Option[PContextEntity] = {
      Some(new ExtjsContextEntity(target_context))
    }

    override protected def create_Controller(): Option[PControllerEntity] = {
      Some(new ExtjsControllerEntity(target_context))
    }

    override protected def create_ErrorModel(): Option[PErrorModelEntity] = {
      Some(new ExtjsErrorModelEntity(target_context))
    }

    override protected def create_Facade(): Option[PFacadeEntity] = {
      Some(new ExtjsFacadeEntity(target_context))
    }

    override protected def create_Factory(): Option[PFactoryEntity] = {
      Some(new ExtjsFactoryEntity(target_context))
    }

    override protected def create_Model(): Option[PModelEntity] = {
      Some(new ExtjsModelEntity(target_context))
    }

    override protected def create_Module(): Option[PModuleEntity] = {
      Some(new ExtjsModuleEntity(target_context))
    }

    override protected def create_Repository(): Option[PRepositoryEntity] = {
      Some(new ExtjsRepositoryEntity(target_context))
    }

    override protected def create_View(): Option[PViewEntity] = {
      Some(new ExtjsViewEntity(target_context))
    }
*/
  }

  class ExtjsResolve extends ResolvePhase {
  }

  class ExtjsMakeCrud extends CrudMakePhase {
  }
}
