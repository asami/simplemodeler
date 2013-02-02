package org.simplemodeling.SimpleModeler.entities

/**
 * @since   Dec. 22, 2012
 * @version Feb.  3, 2013
 * @author  ASAMI, Tomoharu
 */
sealed trait PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute): Boolean
}

/**
 * Publish whole attributes.
 *
 * Attributes which have the whole visibility will be shown
 * just only in the whole visibility widgets.
 */
case object WholeVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = true
}

/**
 * Publishes for ordinaly use case.
 */
case object PlainVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr) match {
      case Nil => attr.isSingle
      case xs => xs.exists(_ match {
        case WholeVisibility => false
        case PlainVisibility => true
        case GridVisibility => true
        case DetailVisibility => true
        case ApiVisibility => true
      })
    }
  }
}

/**
 * Publishes for grid.
 */
case object GridVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr) match {
      case Nil => attr.isSingle
      case xs => xs.exists(_ match {
        case WholeVisibility => false
        case PlainVisibility => true
        case GridVisibility => true
        case DetailVisibility => false
        case ApiVisibility => false
      })
    }
  }
}

/**
 * Publishes for showing detail information.
 */
case object DetailVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr).exists(_ match {
      case WholeVisibility => false
      case PlainVisibility => true
      case GridVisibility => true
      case DetailVisibility => true
      case ApiVisibility => false
    })
  }
}

/**
 * Publishes for api.
 */
case object ApiVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr) match {
      case Nil => true
      case xs => xs.exists(_ match {
        case WholeVisibility => false
        case PlainVisibility => true
        case GridVisibility => false
        case DetailVisibility => false
        case ApiVisibility => true
      })
    }
  }
}
