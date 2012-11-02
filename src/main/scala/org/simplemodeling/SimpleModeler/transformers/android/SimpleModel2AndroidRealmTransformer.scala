package org.simplemodeling.SimpleModeler.transformers.android

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.android._
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
 * @since   Apr. 18, 2011
 *  version Oct. 26, 2011
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2AndroidRealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2JavaRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = AndroidEntityContext
  type TargetRealmTYPE = AndroidRealmEntity

  override val target_context = new AndroidEntityContext(sm.entityContext, sctx)
  override val target_realm = new AndroidRealmEntity(target_context)  
  target_context.setModel(sm)
  target_context.setPlatform(target_realm)

  def toAndroidRealm() = transform

  override protected def make_Builder() = {
    new AndroidBuilder 
  }

  override protected def make_Phases(): List[TransformerPhase] = {
    List(new AndroidBuilder2(), new AndroidResolve(), new AndroidMakeCrud())
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

  class AndroidBuilder extends BuilderBase {
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      val appname = target_context.className(pkg, "Application")
      val app = new AndroidApplicationEntity(target_context)
//      build_package(app, pkg, ppkg, appname)
      build_object_for_package(app, pkg, ppkg, appname)
      //
      val providername = target_context.className(pkg, "Provider")
      val provider = new AndroidContentProviderEntity(target_context)
//      build_package(provider, pkg, ppkg, providername)
      build_object_for_package(app, pkg, ppkg, providername)
      for (m <- module) {
        m.entries += PModuleEntry(providername, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(providername, None, true)
      }
      //
      val contractname = target_context.className(pkg, "Contract")
      val contract = new AndroidContentContractEntity(target_context)
//      build_package(contract, pkg, ppkg, contractname)
      build_object_for_package(contract, pkg, ppkg, contractname)
      for (m <- module) {
        m.entries += PModuleEntry(contractname, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(contractname, None, true)
      }
    }

    override protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      new AndroidEntityEntity(target_context)
    }

    override protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      new AndroidEntityEntity(target_context)
    }

    override protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      new AndroidEntityEntity(target_context)
    }

    override protected def create_Role(entity: SMDomainRole): DomainRoleTYPE = {
      new AndroidEntityEntity(target_context)
    }

    override protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      new AndroidEntityEntity(target_context)
    }

    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new AndroidEntityEntity(target_context)
    }

    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new AndroidEntityPartEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): Option[DomainPowertypeTYPE] = {
      val r = new AndroidPowertypeEntity(target_context)
      r.modelPowertype = entity
      Some(r)
    }

    override protected def create_Value(entity: SMDomainValue): Option[DomainValueTYPE] = {
      Some(new AndroidValueEntity(target_context))
    }

    override protected def create_Document(entity: SMDomainDocument): Option[DomainDocumentTYPE] = {
      Some(new AndroidDocumentEntity(target_context))
    }

    override protected def create_Service(entity: SMDomainService): Option[DomainServiceTYPE] = {
      Some(new AndroidServiceEntity(target_context))
    }

    // platform specific models
    override protected def create_Agent(): Option[PAgentEntity] = {
      Some(new AndroidAgentEntity(target_context))
    }

    override protected def create_Command(): Option[PCommanderEntity] = {
      Some(new AndroidCommanderEntity(target_context))
    }

    override protected def create_Component(): Option[PComponentEntity] = {
      Some(new AndroidComponentEntity(target_context))
    }

    override protected def create_Context(): Option[PContextEntity] = {
      Some(new AndroidContextEntity(target_context))
    }

    override protected def create_Controller(): Option[PControllerEntity] = {
      Some(new AndroidControllerEntity(target_context))
    }

    override protected def create_ErrorModel(): Option[PErrorModelEntity] = {
      Some(new AndroidErrorModelEntity(target_context))
    }

    override protected def create_Facade(): Option[PFacadeEntity] = {
      Some(new AndroidFacadeEntity(target_context))
    }

    override protected def create_Factory(): Option[PFactoryEntity] = {
      Some(new AndroidFactoryEntity(target_context))
    }

    override protected def create_Model(): Option[PModelEntity] = {
      Some(new AndroidModelEntity(target_context))
    }

    override protected def create_Module(): Option[PModuleEntity] = {
      Some(new AndroidModuleEntity(target_context))
    }

    override protected def create_Repository(): Option[PRepositoryEntity] = {
      Some(new AndroidRepositoryEntity(target_context))
    }

    override protected def create_View(): Option[PViewEntity] = {
      Some(new AndroidViewEntity(target_context))
    }
  }

  private def _driver_if_name(pkgname: String) = {
    "I" + target_context.packageClassName(pkgname, "RestDriver")
  }

  private def _g3_driver_name(pkgname: String) = {
    target_context.packageClassName(pkgname, "G3Driver")
  }

  class AndroidBuilder2 extends TransformerPhase {
    override def transform_Entity(entity: PEntityEntity) {
      val pkgname = entity.packageName
      val driverIfName = _driver_if_name(pkgname)
      val g3DriverName = _g3_driver_name(pkgname)
      val restfeedrepository = new AndroidRestFeedRepositoryEntity(target_context)
      restfeedrepository.entityDocumentName = Some(entity.documentName)
      restfeedrepository.driverIfName = Some(driverIfName)
      restfeedrepository.defaultDriverName = Some(g3DriverName)
//      build_entity(restfeedrepository, entity, target_context.className(entity.classNameBase + "RestFeedRepository"))
      build_entity_android(restfeedrepository, entity, target_context.className(entity.classNameBase + "RestFeedRepository"))
      val restfeedadapter = new AndroidRestFeedAdapterEntity(target_context)
//      build_entity(restfeedadapter, entity, target_context.className(entity.classNameBase + "RestFeedAdapter"))
      build_entity_android(restfeedadapter, entity, target_context.className(entity.classNameBase + "RestFeedAdapter"))
      val restview = new AndroidRestViewActivityEntity(target_context)
//      build_entity(restview, entity, target_context.className(
//          entity.classNameBase + "RestViewActivity"))
      build_entity_android(restview, entity, target_context.className(
          entity.classNameBase + "RestViewActivity"))
    }

    override def transform_Package(pkg: PPackageEntity) {
      val pkgname = pkg.modelPackage.get.name
      val driverIfName = _driver_if_name(pkgname)
      val g3DriverName = _g3_driver_name(pkgname)
      val driverIf = new AndroidIRestDriverEntity(target_context)
      build_package_android(driverIf, pkg, driverIfName)
      val g3driver = new AndroidG3RestDriverEntity(target_context)
      g3driver.interfaceName = Some(driverIfName)
      build_package_android(g3driver, pkg, g3DriverName)
    }
  }

  class AndroidResolve extends ResolvePhase {
  }

  class AndroidMakeCrud extends CrudMakePhase {
  }
}
