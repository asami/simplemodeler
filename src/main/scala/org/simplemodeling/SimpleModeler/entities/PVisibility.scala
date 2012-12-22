package org.simplemodeling.SimpleModeler.entities

/**
 * @since   Dec. 22, 2012
 * @version Dec. 22, 2012
 * @author  ASAMI, Tomoharu
 */
sealed trait PVisibility {
  def isVisible(attr: PAttribute): Boolean
}

case object WholeVisibility extends PVisibility {
  def isVisible(attr: PAttribute) = true
}

case object PlainVisibility extends PVisibility {
  def isVisible(attr: PAttribute) = true
}

case object GridVisibility extends PVisibility {
  def isVisible(attr: PAttribute) = {
    attr.getVisibility match {
      case Some(WholeVisibility) => false
      case Some(PlainVisibility) => true
      case Some(GridVisibility) => true
      case Some(DetailVisibility) => false
      case None => attr.isSingle
    }
  }
}

case object DetailVisibility extends PVisibility {
  def isVisible(attr: PAttribute) = {
    attr.getVisibility match {
      case Some(WholeVisibility) => false
      case Some(PlainVisibility) => false
      case Some(GridVisibility) => false
      case Some(DetailVisibility) => true
      case None => false
    }
  }
}
