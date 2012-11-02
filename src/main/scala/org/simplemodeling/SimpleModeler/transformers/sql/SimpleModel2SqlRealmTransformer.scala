package org.simplemodeling.SimpleModeler.transformers.sql

import scalaz._, Scalaz._
import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.sql._
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
 * @since   May.  6, 2012
 *  version May. 18, 2012
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2SqlRealmTransformer(sm: SimpleModelEntity, sctx: GServiceContext) extends SimpleModel2ProgramRealmTransformerBase(sm, sctx) {
  type EntityContextTYPE = SqlEntityContext
  type TargetRealmTYPE = SqlRealmEntity

  srcMainDir = "/public" // XXX Play
  override val defaultFileSuffix = "sql"
  override val target_context = new SqlEntityContext(sm.entityContext, sctx)
  override val target_realm = new SqlRealmEntity(target_context)
  target_context.setModel(sm)
  target_context.setPlatform(target_realm)
  useEntityDocument = true
  useValue = false
//  usePowertype = false
  useKindPackage = true

  def toSqlRealm() = transform

  override protected def make_Builder() = {
    new SqlBuilder 
  }

  override protected def make_Phases(): List[TransformerPhase] = {
//    List(new SqlBuilder2(), new SqlResolve(), new SqlMakeCrud())
    List(new SqlResolve(), new SqlMakeCrud())
  }

  override protected def make_Project() {
  }

  class SqlBuilder extends BuilderBase {
    override protected def create_Entity(entity: SMDomainEntity): DomainEntityTYPE = {
      new SqlEntityEntity(target_context)
    }
 
    override protected def create_Entity_Part(entity: SMDomainEntityPart): DomainEntityPartTYPE = {
      new SqlEntityPartEntity(target_context)
    }

    override protected def create_Powertype(entity: SMDomainPowertype): Option[DomainPowertypeTYPE] = {
      new SqlPowertypeEntity(target_context) {
        modelPowertype = entity
      }.some
    }

/*
    override protected def create_Value(entity: SMDomainValue): DomainValueTYPE = {
      new SqlValueEntity(target_context)
    }

    override protected def create_Document(entity: SMDomainDocument): DomainDocumentTYPE = {
      new SqlDocumentEntity(target_context)
    }

    override protected def create_Service(entity: SMDomainService): DomainServiceTYPE = {
      new SqlServiceEntity(target_context)
    }
*/
  }

  class SqlResolve extends ResolvePhase {
  }

  class SqlMakeCrud extends CrudMakePhase {
  }
}
