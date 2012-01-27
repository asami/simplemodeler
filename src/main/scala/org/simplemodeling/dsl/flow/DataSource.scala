package org.simplemodeling.dsl.flow

import org.simplemodeling.dsl.SEntity

/**
 * @since   Jan. 17, 2011
 * @version Mar. 13. 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DataSet extends SEntity {
}

abstract class DataSource extends SEntity {
  val entities: List[SEntity]
}

abstract class DataSource2[D1 <: SEntity, D2 <: SEntity](implicit val m1: Manifest[D1], implicit val m2: Manifest[D2]) extends DataSource {
  override val entities: List[SEntity] = List(m1.erasure.newInstance().asInstanceOf[SEntity],
                                              m2.erasure.newInstance().asInstanceOf[SEntity])
}

abstract class DataSource3[D1 <: SEntity, D2 <: SEntity, D3 <: SEntity](implicit val m1: Manifest[D1], implicit val m2: Manifest[D2], implicit val m3: Manifest[D3]) extends DataSource {
  override val entities: List[SEntity] = List(m1.erasure.newInstance().asInstanceOf[SEntity],
                                              m2.erasure.newInstance().asInstanceOf[SEntity],
                                              m3.erasure.newInstance().asInstanceOf[SEntity])
}

abstract class DataSource4[D1 <: SEntity, D2 <: SEntity, D3 <: SEntity, D4 <: SEntity](implicit val m1: Manifest[D1], implicit val m2: Manifest[D2], implicit val m3: Manifest[D3], implicit val m4: Manifest[D4]) extends DataSource {
  override val entities: List[SEntity] = List(m1.erasure.newInstance().asInstanceOf[SEntity],
                                              m2.erasure.newInstance().asInstanceOf[SEntity],
                                              m3.erasure.newInstance().asInstanceOf[SEntity],
                                              m4.erasure.newInstance().asInstanceOf[SEntity])
}

abstract class DataSource5[D1 <: SEntity, D2 <: SEntity, D3 <: SEntity, D4 <: SEntity, D5 <: SEntity](implicit val m1: Manifest[D1], implicit val m2: Manifest[D2], implicit val m3: Manifest[D3], implicit val m4: Manifest[D4], implicit val m5: Manifest[D5]) extends DataSource {
  override val entities: List[SEntity] = List(m1.erasure.newInstance().asInstanceOf[SEntity],
                                              m2.erasure.newInstance().asInstanceOf[SEntity],
                                              m3.erasure.newInstance().asInstanceOf[SEntity],
                                              m4.erasure.newInstance().asInstanceOf[SEntity],
                                              m5.erasure.newInstance().asInstanceOf[SEntity])
}

abstract class DataSource6[D1 <: SEntity, D2 <: SEntity, D3 <: SEntity, D4 <: SEntity, D5 <: SEntity, D6 <: SEntity](implicit val m1: Manifest[D1], implicit val m2: Manifest[D2], implicit val m3: Manifest[D3], implicit val m4: Manifest[D4], implicit val m5: Manifest[D5], implicit val m6: Manifest[D6]) extends DataSource {
  override val entities: List[SEntity] = List(m1.erasure.newInstance().asInstanceOf[SEntity],
                                              m2.erasure.newInstance().asInstanceOf[SEntity],
                                              m3.erasure.newInstance().asInstanceOf[SEntity],
                                              m4.erasure.newInstance().asInstanceOf[SEntity],
                                              m5.erasure.newInstance().asInstanceOf[SEntity],
                                              m6.erasure.newInstance().asInstanceOf[SEntity])
}

abstract class DataSource7[D1 <: SEntity, D2 <: SEntity, D3 <: SEntity, D4 <: SEntity, D5 <: SEntity, D6 <: SEntity, D7 <: SEntity](implicit val m1: Manifest[D1], implicit val m2: Manifest[D2], implicit val m3: Manifest[D3], implicit val m4: Manifest[D4], implicit val m5: Manifest[D5], implicit val m6: Manifest[D6], implicit val m7: Manifest[D7]) extends DataSource {
  override val entities: List[SEntity] = List(m1.erasure.newInstance().asInstanceOf[SEntity],
                                              m2.erasure.newInstance().asInstanceOf[SEntity],
                                              m3.erasure.newInstance().asInstanceOf[SEntity],
                                              m4.erasure.newInstance().asInstanceOf[SEntity],
                                              m5.erasure.newInstance().asInstanceOf[SEntity],
                                              m6.erasure.newInstance().asInstanceOf[SEntity],
                                              m7.erasure.newInstance().asInstanceOf[SEntity])
}

object NullDataSet extends DataSet
object NullDataSource extends DataSource {
  val entities = Nil
}
