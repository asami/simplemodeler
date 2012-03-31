package org.simplemodeling.SimpleModeler.transformers.play

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.play._
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
 * @since   Mar. 31, 2012
 * @version Mar. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2PlayRealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2JavaRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = PlayEntityContext
  type TargetRealmTYPE = PlayRealmEntity

  override val target_context = new PlayEntityContext(sm.entityContext, sctx)
  override val target_realm = new PlayRealmEntity(target_context)  

  def toPlayRealm() = transform

  override protected def make_Builder() = {
    new PlayBuilder 
  }

//  override protected def make_Phases(): List[TransformerPhase] = {
//    List(new PlayBuilder2(), new PlayResolve(), new PlayMakeCrud())
//  }

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

  class PlayBuilder extends BuilderBase {
/*
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      val appname = target_context.className(pkg, "Application")
      val app = new PlayApplicationEntity(target_context)
      build_package(app, pkg, ppkg, appname)
      //
      val providername = target_context.className(pkg, "Provider")
      val provider = new PlayContentProviderEntity(target_context)
      build_package(provider, pkg, ppkg, providername)
      for (m <- module) {
        m.entries += PModuleEntry(providername, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(providername, None, true)
      }
      //
      val contractname = target_context.className(pkg, "Contract")
      val contract = new PlayContentContractEntity(target_context)
      build_package(contract, pkg, ppkg, contractname)
      for (m <- module) {
        m.entries += PModuleEntry(contractname, None, true)
      }
      for (f <- factory) {
        f.entries += PModuleEntry(contractname, None, true)
      }
    }
*/
    override protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      new PlayEntityEntity(target_context)
    }

    override protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      new PlayEntityEntity(target_context)
    }

    override protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      new PlayEntityEntity(target_context)
    }

    override protected def create_Role(entity: SMDomainRole): DomainRoleTYPE = {
      new PlayEntityEntity(target_context)
    }

    override protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      new PlayEntityEntity(target_context)
    }

    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new PlayEntityEntity(target_context)
    }

    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new PlayEntityPartEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): DomainPowertypeTYPE = {
      val r = new PlayPowertypeEntity(target_context)
      r.modelPowertype = entity
      r
    }

    override protected def create_Value(entity: SMDomainValue): DomainValueTYPE = {
      new PlayValueEntity(target_context)
    }

    override protected def create_Document(entity: SMDomainDocument): DomainDocumentTYPE = {
      new PlayDocumentEntity(target_context)
    }

    override protected def create_Service(entity: SMDomainService): DomainServiceTYPE = {
      new PlayServiceEntity(target_context)
    }
/*
    // platform specific models
    override protected def create_Agent(): Option[PAgentEntity] = {
      Some(new PlayAgentEntity(target_context))
    }

    override protected def create_Command(): Option[PCommanderEntity] = {
      Some(new PlayCommanderEntity(target_context))
    }

    override protected def create_Component(): Option[PComponentEntity] = {
      Some(new PlayComponentEntity(target_context))
    }

    override protected def create_Context(): Option[PContextEntity] = {
      Some(new PlayContextEntity(target_context))
    }

    override protected def create_Controller(): Option[PControllerEntity] = {
      Some(new PlayControllerEntity(target_context))
    }

    override protected def create_ErrorModel(): Option[PErrorModelEntity] = {
      Some(new PlayErrorModelEntity(target_context))
    }

    override protected def create_Facade(): Option[PFacadeEntity] = {
      Some(new PlayFacadeEntity(target_context))
    }

    override protected def create_Factory(): Option[PFactoryEntity] = {
      Some(new PlayFactoryEntity(target_context))
    }

    override protected def create_Model(): Option[PModelEntity] = {
      Some(new PlayModelEntity(target_context))
    }

    override protected def create_Module(): Option[PModuleEntity] = {
      Some(new PlayModuleEntity(target_context))
    }

    override protected def create_Repository(): Option[PRepositoryEntity] = {
      Some(new PlayRepositoryEntity(target_context))
    }

    override protected def create_View(): Option[PViewEntity] = {
      Some(new PlayViewEntity(target_context))
    }
*/
  }

  class PlayResolve extends ResolvePhase {
  }

  class PlayMakeCrud extends CrudMakePhase {
  }
}
