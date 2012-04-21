package org.simplemodeling.SimpleModeler.entities.extjs.play

import java.io.BufferedWriter
import com.asamioffice.goldenport.text.JavaScriptTextMaker
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.extjs._

/**
 * @since   Apr. 20, 2012
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayReadmeEntity(pContext: PEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = ""
  val template = """
* Playをインストールして下さい。

http://www.playframework.org/

* Playプロジェクトの作成

play new helloproject
cd projectprojectt

What is the application name? 
> hello
hello

Which template do you want to use for this new application? 

  1 - Create a simple Scala application
  2 - Create a simple Java application
  3 - Create an empty project

> 1

* Ext-JSの最新版を格納したpublic/extjsというディレクトリを作成してください。

http://www.sencha.com/products/extjs/download/

cd public
unzip xvf ~/Downloads/ext-4.0.7-gpl.zip 
mv ext-4.0.7-gpl extjs

"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
