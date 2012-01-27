package org.simplemodeling.SimpleModeler.service

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec, GivenWhenThen}
import org.scalatest.matchers.ShouldMatchers
import org.simplemodeling.SimpleModeler._

// derived from ProjectRealmGeneratorServiceTest (Jan. 29, 2009)
/**
 * @since   Apr. 17, 2011
 * @version Apr. 17, 2011
 * @author  ASAMI, Tomoharu
 */
@RunWith(classOf[JUnitRunner])
class ProjectRealmGeneratorServiceSpec extends WordSpec with ShouldMatchers with GivenWhenThen {
  val TestReadDir = "/tmp/goldenport.d/read"
  val TestCreateDir = "/tmp/goldenport.d/create/ProjectRealmGeneratorService"

  "ProjectRealmGeneratorService" should {
    "ProjectRealmGeneratorService" in {
      val args = Array[String]("-project", "com.hello", "-output:" + TestCreateDir)
      val simpleModeler = new SimpleModeler(args)
      simpleModeler.executeShellCommand(args)
    }
  }
}
