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
        case ApiVisibility => false
        case ApiGridVisibility => false
        case ApiDetailVisibility => false
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
        case ApiGridVisibility => false
        case ApiDetailVisibility => false
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
      case ApiGridVisibility => false
      case ApiDetailVisibility => false
    })
  }
}

/**
 * Publishes for api.
 *
 * <dl>
 *   <dt>subject</dt>
 *   <dd>Api version of plain.</dd>
 *   <dt>object</dt>
 *   <dd>Derived from view visibility. In case of GridVisibility, use ApiGridVisibility.</dd>
 * </dl>
 */
case object ApiVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr) match {
      case Nil => true
      case xs => xs.exists(_ match {
        case WholeVisibility => false
        case PlainVisibility => false
        case GridVisibility => false
        case DetailVisibility => false
        case ApiVisibility => true
        case ApiGridVisibility => true
        case ApiDetailVisibility => true
      })
    }
  }
}

/**
 * Publishes for api grid.
 *
 * <dl>
 *   <dt>subject</dt>
 *   <dd>When ApiDetailVisibility then true, otherwise in case of ApiVisibility, derived from GridVisibility.</dd>
 *   <dt>object</dt>
 *   <dd>grid for api</dd>
 * </dl>
 */
case object ApiGridVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr) match {
      case Nil => true
      case xs => {
        xs.contains(ApiGridVisibility) ||
        xs.contains(ApiVisibility) && GridVisibility.isVisible(entity)(attr)
      }
    }
  }
}

/**
 * Publishes for api detail.
 * 
 * <dl>
 *   <dt>subject</dt>
 *   <dd>When ApiDetailVisibility then true, otherwise in case of ApiVisibility, derived from DetailVisibility.</dd>
 *   <dt>object</dt>
 *   <dd>detail for api</dd>
 * </dl>
 */
case object ApiDetailVisibility extends PVisibility {
  def isVisible(entity: PObjectEntity)(attr: PAttribute) = {
    entity.getDisplayVisibilities(attr) match {
      case Nil => true
      case xs =>
        xs.contains(ApiDetailVisibility) ||
        xs.contains(ApiVisibility) && DetailVisibility.isVisible(entity)(attr)
    }
  }
}
