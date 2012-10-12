package org.simplemodeling.SimpleModeler.builder
import com.asamioffice.goldenport.text.UString
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.goldenport.entity.GEntityContext

/*
 * @since   Dec.  8, 2011
 *  version Dec. 12, 2011
 * @version Oct. 12, 2012
 * @author  ASAMI, Tomoharu
 */
case class Strategy(naming: NamingStrategy, slot: SlotStrategy)

trait NamingStrategy {
  def makeName(name: String, kind: ElementKind): String = {
    UString.capitalize(name)
  }
}

object DefaultNamingStrategy extends NamingStrategy {
}

object NoneNamingStrategy extends NamingStrategy {
}

object SimpleModelingNamingStrategy extends NamingStrategy {
  override def makeName(name: String, kind: ElementKind) = {
    val prefix = kind match {
      case ActorKind             => "DEA"
      case ResourceKind          => "DER"
      case EventKind             => "DEE"
      case RoleKind              => "DEO"
      case SummaryKind           => "DES"
      case EntityKind            => "DE"
      case RuleKind              => "DR"
      case UsecaseKind           => "BU"
      case StateMachineKind      => "DM"
      case StateMachineStateKind => "DMS"
      case _                     => error("not implemented yet = " + kind)
    }
    prefix + UString.capitalize(name)
  }
}

abstract class SlotStrategy(val context: GEntityContext, val packageName: String) {
  def makeAttributesForBaseObject(obj: SMMEntityEntity) {
  }

  def makeAttributesForDerivedObject(obj: SMMEntityEntity) {
  }

  protected final def build_id_if_needed(obj: SMMEntityEntity) {
    def nonid = !obj.attributes.exists(_.id)
    def idneededkind = obj.kind.isIdNeeded
/*
    val id = obj.attributes.collect {
      case it: SMMValueIdType => it
    }
    if (id.isEmpty) {
      build_id(obj)
    }
*/
    if (idneededkind && nonid) {
      build_id(obj)
    }
  }

  protected final def build_id(anEntity: SMMEntityEntity) {
    val term = anEntity.term + "Id" // "番号" // XXX
    val name = "DVI" + UString.capitalize(term)
    val value = new SMMEntityEntity(context)
    value.name = name
    value.kind = IdKind
    value.term = term
    value.packageName = packageName
    value.attribute("value", SMMStringType)
    anEntity.addPrivateObject(value)
    anEntity.attribute(term, value)
  }

  protected final def build_dateTime(anEntity: SMMEntityEntity) {
    if (anEntity.kind != "event") return
    anEntity.attribute("dateTime", new SMMValueType("XDateTime", "org.simplemodeling.dsl.datatype"))
    //    anEntity.attribute("発生日時", new SMMValueType("XDateTime", "org.simplemodeling.dsl.datatype"))
  }

  protected final def build_name(anEntity: SMMEntityEntity) {
    if (anEntity.kind == "event") return
    val term = anEntity.term + "Name" // "名" // XXX
    val name = "DVN" + UString.capitalize(term)
    val value = new SMMEntityEntity(context)
    value.name = name
    value.kind = NameKind
    value.term = term
    value.packageName = packageName
    value.attribute("value", SMMStringType)
    anEntity.addPrivateObject(value)
    anEntity.attribute(term, value)
  }
}

abstract class SlotStrategyClass {
  def apply(context: GEntityContext, packageName: String): SlotStrategy
}

class DefaultSlotStrategy(aContext: GEntityContext, aName: String) extends SlotStrategy(aContext, aName) {
  override def makeAttributesForBaseObject(obj: SMMEntityEntity) {
    build_id_if_needed(obj)
  }  
}

object DefaultSlotStrategy extends SlotStrategyClass {
  def apply(context: GEntityContext, packageName: String): SlotStrategy = {
    new DefaultSlotStrategy(context, packageName)
  }
}

class NoneSlotStrategy(aContext: GEntityContext, aName: String) extends SlotStrategy(aContext, aName) {
}

object NoneSlotStrategy extends SlotStrategyClass {
  def apply(context: GEntityContext, packageName: String): SlotStrategy = {
    new NoneSlotStrategy(context, packageName)
  }  
}

class SimpleSlotStrategy(aContext: GEntityContext, aName: String) extends SlotStrategy(aContext, aName) {
  override def makeAttributesForBaseObject(anObj: SMMEntityEntity) {
    build_id(anObj)
    build_dateTime(anObj)
    build_name(anObj)    
  }
}

object SimpleSlotStrategy extends SlotStrategyClass {
  def apply(context: GEntityContext, packageName: String): SlotStrategy = {
    new SimpleSlotStrategy(context, packageName)
  }  
}

class SchemaorgSlotStrategy(aContext: GEntityContext, aName: String) extends SlotStrategy(aContext, aName) {
  override def makeAttributesForBaseObject(anObj: SMMEntityEntity) {
    build_id(anObj)
    build_dateTime(anObj)
    build_name(anObj)    
  }
}

object SchemaorgSlotStrategy extends SlotStrategyClass {
  def apply(context: GEntityContext, packageName: String): SlotStrategy = {
    new SchemaorgSlotStrategy(context, packageName)
  }  
}
