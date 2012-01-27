package org.simplemodeling.SimpleModeler.entities

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Aug. 20, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class DefaultPackageEntity(aContext: PEntityContext) extends PObjectEntity(aContext) with PPackageEntity {
  val fileSuffix = "sm"
}
