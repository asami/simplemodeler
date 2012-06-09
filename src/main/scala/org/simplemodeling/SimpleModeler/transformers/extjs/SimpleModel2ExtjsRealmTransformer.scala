package org.simplemodeling.SimpleModeler.transformers.extjs

import scalaz._, Scalaz._
import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.extjs._
import org.simplemodeling.SimpleModeler.entities.extjs.play._
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

/**
 * @since   Mar. 31, 2011
 * @version Jun.  5, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2ExtjsRealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2ProgramRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = ExtjsEntityContext
  type TargetRealmTYPE = ExtjsRealmEntity

  override val srcMainDir = "/public" // XXX Play
  override val defaultFileSuffix = "js"
  override val target_context = new ExtjsEntityContext(sm.entityContext, sctx)
  override val target_realm = new ExtjsRealmEntity(target_context)
  target_context.setModel(sm)
  target_context.setPlatform(target_realm)
  useEntityDocument = false
  useValue = false
  usePowertype = false
  useKindPackage = true

  def toExtjsRealm() = transform

  override protected def make_Builder() = {
    new ExtjsBuilder 
  }

  override protected def make_Phases(): List[TransformerPhase] = {
//    List(new ExtjsBuilder2(), new ExtjsResolve(), new ExtjsMakeCrud())
    List(new ExtjsResolve(), new ExtjsMakeCrud())
  }

  override protected def make_Project() {
    _make_project()
    _make_play()
/*
    make_service_utilities()
    _gaejRealm.traverse(new CrudMaker())
    make_project()
    make_gwt()
    make_atom()
    make_rest()
*/
  }

  private def _make_project() {
    val main = new ExtjsMainEntity(target_context)
    target_realm.setEntity(srcMainDir + "/app.js", main)
  }

  private def _make_play() {
    val readme = new PlayReadmeEntity(target_context)
    target_realm.setEntity("/README.sm", readme)
    val route = new PlayRouteEntity(target_context)
    target_realm.setEntity("/conf/routes.sm", route)
    val mainview = new PlayMainViewEntity(target_context)
    target_realm.setEntity("/app/views/app.scala.html", mainview) // XXX
    val indexcontroller = new PlayMainControllerEntity(target_context)
    target_realm.setEntity("/app/controllers/AppMain.scala", indexcontroller)
    val restcontroller = new PlayRestControllerEntity(target_context)
    target_realm.setEntity("/app/controllers/AppRest.scala", restcontroller)
  }

  class ExtjsBuilder extends BuilderBase {
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      println("SimpleModel2Extjs:" + ppkg.name)
      val evolution = new PlayEvolutionEntity(target_context)
      build_object_for_package_at_pathname(evolution, pkg, ppkg, "/conf/evolutions/default/1.sql.sm")

      // XXX unify application view
      val viewport = new ExtjsViewportEntity(target_context)
      build_object_for_package(viewport, pkg, ppkg, "Viewport")

      val navi = new ExtjsNavigationStoreEntity(target_context)
      build_object_for_package(navi, pkg, ppkg, "NavigationStore")
    }

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
      for (f <- ;) {
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

    override protected def make_Entities(entity: SMDomainEntity, po: PEntityObjectEntity): List[PObjectEntity] = {
//      val pos = Some(po)
      List(
        new ExtjsEntityGridEntity(target_context) {
//          modelObject = entity
//          sourcePlatformObject = pos
          name = target_context.entityGridViewName(entity)
//          setKindedPackageName(entity.packageName)
        },
        new ExtjsEntityViewFormEntity(target_context) {
//          modelObject = entity
//          sourcePlatformObject = pos
          name = target_context.entityFormViewName(entity)
//          setKindedPackageName(entity.packageName)
        },
        new ExtjsEntityEditFormEntity(target_context) {
//          modelObject = entity
//          sourcePlatformObject = pos
          name = target_context.entityEditFormViewName(entity)
//          setKindedPackageName(entity.packageName)
        },
        new ExtjsEntityStoreEntity(target_context) {
//          modelObject = entity
//          sourcePlatformObject = pos
          name = target_context.entityStoreName(entity)
//          setKindedPackageName(entity.packageName)
        },
        new ExtjsEntityControllerEntity(target_context) {
//          modelObject = entity
//          sourcePlatformObject = pos
          name = target_context.entityControllerName(entity)
//          setKindedPackageName(entity.packageName)
        })
    }
 
    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new ExtjsEntityPartEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): Option[DomainPowertypeTYPE] = {
      val r = new ExtjsPowertypeEntity(target_context)
      r.modelPowertype = entity
      r.some
    }

    override protected def create_Value(entity: SMDomainValue): Option[DomainValueTYPE] = {
      new ExtjsValueEntity(target_context).some
    }

    override protected def create_Document(entity: SMDomainDocument): Option[DomainDocumentTYPE] = {
      new ExtjsDocumentEntity(target_context).some
    }

    override protected def create_Service(entity: SMDomainService): Option[DomainServiceTYPE] = {
      new ExtjsServiceEntity(target_context).some
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
