package org.simplemodeling.dsl.flow

import org.simplemodeling.dsl._

/**
 * @since   Jan.  4, 2011
 * @version Mar. 26, 2011
 * @author  ASAMI, Tomoharu
 */

abstract class Operator(val ports: List[Port[_]]) extends SObject(null, null) {
  def entities: List[SEntity] = {
    for (m <- (input_Manifests ::: output_Manifests)) yield {
      m.erasure.newInstance.asInstanceOf[SEntity]
    }
  }

  def inputs: List[SEntity] = {
    for (m <- input_Manifests) yield {
      m.erasure.newInstance.asInstanceOf[SEntity]
    }
  }

  def outputs: List[SEntity] = {
    for (m <- output_Manifests) yield {
      m.erasure.newInstance.asInstanceOf[SEntity]
    }
  }

  protected def input_Manifests: List[Manifest[_]]
  protected def output_Manifests: List[Manifest[_]]
}

abstract class Operator11[FIN1 <: SEntity, FOUT1 <: SEntity](implicit val min1: Manifest[FIN1], implicit val mout1: Manifest[FOUT1]) extends Operator(Nil) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1)
  }
}

abstract class Operator12[FIN1 <: SEntity, FOUT1 <: SEntity, FOUT2 <: SEntity](cout2: Port[FOUT2])(implicit val min1: Manifest[FIN1], implicit val mout1: Manifest[FOUT1], implicit val mout2: Manifest[FOUT2]) extends Operator(List(cout2)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1, mout2)
  }
}

abstract class Operator13[FIN1 <: SEntity, FOUT1 <: SEntity, FOUT2 <: SEntity, FOUT3 <: SEntity](cout2: Port[FOUT2], cout3: Port[FOUT3])(implicit val min1: Manifest[FIN1], implicit val mout1: Manifest[FOUT1], implicit val mout2: Manifest[FOUT2], implicit val mout3: Manifest[FOUT3]) extends Operator(List(cout2, cout3)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1, mout2, mout3)
  }
}

abstract class Operator21[FIN1 <: SEntity, FIN2 <: SEntity, FOUT1 <: SEntity](cin2: Port[FIN2])(implicit val min1: Manifest[FIN1], implicit val min2: Manifest[FIN2], implicit val mout1: Manifest[FOUT1]) extends Operator(List(cin2)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1, min2)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1)
  }
}

abstract class Operator22[FIN1 <: SEntity, FIN2 <: SEntity, FOUT1 <: SEntity, FOUT2 <: SEntity](cin2: Port[FIN2], cout2: Port[FOUT2])(implicit val min1: Manifest[FIN1], implicit val min2: Manifest[FIN2], implicit val mout1: Manifest[FOUT1], implicit val mout2: Manifest[FOUT2]) extends Operator(List(cin2, cout2)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1, min2)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1, mout2)
  }
}

abstract class Operator23[FIN1 <: SEntity, FIN2 <: SEntity, FOUT1 <: SEntity, FOUT2 <: SEntity, FOUT3 <: SEntity](cin2: Port[FIN2], cout2: Port[FOUT2], cout3: Port[FOUT3])(implicit val min1: Manifest[FIN1], implicit val min2: Manifest[FIN2], implicit val mout1: Manifest[FOUT1], implicit val mout2: Manifest[FOUT2], implicit val mout3: Manifest[FOUT3]) extends Operator(List(cin2, cout2, cout3)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1, min2)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1, mout2, mout3)
  }
}

abstract class Operator31[FIN1 <: SEntity, FIN2 <: SEntity, FIN3 <: SEntity, FOUT1 <: SEntity](cin2: Port[FIN2], cin3: Port[FIN3])(implicit val min1: Manifest[FIN1], implicit val min2: Manifest[FIN2], implicit val min3: Manifest[FIN3], implicit val mout1: Manifest[FOUT1]) extends Operator(List(cin2, cin3)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1, min2, min3)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1)
  }
}

abstract class Operator32[FIN1 <: SEntity, FIN2 <: SEntity, FIN3 <: SEntity, FOUT1 <: SEntity, FOUT2 <: SEntity](cin2: Port[FIN2], cin3: Port[FIN3], cout2: Port[FOUT2])(implicit val min1: Manifest[FIN1], implicit val min2: Manifest[FIN2], implicit val min3: Manifest[FIN3], implicit val mout1: Manifest[FOUT1], implicit val mout2: Manifest[FOUT2]) extends Operator(List(cin2, cin3, cout2)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1, min2, min3)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1, mout2)
  }
}

abstract class Operator33[FIN1 <: SEntity, FIN2 <: SEntity, FIN3 <: SEntity, FOUT1 <: SEntity, FOUT2 <: SEntity, FOUT3 <: SEntity](cin2: Port[FIN2], cin3: Port[FIN3], cout2: Port[FOUT2], cout3: Port[FOUT3])(implicit val min1: Manifest[FIN1], implicit val min2: Manifest[FIN2], implicit val min3: Manifest[FIN3], implicit val mout1: Manifest[FOUT1], implicit val mout2: Manifest[FOUT2], implicit val mout3: Manifest[FOUT3]) extends Operator(List(cin2, cin3, cout2, cout3)) {
  protected def input_Manifests: List[Manifest[_]] = {
    List(min1, min2, min3)
  }

  protected def output_Manifests: List[Manifest[_]] = {
    List(mout1, mout2, mout3)
  }
}
