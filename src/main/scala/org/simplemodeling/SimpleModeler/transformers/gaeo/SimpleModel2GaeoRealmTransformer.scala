package org.simplemodeling.SimpleModeler.transformers.gaeo

import _root_.java.io.File
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities.gaeo._
import org.simplemodeling.SimpleModeler.entities.gae._
import org.simplemodeling.SimpleModeler.transformers.gae.SimpleModel2GaeRealmTransformer
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

/*
 * @since   Mar. 11, 2009
 * @version Apr. 17, 2011
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2GaeoRealmTransformer(val simpleModel: SimpleModelEntity, val seriviceContext: GServiceContext) {
  private val _context = simpleModel.entityContext
  private val _gaeRealm = new GaeoRealmEntity(_context)
  private val _workdir = _context.createWorkDirectory

  def toGaeoRealm: GaeoRealmEntity = {
    _gaeRealm.open
    val toGae = new SimpleModel2GaeRealmTransformer(simpleModel, seriviceContext)
    val gae = toGae.toGaeRealm
    gae.traverse(new Generator())
    val fs = new FileStoreEntity(_workdir, _context)
    fs.open()
//    fs.traverse(new Dump())
    _gaeRealm.copyIn(fs)
    fs.close()
    // remove _workdir
//    _gaeRealm.traverse(new Dump())
    _gaeRealm ensuring(_.isOpened)
  }

  private def output_dir: File = _workdir

  private def output_models(models: GaeModelsEntity) {
/*
    println("model package = " + models.packageName)
    for (obj <- models.objects) {
      println("package = " + obj.packageName)
      println("name = " + obj.name)
      for (attr <- obj.attributes) {
	println("attr name = " + attr.name)
	println("type = " + attr.typeLiteral)
      }
    }
*/
    val procs = new ArrayBuffer[Process]
    for (obj <- models.entities) {
      val buf = new StringBuffer
      buf.append("gaeogen.py scaffold ")
      buf.append(obj.name)
      buf.append(" index new edit show create update destroy search")
      for (attr <- obj.attributes) {
	buf.append(" \"")
	buf.append(attr.name)
	buf.append(":")
	buf.append(attr.typeLiteral)
	buf.append("\"")
      }
      val line = buf.toString
//      record_trace(line)
      procs += _context.executeCommand("sh -i " + line, null, output_dir)
    }
    for (proc <- procs) {
      val in = proc.getInputStream()
      val message = UIO.stream2String(in)
//      record_trace(message) // XXX
      in.close()
    }
  }

  class Generator extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      val node = aNode.asInstanceOf[GTreeContainerEntityNode]
      node.entity match {
	case Some(models: GaeModelsEntity) => output_models(models)
	case Some(_) => //
	case None => //
      }
    }
  }

  class Dump extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      println("pathname = " + aNode.pathname)
    }
  }
}
