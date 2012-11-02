package org.simplemodeling.SimpleModeler.service

import scalaz._
import Scalaz._
import com.asamioffice.goldenport.text.UString
import com.asamioffice.goldenport.text.UPathString
import org.goldenport.z._
import org.goldenport.Z._
import org.goldenport._
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entity.content.GContent
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.goldenport.entities.fs.FileStoreEntity
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.record._
import org.smartdox.Text
import org.simplemodeling.SimpleModeler.SimpleModelerConstants._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.generators.uml.ClassDiagramGenerator
import org.simplemodeling.SimpleModeler.importer.ScalaDslImporter
import org.simplemodeling.SimpleModeler.transformers.java.SimpleModel2Java6RealmTransformer

/**
 * @since   Jan. 29, 2012
 *  version Feb. 28, 2012
 *  version Jun. 17, 2012
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class BuildService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  var crossTarget = "target" + "/scala-2.9.1"
  var sourceManaged =  crossTarget + "/" + "src_managed"

  var _target_dir: GTreeContainerEntityNode = null  

  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) {
    val pkgs = aRequest.parameterAsStrings("source.package")
    val pkgname = (pkgs match {
      case Nil => ""
      case _ => pkgs.head
    }) match { // XXX temporary fix for cloud service
      case "" => DEFAULT_PACKAGE_NAME
      case n => n
    }
    val srcdir: Either[String, FileStoreEntity] = _src_dir(aRequest)
    val result: Either[String, Unit] = srcdir.flatMap { src =>
      src.open()
      val dest = new TreeWorkspaceEntity(entityContext)
      dest.open()
      _target_dir = dest.setNode(sourceManaged)
      val home = src.getNode("/src/main/simplemodel")
      home.foreach(_build)
      aResponse.addRealm(dest).right
    }
    result.left.foreach(m => record_error(m))
  }

  private def _src_dir(req: GServiceRequest): Either[String, FileStoreEntity] = {
    req.getEntity match {
      case Some(fs: FileStoreEntity) => fs.right
      case _ => new FileStoreEntity(".", entityContext).right
    }
  } 

  private object NodeZPathClass extends ZPathClass[GTreeContainerEntityNode] {
    def name(node: GTreeContainerEntityNode): String = {
      node.name
    }
  }

  implicit def GtreeContainerEntityNodeShow: Show[GTreeContainerEntityNode] = showA

  private def _build(home: GTreeContainerEntityNode) {
    val tree = entree(home)
    println("tree = " + tree.drawTree)
    val x = _collect_suffixes(tree, List("csv"))
    println("csvs = " + x.toList)
    val transformed = (for ((path, node) <- _collect_suffixes(tree, List("csv")).toList) yield {
      println("path/content = " + path + ", " + node)
      _transform(path, node)
    })
    println("transformed end")
    println("transformed = " + transformed.toList)
    val transformed2 = transformed.flatten
    println("transformed2 = " + transformed2.toList)
    for ((path, content) <- transformed2) {
      println("path/content = " + path + ", " + content)
      _target_dir.setContent(path.qname, content)
    }
  }

  private def _transform(path: ZPath, node: GTreeContainerEntityNode): Stream[(ZPath, GContent)] = {
    node.mount match {
      case Some(csv: CsvEntity) => _transform_csv(path, csv)
      case None => sys.error("not implemented yet")
    }
  }

  private def _transform_csv(path: ZPath, csv: CsvEntity): Stream[(ZPath, GContent)] = {
    val c = new DirectServiceCall("java", List(csv))
    c.setup(call.serviceContext)
    c.setSession(call.session)
    val importer = new ScalaDslImporter(c)
    importer.execute()
    val simpleModel = c.request.getEntity.collectFirst {
      case sm: SimpleModelEntity => sm
    }
    simpleModel match {
      case Some(sm) => _sm2_java6(path, call, sm)
      case None => sys.error("not implemented yet")
    }
  }

  private def _sm2_java6(path: ZPath, call: GServiceCall, sm: SimpleModelEntity): Stream[(ZPath, GContent)] = {
    val sm2java = new SimpleModel2Java6RealmTransformer(sm, call.serviceContext)
    sm2java.srcMainDir = "/main/java"
    sm2java.isMakeProject = false
    val javaRealm = sm2java.transform
    val tree = entree(javaRealm.root)
    println("sm_java6 tree = " + tree.drawTree)
    val r = _collect_path(tree) {
      case (p, t) if t.rootLabel.isContent => t.rootLabel.content
    } (NodeZPathClass)
    println("_sm2_java6 = " + r.toList)
    r
  }

  private def _collect_path[T, U](tree: Tree[T])(
      pf: PartialFunction[(ZPath, Tree[T]), U])
      (implicit op: ZPathClass[T]): Stream[(ZPath, U)] = {
    ZUtils.collectZPathPT(tree)(pf)(op)
  }

  private def _build0(home: GTreeContainerEntityNode, dest: GTreeContainerEntity) {
    val tree = entree(home)
    for ((path, node) <- _collect_suffix(tree, "csv")) {
      _build_csv(path, node, dest)
    }
  }

  private def _collect_suffix(
      tree: Tree[GTreeContainerEntityNode], suffix: String
      ): Stream[(ZPath, GTreeContainerEntityNode)] = {
    _collect_path(tree) {
      case (p, t) if p.suffix == suffix => t.rootLabel
    } (NodeZPathClass)
  }

  private def _collect_suffixes(
      tree: Tree[GTreeContainerEntityNode], suffixes: List[String]
      ): Stream[(ZPath, GTreeContainerEntityNode)] = {
    _collect_path(tree) {
      new PartialFunction[(ZPath, Tree[GTreeContainerEntityNode]), GTreeContainerEntityNode] {
        override def isDefinedAt(pt: (ZPath, Tree[GTreeContainerEntityNode])) = {
          pt._1.suffix.exists(suffixes.contains) ensuring { x => println(pt._1 + " => " + x);true}
        }
        override def apply(pt: (ZPath, Tree[GTreeContainerEntityNode])) = {
          pt._2.rootLabel
        }
      }
//      case (p, t) if p.suffix.exists(suffixes.contains) => t.rootLabel
    } (NodeZPathClass)
  }

  private def _build_csv(path: ZPath, node: GTreeContainerEntityNode, dest: GTreeContainerEntity) {
    val entity = node.entity
    _target_dir.setContent(path.toString, node.content) 
  }

  def entree(node: GTreeContainerEntityNode): Tree[GTreeContainerEntityNode] = {
    Scalaz.node(node, node.children.toStream.map(entree))
  }

  def untree(root: Tree[GTreeContainerEntityNode]): GTreeContainerEntityNode = {
    error("not implemented yet.")
  }
}

object BuildService extends GServiceClass("build") with GoldenportConstants {
  description.title = "Builds SimpleModel project."
  contract = Schema(
      Field("source.package", XString, summary = "Package name"),
      Field(Container_Message, XString))
  def new_Service(aCall: GServiceCall): GService =
    new BuildService(aCall, this)
}
