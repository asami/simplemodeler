package org.simplemodeling.SimpleModeler.transformers.gaej

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities.gae._
import org.simplemodeling.SimpleModeler.entities.gaej._
import org.simplemodeling.SimpleModeler.entities.gwt._
import org.simplemodeling.SimpleModeler.entities.atom._
import org.simplemodeling.SimpleModeler.entities.rest._
import org.simplemodeling.SimpleModeler.transformers.gae.SimpleModel2GaeRealmTransformer
import org.goldenport.value._
import org.goldenport.service.GServiceContext
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.goldenport.entities.fs.FileStoreEntity
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.UJavaString
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap

/*
 * @since   Apr.  9, 2009
 *  version Apr. 17, 2011
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class SimpleModel2GaeJavaRealmTransformer(val simpleModel: SimpleModelEntity, val serviceContext: GServiceContext) {
  private val _context = new GaejEntityContext(simpleModel.entityContext, serviceContext)
  private val _gaejRealm = new GaeJavaRealmEntity(_context)
  private val _entities = new ArrayBuffer[GaejEntityEntity]
  private val _parts = new ArrayBuffer[GaejEntityPartEntity]
  private val _documents = new ArrayBuffer[GaejDocumentEntity]
  private val _powertypes = new ArrayBuffer[GaejPowertypeEntity]
  private val _services = new ArrayBuffer[GaejServiceEntity]
  // available after Resolver
  private lazy val _entityRefs = for (entity <- _entities) yield {
    (entity.qualifiedName, entity.term)
  }

  //
  private def make_class_name(gaeObj: GaeObjectEntity): String = {
    _context.className(gaeObj.modelObject)
  }

  private def make_class_name(anObject: SMObject): String = {
    _context.className(anObject)
  }

  private def make_term(gaeObj: GaeObjectEntity): String = {
    make_term(gaeObj.modelObject)
  }

  private def make_term(anObject: SMObject): String = {
    _context.enTerm(anObject)
  }

  private def make_entity_document_name(anObject: SMObject): String = {
    _context.entityDocumentName(anObject)
  }

  private def make_multiplicity(aMultiplicity: SMMultiplicity): GaejMultiplicity = {
    aMultiplicity.kind match {
      case m: SMMultiplicityOne => GaejOne
      case m: SMMultiplicityZeroOne => GaejZeroOne
      case m: SMMultiplicityOneMore => GaejOneMore
      case m: SMMultiplicityZeroMore => GaejZeroMore
      case m: SMMultiplicityRange => new GaejRange // XXX
    }
  }

  //
  def toGaeJavaRealm: GaeJavaRealmEntity = {
    _gaejRealm.open
    val toGae = new SimpleModel2GaeRealmTransformer(simpleModel, serviceContext)
    val gae = toGae.toGaeRealm
    gae.traverse(new Transformer())
    _gaejRealm.traverse(new Dump())
    _gaejRealm.traverse(new Resolver())
    make_service_utilities()
    _gaejRealm.traverse(new CrudMaker())
    make_project()
    make_gwt()
    make_atom()
    make_rest()
    _gaejRealm ensuring(_.isOpened)
  }

  private def transform_models(models: GaeModelsEntity) {
    val entities = for (gaeEntity <- models.entities) yield {
      val entity = make_entity(gaeEntity)
      val doc = make_entity_document(gaeEntity)
      entity.documentName = doc.name
      entity
    }
    make_domain_crud_service(models, entities)
    make_utilities(models, entities)
    make_MDEntityInfo(models, entities)
    make_MEEntityModelInfo(models, entities)
    for (gaePart <- models.parts) {
      val part = make_part(gaePart)
      val doc = make_part_document(gaePart)
      part.documentName = doc.name
    }
    for (gaePowertype <- models.powertypes) {
      val powertype = make_powertype(gaePowertype)
      powertype
    }
  }

  private def transform_document(document: GaeDocumentEntity) {
    make_document(document)
  }

  private def transform_powertype(powertype: GaePowertypeEntity) {
    make_powertype(powertype)
  }

  private def transform_service(service: GaeServiceEntity) {
    make_service(service)
  }

  private def make_entity(anEntity: GaeEntityEntity) = {
    val entity = new GaejEntityEntity(_context)
    build_object(entity, anEntity)
//    entity.documentName = anEntity.documentName
    entity.modelEntity = anEntity.modelEntity
    _entities += entity
    entity
  }

  private def make_part(aPart: GaeEntityPartEntity) = {
    val part = new GaejEntityPartEntity(_context)
    build_object(part, aPart)
//    part.documentName = aPart.documentName
    part.modelEntityPart = aPart.modelEntityPart
    _parts += part
    part
  }

  private def make_document(aDocument: GaeDocumentEntity) = {
    val document = new GaejDocumentEntity(_context)
    build_object(document, aDocument)
    document.modelDocumentOption = Some(aDocument.modelDocument)
    _documents += document
    document
  }

  private def make_powertype(aPowertype: GaePowertypeEntity) = {
//    record_trace("make_powertype = " + aPowertype)
    val powertype = new GaejPowertypeEntity(_context)
    build_object(powertype, aPowertype)
    powertype.modelPowertype = aPowertype.modelPowertype
    _powertypes += powertype
    powertype
  }

  private def make_service(aService: GaeServiceEntity) = {
    val service = new GaejServiceEntity(_context)
    build_object(service, aService)
    _services += service
    service
  }

  private def build_object(gaejObj: GaejObjectEntity, gaeObj: GaeObjectEntity) {
    val modelObject = gaeObj.modelObject
    gaejObj.name = make_class_name(gaeObj)
    gaejObj.term = modelObject.term
    gaejObj.term_en = modelObject.term_en
    gaejObj.term_ja = modelObject.term_ja
    gaejObj.termName = _context.termName(modelObject)
    gaejObj.termNameBase = _context.objectBaseName(modelObject)
    gaejObj.packageName = modelObject.packageName
    gaejObj.xmlNamespace = modelObject.xmlNamespace
    gaejObj.modelObject = modelObject
    modelObject.getBaseObject match {
      case Some(base) => {
        gaejObj.setBaseObjectType(make_class_name(base), base.packageName)
      }
      case None => {}
    }
    for (attr <- gaeObj.attributes) {
      gaejObj.attributes += make_attribute(attr, modelObject)
    }
/*
    for (power <- modelObject.powertypes) {
      gaejObj.attributes += make_powertype_relationship(power)
      record_trace("transformer power = " + power)
    }
*/
    for (oper <- modelObject.operations) {
      gaejObj.operations += make_operation(oper)
    }
    val pathname = "/src" + UJavaString.packageName2pathname(gaejObj.packageName) + "/" + gaejObj.name + ".java"
    _gaejRealm.setEntity(pathname, gaejObj)
    gaejObj
  }

  private def make_entity_document(anEntity: GaeEntityEntity) = {
    val modelObject = anEntity.modelObject
    val term = make_term(modelObject)
    val doc = new GaejDocumentEntity(_context)
    doc.name = make_entity_document_name(modelObject)
    doc.term = term
    doc.term_en = modelObject.term_en
    doc.term_ja = modelObject.term_ja
    doc.termName = _context.objectBaseName(modelObject)
    doc.packageName = modelObject.packageName
    doc.xmlNamespace = modelObject.xmlNamespace
    modelObject.getBaseObject match {
      case Some(base) => {
        val baseName = make_entity_document_name(base)
        doc.setBaseObjectType(baseName, base.packageName)
      }
      case None => {}
    }
    for (attr <- anEntity.attributes) {
      doc.attributes += make_attribute(attr, modelObject)
    }
    val pathname = "/src" + UJavaString.packageName2pathname(doc.packageName) + "/" + doc.name + ".java"
    _gaejRealm.setEntity(pathname, doc)
    doc.modelEntityOption = Some(modelObject.asInstanceOf[SMEntity])
    doc
  }

  private def make_part_document(aPart: GaeEntityPartEntity) = {
    val modelObject = aPart.modelObject
    val term = make_term(modelObject)
    val doc = new GaejDocumentEntity(_context)
    doc.name = make_entity_document_name(modelObject)
    doc.term = term
    doc.term_en = modelObject.term_en
    doc.term_ja = modelObject.term_ja
    doc.termName = _context.objectBaseName(modelObject)
    doc.packageName = modelObject.packageName
    doc.xmlNamespace = modelObject.xmlNamespace
    for (attr <- aPart.attributes) {
      doc.attributes += make_attribute(attr, modelObject)
    }
    val pathname = "/src" + UJavaString.packageName2pathname(doc.packageName) + "/" + doc.name + ".java"
    _gaejRealm.setEntity(pathname, doc)
    doc
  }

  private def make_attribute(anAttr: GaeAttribute, anObject: SMObject) = {
    def make_id_type(aType: GaeObjectType): GaejObjectType = {
      def make_plain_id_type = {
        val modelAttribute = anAttr.modelAttribute
        modelAttribute.idPolicy match {
          case p: SMAutoIdPolicy => GaejLongType
          case p: SMApplicationIdPolicy => GaejStringType
        }
      }

      make_plain_id_type
/*
      record_trace("make_id_type = " + anAttr.name + "/" + anObject.name)
      anObject match {
        case entity: SMEntity => {
          record_trace("make_id_type application_id = " + entity.appEngine.logical_operation)
          if (entity.appEngine.logical_operation) {
            GaejStringType
          } else {
            make_plain_id_type
          }
        }
        case _ => make_plain_id_type
      }
*/
    }

    def make_type(aType: GaeObjectType): GaejObjectType = {
      def generic_type(t: GaeGenericType) = {
        make_attribute_type(t.modelAttributeType)
      }

      def entity_type(t: GaeEntityType) = {
        require (anAttr.modelAssociation != null)
//        record_trace("gae2gaej entity type = %s %s".format(t.name, t.packageName))
        val name = make_class_name(t.entity)
        t.entity match {
          case entity: GaeEntityEntity => {
            new GaejEntityType(name, t.packageName)
          }
          case part: GaeEntityPartEntity => {
            new GaejEntityPartType(name, t.packageName)
          }
        }
      }

      def powertype_type(t: GaePowertypeType) = {
//        record_trace("gae2gaej powertype type = %s %s".format(t.name, t.packageName))
        val name = make_class_name(t.powertype)
        new GaejPowertypeType(name, t.packageName)
      }

      def plain_type: GaejObjectType = {
//        record_trace("gae2gaej plain type = %s, %s, %s".format(aType, aType.getClass, anAttr.name))
        aType match {
          case t: GaeStringType => GaejStringType
          case t: GaeByteStringType => GaejByteStringType
          case t: GaeBooleanType => GaejBooleanType
          case t: GaeIntegerType => {
            t.modelAttributeType match {
              case null => GaejIntType
              case mt => {
                mt.qualifiedName match {
                  case "org.simplemodeling.dsl.datatype.XByte" => GaejByteType
                  case "org.simplemodeling.dsl.datatype.XShort" => GaejShortType
                  case "org.simplemodeling.dsl.datatype.XInt" => GaejIntType
                  case "org.simplemodeling.dsl.datatype.XLong" => GaejLongType
                  case qName => error("type = " + qName)
                }
              }
            }
          }
          case t: GaeFloatType => {
            t.modelAttributeType match {
              case null => GaejFloatType
              case mt => {
                mt.qualifiedName match {
                  case "org.simplemodeling.dsl.datatype.XFloat" => GaejFloatType
                  case "org.simplemodeling.dsl.datatype.XDouble" => GaejDoubleType
                  case qName => error("type = " + qName)
                }
              }
            }
          }
          case t: GaeDateTimeType => GaejDateTimeType
          case t: GaeDateType => GaejDateType
          case t: GaeTimeType => GaejTimeType
          case t: GaeUserType => GaejUserType
          case t: GaeBlobType => GaejBlobType
          case t: GaeTextType => GaejTextType
          case t: GaeCategoryType => GaejCategoryType
          case t: GaeLinkType => GaejLinkType
          case t: GaeEmailType => GaejEmailType
          case t: GaeGeoPtType => GaejGeoPtType
          case t: GaeIMType => GaejIMType
          case t: GaePhoneNumberType => GaejPhoneNumberType
          case t: GaePostalAddressType => GaejPostalAddressType
          case t: GaeRatingType => GaejRatingType
          case t: GaeUrlType => GaejUrlType
          case t: GaePowertypeType => powertype_type(t)
          case t: GaeGenericType => generic_type(t)
          case t: GaeEntityType => entity_type(t)
        }
      }

      def complex_type(modelAttrType: SMAttributeType): GaejObjectType = {
        make_attribute_type(modelAttrType)
      }
/*
      def complex_type(modelAttrType: SMAttributeType): GaejObjectType = {
        record_trace("gae2gaej complex type = %s, %s, %s".format(aType, aType.getClass, anAttr.name))
        val gaejType = aType match {
          case t: GaeStringType => new GaejStringType
          case t: GaeByteStringType => new GaejByteStringType
          case t: GaeBooleanType => new GaejBooleanType
          case t: GaeIntegerType => {
            t.modelAttributeType match {
              case null => new GaejIntType
              case mt => {
                mt.qualifiedName match {
                  case "org.simplemodeling.dsl.datatype.XByte" => new GaejByteType
                  case "org.simplemodeling.dsl.datatype.XShort" => new GaejShortType
                  case "org.simplemodeling.dsl.datatype.XInt" => new GaejIntType
                  case "org.simplemodeling.dsl.datatype.XLong" => new GaejLongType
                  case qName => error("type = " + qName)
                }
              }
            }
          }
          case t: GaeFloatType => {
            t.modelAttributeType match {
              case null => new GaejFloatType
              case mt => {
                mt.qualifiedName match {
                  case "org.simplemodeling.dsl.datatype.XFloat" => new GaejFloatType
                  case "org.simplemodeling.dsl.datatype.XDouble" => new GaejDoubleType
                  case qName => error("type = " + qName)
                }
              }
            }
          }
          case t: GaeDateTimeType => new GaejDateTimeType
          case t: GaeDateType => new GaejDateType
          case t: GaeTimeType => new GaejTimeType
          case t: GaeUserType => new GaejUserType
          case t: GaeBlobType => new GaejBlobType
          case t: GaeTextType => new GaejTextType
          case t: GaeCategoryType => new GaejCategoryType
          case t: GaeLinkType => new GaejLinkType
          case t: GaeEmailType => new GaejEmailType
          case t: GaeGeoPtType => new GaejGeoPtType
          case t: GaeIMType => new GaejIMType
          case t: GaePhoneNumberType => new GaejPhoneNumberType
          case t: GaePostalAddressType => new GaejPostalAddressType
          case t: GaeRatingType => new GaejRatingType
          case t: GaeUrlType => new GaejUrlType
          case t: GaeGenericType => generic_type(t)
          case t: GaeEntityType => entity_type(t)
        }
        val constraints =
          for (constraint <- aType.modelAttributeType.constraints) yield {
            new GaejConstraint(constraint.name, constraint.value)
          }
        gaejType.addConstraints(constraints)
        gaejType
      }
*/
      aType.modelAttributeType match {
        case null => plain_type
        case modelAttrType: SMAttributeType => complex_type(modelAttrType)
      }
    }

    def make_multiplicity(aMultiplicity: GaeMultiplicity): GaejMultiplicity = {
      aMultiplicity match {
        case m: GaeOne => GaejOne
        case m: GaeZeroOne => GaejZeroOne
        case m: GaeOneMore => GaejOneMore
        case m: GaeZeroMore => GaejZeroMore
        case m: GaeRange => new GaejRange() // XXX
      }
    }

    val attrName = make_attr_name(anAttr)
    val attr = if (anAttr.isId) {
      new GaejAttribute(attrName, make_id_type(anAttr.attributeType))
    } else {
      new GaejAttribute(attrName, make_type(anAttr.attributeType))
    }
    attr.isId = anAttr.isId
    attr.multiplicity = make_multiplicity(anAttr.multiplicity)
    attr.modelAttribute = anAttr.modelAttribute
    attr.modelAssociation = anAttr.modelAssociation
    attr
  }

  private def make_attr_name(attr: GaeAttribute): String = {
    if (attr.modelAttribute != null) {
      _context.attributeName(attr.modelAttribute)
    } else if (attr.modelAssociation != null) {
      _context.associationName(attr.modelAssociation)
    } else if (attr.modelPowertype != null) {
      _context.powertypeName(attr.modelPowertype)
    } else {
      throw new IllegalArgumentException("no attr name")
    }
  }

  private def make_attribute_type(attrType: SMAttributeType): GaejObjectType = {
    def make_simple = {
      attrType.qualifiedName match {
	case "org.simplemodeling.dsl.datatype.XString" => GaejStringType
	//
	case "org.simplemodeling.dsl.datatype.XBoolean" => GaejBooleanType
	case "org.simplemodeling.dsl.datatype.XByte" => GaejByteType
	case "org.simplemodeling.dsl.datatype.XUnsignedByte" => GaejUnsignedByteType
	case "org.simplemodeling.dsl.datatype.XShort" => GaejShortType
	case "org.simplemodeling.dsl.datatype.XUnsignedShort" => GaejUnsignedShortType
	case "org.simplemodeling.dsl.datatype.XInt" => GaejIntType
	case "org.simplemodeling.dsl.datatype.XUnsignedInt" => GaejUnsignedIntType
	case "org.simplemodeling.dsl.datatype.XLong" => GaejLongType
	case "org.simplemodeling.dsl.datatype.XUnsignedLong" => GaejUnsignedLongType
	case "org.simplemodeling.dsl.datatype.XInteger" => GaejIntegerType
	case "org.simplemodeling.dsl.datatype.XPositiveInteger" => GaejPositiveIntegerType
	case "org.simplemodeling.dsl.datatype.XNonPositiveInteger" => GaejNonPositiveIntegerType
	case "org.simplemodeling.dsl.datatype.XNegativeInteger" => GaejNegativeIntegerType
	case "org.simplemodeling.dsl.datatype.XNonNegativeInteger" => GaejNonNegativeIntegerType
	case "org.simplemodeling.dsl.datatype.XFloat" => GaejFloatType
	case "org.simplemodeling.dsl.datatype.XDouble" => new GaejDoubleType
	case "org.simplemodeling.dsl.datatype.XDecimal" => GaejDecimalType
	//
	case "org.simplemodeling.dsl.datatype.XDuration" => GaejDurationType
	case "org.simplemodeling.dsl.datatype.XDateTime" => GaejDateTimeType
	case "org.simplemodeling.dsl.datatype.XDate" => GaejDateType
	case "org.simplemodeling.dsl.datatype.XTime" => GaejTimeType
	case "org.simplemodeling.dsl.datatype.XGYearMonth" => GaejGYearMonthType
	case "org.simplemodeling.dsl.datatype.XGMonthDay" => GaejGMonthDayType
	case "org.simplemodeling.dsl.datatype.XGYear" => GaejGYearType
	case "org.simplemodeling.dsl.datatype.XGMonth" => GaejGMonthType
	case "org.simplemodeling.dsl.datatype.XGDay" => GaejGDayType
	//
	case "org.simplemodeling.dsl.datatype.XAnyURI" => GaejAnyURIType
	case "org.simplemodeling.dsl.datatype.XHexByte" => GaejByteStringType
	case "org.simplemodeling.dsl.datatype.XBase64Byte" => GaejBlobType
	case "org.simplemodeling.dsl.datatype.XLanguage" => GaejLanguageType
	//
	case "org.simplemodeling.dsl.datatype.ext.XText" => GaejTextType
	case "org.simplemodeling.dsl.datatype.ext.XLink" => GaejLinkType
	case "org.simplemodeling.dsl.datatype.ext.XCategory" => GaejCategoryType
	case "org.simplemodeling.dsl.datatype.ext.XUser" => GaejUserType
	case "org.simplemodeling.dsl.datatype.ext.XEmail" => GaejEmailType
	case "org.simplemodeling.dsl.datatype.ext.XGeoPt" => GaejGeoPtType
	case "org.simplemodeling.dsl.datatype.ext.XIM" => GaejIMType
	case "org.simplemodeling.dsl.datatype.ext.XPhoneNumber" => GaejPhoneNumberType
	case "org.simplemodeling.dsl.datatype.ext.XPostalAddress" => GaejPostalAddressType
	case "org.simplemodeling.dsl.datatype.ext.XRating" => GaejRatingType
	//
	case "org.simplemodeling.dsl.domain.values.Money" => GaejMoneyType
	//
	case _ => new GaejGenericType(attrType)
      }
    }

    def make_complex = {
      attrType.qualifiedName match {
	case "org.simplemodeling.dsl.datatype.XString" => new GaejStringType(attrType)
	//
	case "org.simplemodeling.dsl.datatype.XBoolean" => new GaejBooleanType(attrType)
	case "org.simplemodeling.dsl.datatype.XByte" => new GaejByteType(attrType)
	case "org.simplemodeling.dsl.datatype.XUnsignedByte" => new GaejUnsignedByteType(attrType)
	case "org.simplemodeling.dsl.datatype.XShort" => new GaejShortType(attrType)
	case "org.simplemodeling.dsl.datatype.XUnsignedShort" => new GaejUnsignedShortType(attrType)
	case "org.simplemodeling.dsl.datatype.XInt" => new GaejIntType(attrType)
	case "org.simplemodeling.dsl.datatype.XUnsignedInt" => new GaejUnsignedIntType(attrType)
	case "org.simplemodeling.dsl.datatype.XLong" => new GaejLongType(attrType)
	case "org.simplemodeling.dsl.datatype.XUnsignedLong" => new GaejUnsignedLongType(attrType)
	case "org.simplemodeling.dsl.datatype.XInteger" => new GaejIntegerType(attrType)
	case "org.simplemodeling.dsl.datatype.XPositiveInteger" => new GaejPositiveIntegerType(attrType)
	case "org.simplemodeling.dsl.datatype.XNonPositiveInteger" => new GaejNonPositiveIntegerType(attrType)
	case "org.simplemodeling.dsl.datatype.XNegativeInteger" => new GaejNegativeIntegerType(attrType)
	case "org.simplemodeling.dsl.datatype.XNonNegativeInteger" => new GaejNonNegativeIntegerType(attrType)
	case "org.simplemodeling.dsl.datatype.XFloat" => new GaejFloatType(attrType)
	case "org.simplemodeling.dsl.datatype.XDouble" => new GaejDoubleType(attrType)
	case "org.simplemodeling.dsl.datatype.XDecimal" => new GaejDecimalType(attrType)
	//
	case "org.simplemodeling.dsl.datatype.XDuration" => new GaejDurationType(attrType)
	case "org.simplemodeling.dsl.datatype.XDateTime" => new GaejDateTimeType(attrType)
	case "org.simplemodeling.dsl.datatype.XDate" => new GaejDateType(attrType)
	case "org.simplemodeling.dsl.datatype.XTime" => new GaejTimeType(attrType)
	case "org.simplemodeling.dsl.datatype.XGYearMonth" => new GaejGYearMonthType(attrType)
	case "org.simplemodeling.dsl.datatype.XGMonthDay" => new GaejGMonthDayType(attrType)
	case "org.simplemodeling.dsl.datatype.XGYear" => new GaejGYearType(attrType)
	case "org.simplemodeling.dsl.datatype.XGMonth" => new GaejGMonthType(attrType)
	case "org.simplemodeling.dsl.datatype.XGDay" => new GaejGDayType(attrType)
	//
	case "org.simplemodeling.dsl.datatype.XAnyURI" => new GaejAnyURIType(attrType)
	case "org.simplemodeling.dsl.datatype.XHexByte" => new GaejByteStringType(attrType)
	case "org.simplemodeling.dsl.datatype.XBase64Byte" => new GaejBlobType(attrType)
	case "org.simplemodeling.dsl.datatype.XLanguage" => new GaejLanguageType(attrType)
	//
	case "org.simplemodeling.dsl.datatype.ext.XText" => new GaejTextType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XLink" => new GaejLinkType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XCategory" => new GaejCategoryType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XUser" => new GaejUserType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XEmail" => new GaejEmailType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XGeoPt" => new GaejGeoPtType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XIM" => new GaejIMType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XPhoneNumber" => new GaejPhoneNumberType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XPostalAddress" => new GaejPostalAddressType(attrType)
	case "org.simplemodeling.dsl.datatype.ext.XRating" => new GaejRatingType(attrType)
	//
	case "org.simplemodeling.dsl.domain.values.Money" => new GaejMoneyType(attrType)
	//
	case _ => new GaejGenericType(attrType)
      }
    }

    if (attrType.constraints.isEmpty) {
      make_simple
    } else {
      make_complex
    }
  }

  private def make_powertype_relationship(powertype: SMPowertypeRelationship): GaejAttribute = {
    new GaejAttribute(powertype.powertype.name,
                      new GaejPowertypeType(powertype.powertype.name,
                                            powertype.powertype.packageName))
    .multiplicity_is(make_multiplicity(powertype.multiplicity))
  }

  private def make_operation(anOperation: SMOperation): GaejOperation = {
    def make_document(doc: SMDocument) = doc match {
      case SMNullDocument => None
      case _ => Some(new GaejDocumentType(doc.name, doc.packageName))
    }

    val in = make_document(anOperation.in)
    val out = make_document(anOperation.out)
    new GaejOperation(anOperation.name, in , out)
  }
  

  private def make_domain_crud_service(aModels: GaeModelsEntity, theEntities: Seq[GaejEntityEntity]) {
    val packageName = aModels.packageName
    val xmlNamespace = aModels.xmlNamespace
    val serviceName = _context.entityRepositoryServiceName(packageName)
    val servicePathname = "/src" + UJavaString.packageName2pathname(packageName) + "/" + serviceName + ".java"

    val service = new GaejDomainServiceEntity(_context)
    service.packageName = packageName
    service.xmlNamespace = xmlNamespace
    service.name = serviceName
    service.entities ++= theEntities
    _gaejRealm.setEntity(servicePathname, service)
    _services += service
  }

  private def make_utilities(aModels: GaeModelsEntity, theEntities: Seq[GaejEntityEntity]) {
    val packageName = aModels.packageName
    val pathname = "/src" + UJavaString.packageName2pathname(packageName) + "/Util.java"

    val utilities = new GaejUtilitiesEntity(_context)
    utilities.packageName = packageName
    utilities.name = "Util"
    utilities.entities ++= theEntities
    _gaejRealm.setEntity(pathname, utilities)
  }

  private def make_MDEntityInfo(aModels: GaeModelsEntity, theEntities: Seq[GaejEntityEntity]) {
    val packageName = aModels.packageName
    val pathname = "/src" + UJavaString.packageName2pathname(packageName) + "/MDEntityInfo.java"

    val info = new GaejMDEntityInfoEntity(_context)
    info.packageName = packageName
    info.name = "MDEntityInfo"
    _gaejRealm.setEntity(pathname, info)
  }

  private def make_MEEntityModelInfo(aModels: GaeModelsEntity, theEntities: Seq[GaejEntityEntity]) {
    val packageName = aModels.packageName
    val pathname = "/src" + UJavaString.packageName2pathname(packageName) + "/MEEntityModelInfo.java"

    val info = new GaejMEEntityModelInfoEntity(_context)
    info.packageName = packageName
    info.name = "MEEntityModelInfo"
    _gaejRealm.setEntity(pathname, info)
  }

  class Transformer extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      val node = aNode.asInstanceOf[GTreeContainerEntityNode]
      node.entity match {
	case Some(models: GaeModelsEntity) => transform_models(models)
        case Some(doc: GaeDocumentEntity) => transform_document(doc)
        case Some(powertype: GaePowertypeEntity) => transform_powertype(powertype) // realy not used.
        case Some(service: GaeServiceEntity) => transform_service(service)
	case Some(_) => {}
	case None => {}
      }
    }
  }

  class Dump extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      println("pathname = " + aNode.pathname)
    }
  }

  class Resolver extends GTreeVisitor[GContent] {
    def findObject(aQName: String): Option[GaejObjectEntity] = {
      _gaejRealm.getNode("/src" + UJavaString.className2pathname(aQName) + ".java") match {
        case Some(node) => node.entity.asInstanceOf[Some[GaejObjectEntity]]
        case None => None
      }
    }

    def getObject(aQName: String): GaejObjectEntity = {
      try {
        findObject(aQName).get
      } catch {
        case _ => error("No object = " + aQName)
      }
    }

    def findEntity(aQName: String): Option[GaejEntityEntity] = {
      _gaejRealm.getNode("/src" + UJavaString.className2pathname(aQName) + ".java") match {
        case Some(node) => node.entity.asInstanceOf[Some[GaejEntityEntity]]
        case None => None
      }
    }

    def getEntity(aQName: String): GaejEntityEntity = {
      try {
        findEntity(aQName).get
      } catch {
        case _ => error("No entity = " + aQName)
      }
    }

    def findPart(aQName: String): Option[GaejEntityPartEntity] = {
      _gaejRealm.getNode("/src" + UJavaString.className2pathname(aQName) + ".java") match {
        case Some(node) => node.entity.asInstanceOf[Some[GaejEntityPartEntity]]
        case None => None
      }
    }

    def getPart(aQName: String): GaejEntityPartEntity = {
      try {
        findPart(aQName).get
      } catch {
        case _ => error("No part = " + aQName)
      }
    }

    def findPowertype(aQName: String): Option[GaejPowertypeEntity] = {
      _gaejRealm.getNode("/src" + UJavaString.className2pathname(aQName) + ".java") match {
        case Some(node) => node.entity.asInstanceOf[Some[GaejPowertypeEntity]]
        case None => None
      }
    }

    def getPowertype(aQName: String): GaejPowertypeEntity = {
      try {
        findPowertype(aQName).get
      } catch {
        case _ => error("No powertype = " + aQName)
      }
    }

    def findDocument(aQName: String): Option[GaejDocumentEntity] = {
      _gaejRealm.getNode("/src" + UJavaString.className2pathname(aQName) + ".java") match {
        case Some(node) => node.entity.asInstanceOf[Some[GaejDocumentEntity]]
        case None => None
      }
    }

    def getDocument(aQName: String): GaejDocumentEntity = {
      try {
        findDocument(aQName).get
      } catch {
        case _ => error("No document = " + aQName)
      }
    }

    def resolve_entity(obj: GaejEntityEntity) {
      resolve_object(obj)
    }

    def resolve_part(obj: GaejEntityPartEntity) {
      resolve_object(obj)
    }

    def resolve_powertype(obj: GaejPowertypeEntity) {
      resolve_object(obj)
    }

    def resolve_document(doc: GaejDocumentEntity) {
      resolve_object(doc)
    }

    def resolve_service(service: GaejServiceEntity) {
      resolve_object(service)
    }

    def resolve_object(obj: GaejObjectEntity) {
      resolve_base(obj)
      resolve_attributes(obj)
      resolve_operations(obj)
    }      

    def resolve_base(obj: GaejObjectEntity) {
      obj.getBaseObjectType match {
        case Some(base) => {
          base.reference = getObject(base.qualifiedName)
        }
        case None => {}
      }
    }

    def resolve_attributes(obj: GaejObjectEntity) {
      for (attr <- obj.attributes) {
        attr.attributeType match {
          case entityType: GaejEntityType => {
            entityType.entity = getEntity(entityType.qualifiedName)
          }
          case partType: GaejEntityPartType => {
            partType.part = getPart(partType.qualifiedName)
          }
          case powertypeType: GaejPowertypeType => {
            powertypeType.powertype = getPowertype(powertypeType.qualifiedName)
          }
          case documentType: GaejDocumentType => {
            documentType.document = getDocument(documentType.qualifiedName)
          }
          case _ => //
        }
      }
    }

    def resolve_operations(obj: GaejObjectEntity) {
      for (op <- obj.operations) {
        op.in match {
          case Some(docType) => docType.document = getDocument(docType.qualifiedName)
          case None => {}
        }
        op.out match {
          case Some(docType) => docType.document = getDocument(docType.qualifiedName)
          case None => {}
        }
      }
    }

    override def enter(aNode: GTreeNode[GContent]) {
      aNode.asInstanceOf[GContainerEntityNode].entity match {
        case Some(entity: GaejEntityEntity) => resolve_entity(entity)
        case Some(entity: GaejEntityPartEntity) => resolve_part(entity)
        case Some(powertype: GaejPowertypeEntity) => resolve_powertype(powertype)
        case Some(doc: GaejDocumentEntity) => resolve_document(doc)
        case Some(service: GaejDomainServiceEntity) => resolve_service(service)
        case Some(service: GaejServiceEntity) => resolve_service(service)
        case Some(utility: GaejUtilitiesEntity) => //
        case Some(utility: GaejMDEntityInfoEntity) => //
        case Some(utility: GaejMEEntityModelInfoEntity) => //
        case None => //
      }
    }
  }

  private def make_service_utilities() {
//    import scala.collection.jcl.Conversions.{convertList, convertSet}
    import scala.collection.JavaConversions._
    val servicesInPackage = new MultiValueMap[String, GaejServiceEntity]
    for (service <- _services) {
      servicesInPackage.add(service.packageName, service)
    }

    def make_factory() {
      for (packageName <- servicesInPackage.keySet) {
        val factory = new GaejFactoryEntity(_context)
        factory.name = _context.factoryName(packageName)
        factory.packageName = packageName
        factory.services ++= servicesInPackage.get(packageName)
        val packagePathname = _context.packagePathname(packageName)
        val factoryPathname = packagePathname + "/" + factory.name + ".java"
        _gaejRealm.setEntity(factoryPathname, factory)
      }
    }

    def make_context() {
      for (packageName <- servicesInPackage.keySet) {
        val context = new GaejContextEntity(_context)
        context.name = _context.contextName(packageName)
        context.packageName = packageName
        context.services ++= servicesInPackage.get(packageName)
        val packagePathname = _context.packagePathname(packageName)
        val contextPathname = packagePathname + "/" + context.name + ".java"
        _gaejRealm.setEntity(contextPathname, context)
      }
    }

    def make_event() {
      for (packageName <- servicesInPackage.keySet) {
        val event = new GaejEventEntity(_context)
        event.name = _context.eventName(packageName)
        event.packageName = packageName
        val packagePathname = _context.packagePathname(packageName)
        val eventPathname = packagePathname + "/" + event.name + ".java"
        _gaejRealm.setEntity(eventPathname, event)
      }
    }

    make_factory
    make_context
    make_event
  }

  class CrudMaker extends GTreeVisitor[GContent] {
    override def enter(aNode: GTreeNode[GContent]) {
      val node = aNode.asInstanceOf[GTreeContainerEntityNode]
      node.entity match {
        case Some(entity: GaejEntityEntity) => make_controllers_views(entity)
        case Some(part: GaejEntityPartEntity) => {}
        case Some(powertype: GaejPowertypeEntity) => {}
        case Some(doc: GaejDocumentEntity) => {}
        case Some(service: GaejDomainServiceEntity) => {}
        case Some(service: GaejServiceEntity) => {}
        case Some(utility: GaejUtilitiesEntity) => {}
        case Some(factory: GaejFactoryEntity) => {}
        case Some(context: GaejContextEntity) => {}
        case Some(event: GaejEventEntity) => {}
        case Some(info: GaejMDEntityInfoEntity) => {}
        case Some(model: GaejMEEntityModelInfoEntity) => {}
        case None => {}
      }
    }

    private def make_controllers_views(anEntity: GaejEntityEntity) {
      val packagePathname = "/src" + UJavaString.className2pathname(anEntity.packageName)

      def make_controllers {
        val controllerPathname = packagePathname + "/controller/" + anEntity.name + "Controller.java"
        val controller = new GaejControllerEntity(anEntity, _context)
        controller.name = anEntity.name + ".java"
        controller.setEntityRefs(_entityRefs)
        _gaejRealm.setEntity(controllerPathname, controller)
      }

      def make_controller_util {
        val utilPathname = packagePathname + "/controller/ServletUtil.java"
        if (_gaejRealm.getNode(utilPathname).isDefined) {
          return
        }
        val util = new GaejServletUtilitiesEntity(_context)
        util.name = "ServletUtil"
        util.packageName = anEntity.packageName + ".controller"
        _gaejRealm.setEntity(utilPathname, util)
      }

      def make_views {
        val viewPathname = _context.entityRepositoryServiceConfig.viewFilePathname(anEntity)
        val indexView = new GaejIndexViewEntity(anEntity, _context)
        indexView.name = "index.jsp"
        _gaejRealm.setEntity(viewPathname + "/" + indexView.name, indexView)
        val showView = new GaejShowViewEntity(anEntity, _context)
        showView.name = "show.jsp"
        _gaejRealm.setEntity(viewPathname + "/" + showView.name, showView)
        val editView = new GaejEditViewEntity(anEntity, _context)
        editView.name = "edit.jsp"
        _gaejRealm.setEntity(viewPathname + "/" + editView.name, editView)
        val newView = new GaejNewViewEntity(anEntity, _context)
        newView.name = "new.jsp"
        _gaejRealm.setEntity(viewPathname + "/" + newView.name, newView)
      }

      make_controllers
      make_controller_util
      make_views
    }
  }

  private def make_project() {
    val mayProject = serviceContext.getParameter("gaej.project")
    if (mayProject.isEmpty) return
    val projectName = mayProject.get.asInstanceOf[String]
    val webXml = new GaejWebXmlEntity(_services, _context)
    val indexHtml = new GaejProjectIndexHtmlEntity(_services, _context)
    val appEngineWebXml = new GaejAppEngineWebXmlEntity(_services, _context)
    _gaejRealm.setEntity("/war/WEB-INF/appengine-web.xml", appEngineWebXml)
    _gaejRealm.setEntity("/war/WEB-INF/web.xml", webXml)
    _gaejRealm.setEntity("/war/index.html", indexHtml)
  }

  private def make_gwt() {
    val mayGwt = serviceContext.getParameter("gaej.gwt")
    if (mayGwt.isEmpty) return

    val gwtContext = new GwtEntityContext(_context)

    def make_utility(packageName: String) {
      val pathname = "/src" + UJavaString.className2pathname(packageName) + "/Util.java"
      _gaejRealm.getNode(pathname) match {
        case Some(node) => {}
        case None => {
          val util = new GwtUtilitiesEntity(gwtContext);
          util.name = "Util"
          util.packageName = packageName
          _gaejRealm.setEntity(pathname, util)
        }
      }
    }

    def make_query(packageName: String) {
      val pathname = "/src" + UJavaString.className2pathname(packageName) + "/GwtQuery.java"
      _gaejRealm.getNode(pathname) match {
        case Some(node) => {}
        case None => {
          val util = new GwtQueryEntity(gwtContext);
          util.name = "GwtQuery"
          util.packageName = packageName
          _gaejRealm.setEntity(pathname, util)
        }
      }
    }

    def make_gwt_service(aService: GaejServiceEntity) {
      aService match {
        case domainService: GaejDomainServiceEntity => make_gwt_domain_service(domainService)
        case _ => make_gwt_plain_service(aService)
      }
    }

    def make_gwt_domain_service(gaejService: GaejDomainServiceEntity) {
      val packageName = gaejService.packageName
      val clientPackageName = packageName + ".client"
      val serverPackageName = packageName + ".server"
      val packagePathname = "/src" + UJavaString.className2pathname(packageName)
      val clientPathname = packagePathname + "/client"
      val serverPathname = packagePathname + "/server"
      val serviceName = _context.entityRepositoryGwtServiceName(packageName)
      val service = new GwtDomainServiceEntity(gaejService, gwtContext)
      service.name = serviceName
      service.packageName = clientPackageName
      val serviceAsync = new GwtDomainServiceAsyncEntity(gaejService, gwtContext)
      serviceAsync.name = serviceName + "Async"
      serviceAsync.packageName = clientPackageName
      val serviceImpl = new GwtDomainServiceImplEntity(gaejService, gwtContext)
      serviceImpl.name = serviceName + "Impl"
      serviceImpl.packageName = serverPackageName
      serviceImpl.basePackageName = packageName
      serviceImpl.clientPackageName = clientPackageName
      serviceImpl.serviceInterfaceName = service.name
      _gaejRealm.setEntity(clientPathname + "/" + service.name + ".java", service)
      _gaejRealm.setEntity(clientPathname + "/" + serviceAsync.name + ".java", serviceAsync)
      _gaejRealm.setEntity(serverPathname + "/" + serviceImpl.name + ".java", serviceImpl)
      make_utility(clientPackageName)
      make_query(clientPackageName)
    }

    def make_gwt_plain_service(gaejService: GaejServiceEntity) {
      val packageName = gaejService.packageName
      val clientPackageName = packageName + ".client"
      val serverPackageName = packageName + ".server"
      val packagePathname = "/src" + UJavaString.className2pathname(packageName)
      val clientPathname = packagePathname + "/client"
      val serverPathname = packagePathname + "/server"
      val serviceName = gwtContext.serviceName(gaejService)
      val service = new GwtServiceEntity(gaejService, gwtContext)
      service.name = serviceName
      service.packageName = clientPackageName
      val serviceAsync = new GwtServiceAsyncEntity(gaejService, gwtContext)
      serviceAsync.name = serviceName + "Async"
      serviceAsync.packageName = clientPackageName
      val serviceImpl = new GwtServiceImplEntity(gaejService, gwtContext)
      serviceImpl.name = serviceName + "Impl"
      serviceImpl.packageName = serverPackageName
      serviceImpl.basePackageName = packageName
      serviceImpl.clientPackageName = clientPackageName
      serviceImpl.serviceInterfaceName = service.name
      _gaejRealm.setEntity(clientPathname + "/" + service.name + ".java", service)
      _gaejRealm.setEntity(clientPathname + "/" + serviceAsync.name + ".java", serviceAsync)
      _gaejRealm.setEntity(serverPathname + "/" + serviceImpl.name + ".java", serviceImpl)
    }

    def make_gwt_entity_editor(gaejEntity: GaejEntityEntity) {
      val packageName = gaejEntity.packageName
      val clientPackageName = packageName + ".client"
      val packagePathname = "/src" + UJavaString.className2pathname(packageName)
      val clientPathname = packagePathname + "/client"
      val webPathname = gwtContext.viewFilePathname(gaejEntity)
      val docName = gwtContext.documentName(gaejEntity)
      val editorName = gwtContext.editorName(gaejEntity)
      val htmlName = "index" // gaejEntity.term

      def make_entity_doc {
        val doc = new GwtDocumentEntity(gaejEntity, gwtContext)
        doc.name = docName
        doc.packageName = clientPackageName
        doc.basePackageName = packageName
        _gaejRealm.setEntity(clientPathname + "/" + doc.name + ".java", doc)
      }

      def make_part_docs {
        for (attr <- gaejEntity.attributes) {
          attr.attributeType match {
            case p: GaejEntityPartType => {
              val doc = new GwtDocumentEntity(p.part, gwtContext)
              doc.name = gwtContext.documentName(p.part)
              doc.packageName = clientPackageName
              doc.basePackageName = packageName
              _gaejRealm.setEntity(clientPathname + "/" + doc.name + ".java", doc)
            }
            case _ => {}
          }
        }
      }

      def make_editor {
        val editor = new GwtEntityEditorEntity(gaejEntity, gwtContext)
        editor.name = editorName
        editor.packageName = clientPackageName
        _gaejRealm.setEntity(clientPathname + "/" + editor.name + ".java", editor)
      }

      def make_html {
        val html = new GwtEntityEditorHtmlEntity(gaejEntity, gwtContext)
        html.name = htmlName
        _gaejRealm.setEntity(webPathname + "/" + html.name + ".html", html)
        make_utility(clientPackageName)
        make_wedgets(clientPackageName)
      }

      make_entity_doc
      make_part_docs
      make_editor
      make_html
    }

    def make_wedgets(packageName: String) {
      val pathname = "/src" + UJavaString.className2pathname(packageName)

      val d2Array = new GwtD2ArrayEntity(gwtContext)
      d2Array.name = "D2Array"
      d2Array.packageName = packageName
      _gaejRealm.setEntity(pathname + "/D2Array.java", d2Array)
      val column = new GwtColumnEntity(gwtContext)
      column.name = "Column"
      column.packageName = packageName
      _gaejRealm.setEntity(pathname + "/Column.java", column)
      val columnSet = new GwtColumnSetEntity(gwtContext)
      columnSet.name = "ColumnSet"
      columnSet.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ColumnSet.java", columnSet)
      val compositeColumn = new GwtCompositeColumnEntity(gwtContext)
      compositeColumn.name = "CompositeColumn"
      compositeColumn.packageName = packageName
      _gaejRealm.setEntity(pathname + "/CompositeColumn.java", compositeColumn)
      val datatypeColumn = new GwtDatatypeColumnEntity(gwtContext)
      datatypeColumn.name = "DatatypeColumn"
      datatypeColumn.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DatatypeColumn.java", datatypeColumn)
      val fieldFactory = new GwtFieldFactoryEntity(gwtContext)
      fieldFactory.name = "FieldFactory"
      fieldFactory.packageName = packageName
      _gaejRealm.setEntity(pathname + "/FieldFactory.java", fieldFactory)
      val iInputField = new GwtIInputFieldEntity(gwtContext)
      iInputField.name = "IInputField"
      iInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IInputField.java", iInputField)
      val iLabelField = new GwtILabelFieldEntity(gwtContext)
      iLabelField.name = "ILabelField"
      iLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ILabelField.java", iLabelField)
      val iValueEditor = new GwtIValueEditorEntity(gwtContext)
      iValueEditor.name = "IValueEditor"
      iValueEditor.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IValueEditor.java", iValueEditor)
      val record = new GwtRecordEntity(gwtContext)
      record.name = "Record"
      record.packageName = packageName
      _gaejRealm.setEntity(pathname + "/Record.java", record)
      val recordSet = new GwtRecordSetEntity(gwtContext)
      recordSet.name = "RecordSet"
      recordSet.packageName = packageName
      _gaejRealm.setEntity(pathname + "/RecordSet.java", recordSet)
      val recordInputField = new GwtRecordInputFieldEntity(gwtContext)
      recordInputField.name = "RecordInputField"
      recordInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/RecordInputField.java", recordInputField)
      val recordSetInputField = new GwtRecordSetInputFieldEntity(gwtContext)
      recordSetInputField.name = "RecordSetInputField"
      recordSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/RecordSetInputField.java", recordSetInputField)
      val valueInputFieldBase = new GwtValueInputFieldBaseEntity(gwtContext)
      valueInputFieldBase.name = "ValueInputFieldBase"
      valueInputFieldBase.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ValueInputFieldBase.java", valueInputFieldBase)
      val valueLabelFieldBase = new GwtValueLabelFieldBaseEntity(gwtContext)
      valueLabelFieldBase.name = "ValueLabelFieldBase"
      valueLabelFieldBase.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ValueLabelFieldBase.java", valueLabelFieldBase)
      val valueSetInputFieldBase = new GwtValueSetInputFieldBaseEntity(gwtContext)
      valueSetInputFieldBase.name = "ValueSetInputFieldBase"
      valueSetInputFieldBase.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ValueSetInputFieldBase.java", valueSetInputFieldBase)
      val valueSetLabelFieldBase = new GwtValueSetLabelFieldBaseEntity(gwtContext)
      valueSetLabelFieldBase.name = "ValueSetLabelFieldBase"
      valueSetLabelFieldBase.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ValueSetLabelFieldBase.java", valueSetLabelFieldBase)

      //
      val intEditor = new GwtIntEditorEntity(gwtContext)
      intEditor.name = "IntEditor"
      intEditor.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntEditor.java", intEditor)
      val stringEditor = new GwtStringEditorEntity(gwtContext)
      stringEditor.name = "StringEditor"
      stringEditor.packageName = packageName
      _gaejRealm.setEntity(pathname + "/StringEditor.java", stringEditor)
      val entityReferenceEditor = new GwtEntityReferenceEditorEntity(gwtContext)
      entityReferenceEditor.name = "EntityReferenceEditor"
      entityReferenceEditor.packageName = packageName
      _gaejRealm.setEntity(pathname + "/EntityReferenceEditor.java", entityReferenceEditor)
      val recordEditor = new GwtRecordEditorEntity(gwtContext)
      recordEditor.name = "RecordEditor"
      recordEditor.packageName = packageName
      _gaejRealm.setEntity(pathname + "/RecordEditor.java", recordEditor)

      // boolean
      val booleanInputField = new GwtBooleanInputFieldEntity(gwtContext)
      booleanInputField.name = "BooleanInputField"
      booleanInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/BooleanInputField.java", booleanInputField)
      val booleanSetInputField = new GwtBooleanSetInputFieldEntity(gwtContext)
      booleanSetInputField.name = "BooleanSetInputField"
      booleanSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/BooleanSetInputField.java", booleanSetInputField)
      val booleanLabelField = new GwtBooleanLabelFieldEntity(gwtContext)
      booleanLabelField.name = "BooleanLabelField"
      booleanLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/BooleanLabelField.java", booleanLabelField)
      val booleanSetLabelField = new GwtBooleanSetLabelFieldEntity(gwtContext)
      booleanSetLabelField.name = "BooleanSetLabelField"
      booleanSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/BooleanSetLabelField.java", booleanSetLabelField)

      // byte
      val byteInputField = new GwtByteInputFieldEntity(gwtContext)
      byteInputField.name = "ByteInputField"
      byteInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ByteInputField.java", byteInputField)
      val byteSetInputField = new GwtByteSetInputFieldEntity(gwtContext)
      byteSetInputField.name = "ByteSetInputField"
      byteSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ByteSetInputField.java", byteSetInputField)
      val byteLabelField = new GwtByteLabelFieldEntity(gwtContext)
      byteLabelField.name = "ByteLabelField"
      byteLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ByteLabelField.java", byteLabelField)
      val byteSetLabelField = new GwtByteSetLabelFieldEntity(gwtContext)
      byteSetLabelField.name = "ByteSetLabelField"
      byteSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ByteSetLabelField.java", byteSetLabelField)

      // short
      val shortInputField = new GwtShortInputFieldEntity(gwtContext)
      shortInputField.name = "ShortInputField"
      shortInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ShortInputField.java", shortInputField)
      val shortSetInputField = new GwtShortSetInputFieldEntity(gwtContext)
      shortSetInputField.name = "ShortSetInputField"
      shortSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ShortSetInputField.java", shortSetInputField)
      val shortLabelField = new GwtShortLabelFieldEntity(gwtContext)
      shortLabelField.name = "ShortLabelField"
      shortLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ShortLabelField.java", shortLabelField)
      val shortSetLabelField = new GwtShortSetLabelFieldEntity(gwtContext)
      shortSetLabelField.name = "ShortSetLabelField"
      shortSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/ShortSetLabelField.java", shortSetLabelField)

      // int
      val intInputField = new GwtIntInputFieldEntity(gwtContext)
      intInputField.name = "IntInputField"
      intInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntInputField.java", intInputField)
      val intSetInputField = new GwtIntSetInputFieldEntity(gwtContext)
      intSetInputField.name = "IntSetInputField"
      intSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntSetInputField.java", intSetInputField)
      val intLabelField = new GwtIntLabelFieldEntity(gwtContext)
      intLabelField.name = "IntLabelField"
      intLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntLabelField.java", intLabelField)
      val intSetLabelField = new GwtIntSetLabelFieldEntity(gwtContext)
      intSetLabelField.name = "IntSetLabelField"
      intSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntSetLabelField.java", intSetLabelField)

      // long
      val longInputField = new GwtLongInputFieldEntity(gwtContext)
      longInputField.name = "LongInputField"
      longInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/LongInputField.java", longInputField)
      val longSetInputField = new GwtLongSetInputFieldEntity(gwtContext)
      longSetInputField.name = "LongSetInputField"
      longSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/LongSetInputField.java", longSetInputField)
      val longLabelField = new GwtLongLabelFieldEntity(gwtContext)
      longLabelField.name = "LongLabelField"
      longLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/LongLabelField.java", longLabelField)
      val longSetLabelField = new GwtLongSetLabelFieldEntity(gwtContext)
      longSetLabelField.name = "LongSetLabelField"
      longSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/LongSetLabelField.java", longSetLabelField)

      // float
      val floatInputField = new GwtFloatInputFieldEntity(gwtContext)
      floatInputField.name = "FloatInputField"
      floatInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/FloatInputField.java", floatInputField)
      val floatSetInputField = new GwtFloatSetInputFieldEntity(gwtContext)
      floatSetInputField.name = "FloatSetInputField"
      floatSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/FloatSetInputField.java", floatSetInputField)
      val floatLabelField = new GwtFloatLabelFieldEntity(gwtContext)
      floatLabelField.name = "FloatLabelField"
      floatLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/FloatLabelField.java", floatLabelField)
      val floatSetLabelField = new GwtFloatSetLabelFieldEntity(gwtContext)
      floatSetLabelField.name = "FloatSetLabelField"
      floatSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/FloatSetLabelField.java", floatSetLabelField)

      // double
      val doubleInputField = new GwtDoubleInputFieldEntity(gwtContext)
      doubleInputField.name = "DoubleInputField"
      doubleInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DoubleInputField.java", doubleInputField)
      val doubleSetInputField = new GwtDoubleSetInputFieldEntity(gwtContext)
      doubleSetInputField.name = "DoubleSetInputField"
      doubleSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DoubleSetInputField.java", doubleSetInputField)
      val doubleLabelField = new GwtDoubleLabelFieldEntity(gwtContext)
      doubleLabelField.name = "DoubleLabelField"
      doubleLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DoubleLabelField.java", doubleLabelField)
      val doubleSetLabelField = new GwtDoubleSetLabelFieldEntity(gwtContext)
      doubleSetLabelField.name = "DoubleSetLabelField"
      doubleSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DoubleSetLabelField.java", doubleSetLabelField)

      // integer
      val integerInputField = new GwtIntegerInputFieldEntity(gwtContext)
      integerInputField.name = "IntegerInputField"
      integerInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntegerInputField.java", integerInputField)
      val integerSetInputField = new GwtIntegerSetInputFieldEntity(gwtContext)
      integerSetInputField.name = "IntegerSetInputField"
      integerSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntegerSetInputField.java", integerSetInputField)
      val integerLabelField = new GwtIntegerLabelFieldEntity(gwtContext)
      integerLabelField.name = "IntegerLabelField"
      integerLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntegerLabelField.java", integerLabelField)
      val integerSetLabelField = new GwtIntegerSetLabelFieldEntity(gwtContext)
      integerSetLabelField.name = "IntegerSetLabelField"
      integerSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/IntegerSetLabelField.java", integerSetLabelField)

      // decimal
      val decimalInputField = new GwtDecimalInputFieldEntity(gwtContext)
      decimalInputField.name = "DecimalInputField"
      decimalInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DecimalInputField.java", decimalInputField)
      val decimalSetInputField = new GwtDecimalSetInputFieldEntity(gwtContext)
      decimalSetInputField.name = "DecimalSetInputField"
      decimalSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DecimalSetInputField.java", decimalSetInputField)
      val decimalLabelField = new GwtDecimalLabelFieldEntity(gwtContext)
      decimalLabelField.name = "DecimalLabelField"
      decimalLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DecimalLabelField.java", decimalLabelField)
      val decimalSetLabelField = new GwtDecimalSetLabelFieldEntity(gwtContext)
      decimalSetLabelField.name = "DecimalSetLabelField"
      decimalSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DecimalSetLabelField.java", decimalSetLabelField)

      // string
      val stringInputField = new GwtStringInputFieldEntity(gwtContext)
      stringInputField.name = "StringInputField"
      stringInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/StringInputField.java", stringInputField)
      val stringSetInputField = new GwtStringSetInputFieldEntity(gwtContext)
      stringSetInputField.name = "StringSetInputField"
      stringSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/StringSetInputField.java", stringSetInputField)
      val stringLabelField = new GwtStringLabelFieldEntity(gwtContext)
      stringLabelField.name = "StringLabelField"
      stringLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/StringLabelField.java", stringLabelField)
      val stringSetLabelField = new GwtStringSetLabelFieldEntity(gwtContext)
      stringSetLabelField.name = "StringSetLabelField"
      stringSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/StringSetLabelField.java", stringSetLabelField)

      // text
      val textInputField = new GwtTextInputFieldEntity(gwtContext)
      textInputField.name = "TextInputField"
      textInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/TextInputField.java", textInputField)
      val textSetInputField = new GwtTextSetInputFieldEntity(gwtContext)
      textSetInputField.name = "TextSetInputField"
      textSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/TextSetInputField.java", textSetInputField)
      val textLabelField = new GwtTextLabelFieldEntity(gwtContext)
      textLabelField.name = "TextLabelField"
      textLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/TextLabelField.java", textLabelField)
      val textSetLabelField = new GwtTextSetLabelFieldEntity(gwtContext)
      textSetLabelField.name = "TextSetLabelField"
      textSetLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/TextSetLabelField.java", textSetLabelField)

      //
      val dateTimeInputField = new GwtDateTimeInputFieldEntity(gwtContext)
      dateTimeInputField.name = "DateTimeInputField"
      dateTimeInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DateTimeInputField.java", dateTimeInputField)
      val dateTimeLabelField = new GwtDateTimeLabelFieldEntity(gwtContext)
      dateTimeLabelField.name = "DateTimeLabelField"
      dateTimeLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DateTimeLabelField.java", dateTimeLabelField)
      val dateInputField = new GwtDateInputFieldEntity(gwtContext)
      dateInputField.name = "DateInputField"
      dateInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DateInputField.java", dateInputField)
      val dateLabelField = new GwtDateLabelFieldEntity(gwtContext)
      dateLabelField.name = "DateLabelField"
      dateLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/DateLabelField.java", dateLabelField)
      val timeInputField = new GwtTimeInputFieldEntity(gwtContext)
      timeInputField.name = "TimeInputField"
      timeInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/TimeInputField.java", timeInputField)
      val timeLabelField = new GwtTimeLabelFieldEntity(gwtContext)
      timeLabelField.name = "TimeLabelField"
      timeLabelField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/TimeLabelField.java", timeLabelField)
      val erefInputField = new GwtEntityReferenceInputFieldEntity(gwtContext)
      erefInputField.name = "EntityReferenceInputField"
      erefInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/EntityReferenceInputField.java", erefInputField)
      val erefSetInputField = new GwtEntityReferenceSetInputFieldEntity(gwtContext)
      erefSetInputField.name = "EntityReferenceSetInputField"
      erefSetInputField.packageName = packageName
      _gaejRealm.setEntity(pathname + "/EntityReferenceSetInputField.java", erefSetInputField)
      val erefCandidate = new GwtEntityReferenceCandidateEntity(gwtContext)
      erefCandidate.name = "EntityReferenceCandidate"
      erefCandidate.packageName = packageName
      _gaejRealm.setEntity(pathname + "/EntityReferenceCandidate.java", erefCandidate)
    }

    def make_gwt_project {
//      import scala.collection.jcl.Conversions.{convertList, convertSet}
      import scala.collection.JavaConversions._
      val mayGwt = serviceContext.getParameter("gaej.gwt")
      if (mayGwt.isEmpty) return
      val mayProject = serviceContext.getParameter("gaej.project")
      if (mayProject.isEmpty) return
      val servicesInProjects = new MultiValueMap[String, GaejServiceEntity]
      for (service <- _services) {
        val packageName = service.packageName
        servicesInProjects.add(packageName, service)
      }
      for (entry <- servicesInProjects.entrySet) {
        val packageName = entry.getKey()
        val services = entry.getValue()
        val packagePathname = "/src" + UJavaString.className2pathname(packageName)
        val moduleName = gwtContext.moduleName(packageName)
        val gwt = new GwtGwtXmlEntity(services, gwtContext)
        _gaejRealm.setEntity(packagePathname + "/" + moduleName.capitalize + ".gwt.xml", gwt)
      }
    }

    _services.foreach(make_gwt_service)
    _entities.foreach(make_gwt_entity_editor)
    make_gwt_project
  }

  private def make_atom() {
    val mayAtom = serviceContext.getParameter("gaej.atom")
    if (mayAtom.isEmpty) return

    def make_entity_repository_service(entityRepositoryService: GaejDomainServiceEntity) {
      val packageName = entityRepositoryService.packageName
      val atomPackageName = packageName + ".atom"
      val packagePathname = "/src" + UJavaString.className2pathname(packageName)
      val atomPathname = packagePathname + "/atom"
      val serviceName = _context.entityRepositoryServiceConfig.atomServiceServletSimpleClassName(entityRepositoryService)
      val service = new AtomEntityRepositoryServcieEntity(entityRepositoryService, _context)
      service.name = serviceName
      service.packageName = atomPackageName
      service.basePackageName = packageName
      _gaejRealm.setEntity(atomPathname + "/" + service.name + ".java", service)

      def make_atom_util {
        val utilPathname = atomPathname + "/ServletUtil.java"
        if (_gaejRealm.getNode(utilPathname).isDefined) {
          return
        }
        val util = new GaejServletUtilitiesEntity(_context)
        util.name = "ServletUtil"
        util.packageName = atomPackageName
        _gaejRealm.setEntity(utilPathname, util)
      }

      make_atom_util
    }

    def make_plain_service(service: GaejServiceEntity) {
    }

    def make_service(aService: GaejServiceEntity) {
      aService match {
        case entityRepositoryService: GaejDomainServiceEntity => make_entity_repository_service(entityRepositoryService)
        case _ => make_plain_service(aService)
      }
    }

    _services.foreach(make_service)
  }

  private def make_rest() {
    val mayRest = serviceContext.getParameter("gaej.rest")
    if (mayRest.isEmpty) return

    def make_entity_repository_service(entityRepositoryService: GaejDomainServiceEntity) {
      val config = _context.entityRepositoryServiceConfig
      val packageName = entityRepositoryService.packageName
      val restPackageName = packageName + ".service"
      val packagePathname = "/src" + UJavaString.className2pathname(packageName)
      val restPathname = packagePathname + "/service"
      val serviceName = config.restServiceServletSimpleClassName(entityRepositoryService)
      val service = new RestEntityRepositoryServcieEntity(entityRepositoryService, _context)
      service.name = serviceName
      service.packageName = restPackageName
      service.basePackageName = packageName
      _gaejRealm.setEntity(restPathname + "/" + service.name + ".java", service)

      def make_rest_util {
        val utilPathname = restPathname + "/ServletUtil.java"
        if (_gaejRealm.getNode(utilPathname).isDefined) {
          return
        }
        val util = new GaejServletUtilitiesEntity(_context)
        util.name = "ServletUtil"
        util.packageName = restPackageName
        _gaejRealm.setEntity(utilPathname, util)
      }

      make_rest_util // XXX
    }

    def make_plain_service(service: GaejServiceEntity) {
      val config = _context.plainServiceConfig
      val packageName = service.packageName
      val restPackageName = packageName + ".service"
      val packagePathname = "/src" + UJavaString.className2pathname(packageName)
      val restPathname = packagePathname + "/service"
      val serviceName = config.restServiceServletSimpleClassName(service)
      val restService = new RestPlainServcieEntity(service, _context)
      restService.name = serviceName
      restService.packageName = restPackageName
      restService.basePackageName = packageName
      _gaejRealm.setEntity(restPathname + "/" + restService.name + ".java", restService)
    }

    def make_service(aService: GaejServiceEntity) {
      aService match {
        case entityRepositoryService: GaejDomainServiceEntity => make_entity_repository_service(entityRepositoryService)
        case _ => make_plain_service(aService)
      }
    }

    _services.foreach(make_service)
  }
}
