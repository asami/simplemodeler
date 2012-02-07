package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull

/**
 * @since   Dec. 15, 2011
 * @version Feb.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class BeanValidation303Aspect extends JavaAspect {
  val AssertFalse = "AssertFalse"
  val AssertTrue = "AssertTrue"
  val DecimalMax = "DecimalMax"
  val DecimalMin = "DecimalMin"
  val Digits = "Digits"
  val Future = "Future"
  val Max = "Max"
  val Min = "Min"
  val NotNull = "NotNull"
  val Null = "Null"
  val Past = "Past"
  val Pattern = "Pattern"
  val Size = "Size"

  override def weaveImports() {
  }

  override def weaveAttributeSlot(attr: PAttribute, varName: String) {
    attr.constraints.get(AssertFalse) foreach { c =>
      jm_annotation(AssertFalse)
    }
    attr.constraints.get(AssertTrue) foreach { c =>
      jm_annotation(AssertTrue)
    }
    attr.constraints.get(DecimalMax) foreach { c =>
      jm_annotation(DecimalMax, c.stringLiteral)
    }
    attr.constraints.get(DecimalMin) foreach { c => 
      jm_annotation(DecimalMin, c.stringLiteral)
    }
    attr.constraints.get(Digits) foreach { c => 
      jm_annotation(Digits, c.params("integer", "fraction"))
    }
    attr.constraints.get(Future) foreach { c => 
      jm_annotation(Future)
    }
    attr.constraints.get(Max) foreach { c => 
      jm_annotation(Max, c.literal)
    }
    attr.constraints.get(Min) foreach { c => 
      jm_annotation(Min, c.literal)
    }
    attr.constraints.get(NotNull) foreach { c => 
      jm_annotation(NotNull)
    }
    attr.constraints.get(Null) foreach { c => 
      jm_annotation(Null)
    }
    attr.constraints.get(Past) foreach { c => 
      jm_annotation(Past)
    }
    attr.constraints.get(Pattern) foreach { c => 
      jm_annotation(Pattern, "regexp = \"%s\"".format(c.value.toString))
    }
    attr.constraints.get(Size) foreach { c => 
      jm_annotation(Size, c.params("min", "max"))
    }
  }
}

object BeanValidation303Aspect extends BeanValidation303Aspect
