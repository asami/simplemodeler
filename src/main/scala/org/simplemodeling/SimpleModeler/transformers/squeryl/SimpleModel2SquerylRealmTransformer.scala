package org.simplemodeling.SimpleModeler.transformers.squeryl

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.squeryl._
import org.simplemodeling.SimpleModeler.transformers.SimpleModel2ScalaRealmTransformerBase
import org.goldenport.value._
import org.goldenport.service.GServiceContext
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.goldenport.entities.fs.FileStoreEntity
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.{UJavaString, UPathString}
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap

/*
 * @since   Feb. 22, 2013
 * @version Feb. 23, 2013
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2SquerylRealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2ScalaRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = SquerylEntityContext
  type TargetRealmTYPE = SquerylRealmEntity

  srcMainDir = "/app"

  override val target_context = new SquerylEntityContext(sm.entityContext, sctx)
  override val target_realm = new SquerylRealmEntity(target_context)  
  target_context.setModel(sm)
  target_context.setPlatform(target_realm)

  def toSquerylRealm() = transform

  override protected def make_Package_Name(qname: String): String = {
    "model"
  }

  override protected def make_Qualified_Name(qname: String): String = {
    "model." + UPathString.getLastComponent(qname, ".")
  }

  override protected def make_Builder() = {
    new SquerylBuilder 
  }

  override protected def make_Project() {
  }

  class SquerylBuilder extends ScalaBuilder {
/*
    override protected def transform_Package_Extension(pkg: SMPackage, ppkg: PPackageEntity, module: Option[PModuleEntity], factory: Option[PFactoryEntity]) {
      val appname = target_context.className(pkg, "Model")
      val app = new SquerylModelEntity(target_context)
      build_object_for_package_at_package(app, pkg, appname, "model")
      val drivername = target_context.className(pkg, "SqlDriver")
      val driver = new SquerylSqlDriverEntity(target_context)
      build_object_for_package_at_package(driver, pkg, drivername, "model")
    }
*/

    override protected def create_Actor(entity: SMDomainActor): DomainActorTYPE = {
      new SquerylEntityEntity(target_context)
    }

    override protected def create_Resource(entity: SMDomainResource): DomainResourceTYPE = {
      new SquerylEntityEntity(target_context)
    }

    override protected def create_Event(entity: SMDomainEvent): DomainEventTYPE = {
      new SquerylEntityEntity(target_context)
    }

    override protected def create_Role(entity: SMDomainRole): DomainRoleTYPE = {
      new SquerylEntityEntity(target_context)
    }

    override protected def create_Summary(entity: SMDomainSummary): DomainSummaryTYPE = {
      new SquerylEntityEntity(target_context)
    }

    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new SquerylEntityEntity(target_context)
    }

    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new SquerylEntityPartEntity(target_context)
    }

    override protected def create_Trait(entity: SMDomainTrait): DomainTraitTYPE = {
      new SquerylTraitEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): Option[DomainPowertypeTYPE] = {
      val r = new SquerylPowertypeEntity(target_context)
      r.modelPowertype = entity
      Some(r)
    }

    override protected def create_StateMachine(entity: SMDomainStateMachine): Option[DomainStateMachineTYPE] = {
      val r = new SquerylStateMachineEntity(target_context)
      r.modelStateMachine = entity
      Some(r)
    }
  }
}
