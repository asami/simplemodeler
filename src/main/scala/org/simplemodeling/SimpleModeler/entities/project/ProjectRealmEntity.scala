package org.simplemodeling.SimpleModeler.entities.project

import org.goldenport.entity._
import org.goldenport.entity.datasource.{ GDataSource, ResourceDataSource }
import org.goldenport.entity.content.GContent
import org.goldenport.entity.locator.EntityLocator
import org.goldenport.sdoc.structure._
import org.goldenport.entities.workspace.TreeWorkspaceNode
import org.goldenport.entities.zip.ZipEntity
import org.goldenport.value.GTreeBase
import com.asamioffice.goldenport.text.UPathString

/*
 * @since   Jan. 28, 2009
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
class ProjectRealmEntity(aDataSource: GDataSource, aContext: GEntityContext) extends GTreeContainerEntityBase(aDataSource, aContext) {
  type DataSource_TYPE = GDataSource
  override type TreeNode_TYPE = GTreeContainerEntityNode
  override def is_Text_Output = true

  var projectName: String = ""
  private var _projectHome: GTreeContainerEntityNode = _

  def this(aContext: GEntityContext) = this(null, aContext)

  override protected def open_Entity_Create() {
    set_root(new TreeWorkspaceNode(null, this))
    _projectHome = root
  }

  final def projectHome = {
    require(_projectHome != null)
    _projectHome
  }

  final def buildScaffold() {
    def set_pom {
      val resource = new ResourceDataSource("org/simplemodeling/SimpleModeler/entities/project/pom.xml", entityContext)
      //      println("resource = " + resource.getUrl) 2009-02-07

      // jar:file:/C:/Program%20Files/Java/jre6/lib/rt.jar!/java/lang/Class.class
      // file:/C:/eclipse/dev2009/org.simplemodeling.SimpleModeler/org.simplemodeling.SimpleModeler/target/classes/org/simplemodeling/SimpleModeler/entities/project/pom.xml

      def get_simplemodeler_home = {
        val url = resource.getUrl.get.toString
        if (url.startsWith("file:")) {
          val index = url.indexOf("/org.simplemodeling.SimpleModeler")
          url.substring("file:".length, index + "/org.simplemodeling.SimpleModeler".length)
        } else if (url.startsWith("jar:file:")) {
          val index = url.indexOf("!")
          val filename = url.substring("jar:file:".length, index)
          UPathString.getContainerPathname(UPathString.getContainerPathname(filename))
        } else {
          url // XXX
        }
      }

      val lib = get_simplemodeler_home + "/lib"

      def goldenport_system_path = {
        lib + "/goldenport-0.1.jar"
      }

      def dsl_system_path = {
        lib + "/simplemodel-dsl-0.1.jar"
      }

      var text = resource.loadString
      text = text.replaceAll("""\$\{groupId\}""", projectName)
      text = text.replaceAll("""\$\{artifactId\}""", projectName)
      text = text.replaceAll("""\$\{version\}""", "1.0-SNAPSHOT")
      text = text.replaceAll("""\$\{name\}""", projectName)
      text = text.replaceAll("""\$\{goldenport-systemPath\}""", goldenport_system_path)
      text = text.replaceAll("""\$\{dsl-systemPath\}""", dsl_system_path)
      _projectHome.setString("pom.xml", text)
    }

    set_pom
  }

  final def buildDemo() {
    val rsc = new ResourceDataSource("org/simplemodeling/SimpleModeler/entities/project/prototypes/demo.zip", entityContext)
    val entity = new ZipEntity(rsc, entityContext)
    entity.open()
    copyIn(entity)
    entity.close()
//    print
  }
}

class ProjectRealmEntityClass extends GEntityClass {
  type Instance_TYPE = ProjectRealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "project.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new ProjectRealmEntity(aDataSource, aContext))
}
