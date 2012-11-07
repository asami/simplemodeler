package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable
import java.util.UUID
import org.goldenport.value._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.content.GContent
import org.goldenport.sdoc._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._
import org.simplemodeling.dsl.datatype.business._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.business._
import org.simplemodeling.dsl.requirement._
import org.simplemodeling.dsl.system._
import org.simplemodeling.dsl.flow._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entity.system._
import org.simplemodeling.SimpleModeler.entity.flow._
import com.asamioffice.goldenport.text.UJavaString

/*
 * @since   Nov.  7, 2012
 * @version Nov.  7, 2012
 * @author  ASAMI, Tomoharu
 */
trait SimpleModelEntityHelper {
  self: SimpleModelEntity =>

  final def makeDomainDocumentPartName(anEntity: SMEntity): String = {
    require (anEntity != SMNullEntity)
    "DDI" + anEntity.term
  }

  final def makeImportAttributeName(anEntity: SMEntity, anAttr: SMAttribute): String = {
    anEntity.term + "_" + anAttr.dslAttribute.name
  }

  final def makeImportAssociationName(anEntity: SMEntity, anAssoc: SMAssociation): String = {
    anEntity.term + "_" + anAssoc.dslAssociation.name
  }

  final def makeDocumentPartAttributeName(anPart: SMDocumentPart): String = {
    require (anPart != SMNullDocumentPart)
    anPart.term
  }

  //
  final def name_en(modelElement: SMElement) = {
    pickup_name(modelElement.name_en, modelElement.term_en, modelElement.name)
  }

  final def term_en(modelElement: SMElement) = {
    pickup_name(modelElement.term_en, modelElement.term, modelElement.name_en, modelElement.name)
  }

  final def entityDocumentName(anObject: SMObject): String = {
    classNameBase(anObject) + "Doc"
  }

  final def classNameBase(anObject: SMObject) = {
    pascal_case_name(enTerm(anObject))
  }

  final def enTerm(modelElement: SMElement) = {
    pickup_name(modelElement.term_en, modelElement.term, modelElement.name_en, modelElement.name)
  }

  final def pickup_name(names: String*): String = {
    for (name <- names) {
      if (!(name == null || "".equals(name))) {
        return name
      }
    }
    throw new IllegalArgumentException("no name")
  }

  final def underscore_name(name: String): String = {
    val buf = new StringBuilder
    for (c <- name) {
      c match {
        case '-' => buf.append('_');
        case ':' => buf.append('_');
        case '.' => buf.append('_');
        case ' ' => buf.append('_');
        case _ => buf.append(c);
      }
    }
    buf.toString
  }

  /**
   * camel case (capitalize nuetral)
   */
  final def camel_case_name(name: String): String = {
    val buf = new StringBuilder
    var afterSpace = false
    for (c <- name) {
      c match {
        case '-' => buf.append('_');afterSpace = false
        case ':' => buf.append('_');afterSpace = false
        case '.' => buf.append('_');afterSpace = false
        case ' ' => afterSpace = true
        case _ if afterSpace => buf.append(c.toUpperCase);afterSpace = false
        case _ => buf.append(c);afterSpace = false
      }
    }
    buf.toString
  }

  /**
   * capitalize camel case
   */
  final def pascal_case_name(name: String): String = {
    val buf = new StringBuilder
    var firstCharacter = true
    var afterSpace = false
    for (c <- name) {
      c match {
        case '-' => buf.append('_');afterSpace = false
        case ':' => buf.append('_');afterSpace = false
        case '.' => buf.append('_');afterSpace = false
        case ' ' => afterSpace = true
        case _ if firstCharacter => buf.append(c.toUpperCase);firstCharacter = false
        case _ if afterSpace => buf.append(c.toUpperCase);afterSpace = false
        case _ => buf.append(c);afterSpace = false
      }
    }
    buf.toString
  }
}

