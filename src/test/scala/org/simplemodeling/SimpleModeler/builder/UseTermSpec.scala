package org.simplemodeling.SimpleModeler.builder

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{WordSpec, GivenWhenThen}
import org.scalatest.matchers.ShouldMatchers
import org.simplemodeling.SimpleModeler._
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Sep. 17, 2012
 * @version Oct. 10, 2012
 * @author  ASAMI, Tomoharu
 */
@RunWith(classOf[JUnitRunner])
class UseTermSpec extends WordSpec with ShouldMatchers with GivenWhenThen with UseTerm {
  "UseTerm" should {
    "get_type_name_explicitly" that {
      "no type" in {
        get_type_name_explicitly("abc") should be (None)
      }
      "explicitly type" in {
        get_type_name_explicitly("abc:xyz") should be (Some("xyz"))
      }
      "explicitly type with zeroMore" in {
        get_type_name_explicitly("abc:xyz*") should be (Some("xyz"))
      }
    }
    "get_multiplicity_by_term" that {
      "implicit one" in {
        get_multiplicity_by_term("abc") should be (GROne)
      }
      "type and implicit one" in {
        get_multiplicity_by_term("abc:xyz") should be (GROne)
      }
      "zero-one" in {
        get_multiplicity_by_term("abc:xyz?") should be (GRZeroOne)
      }
      "one-more" in {
        get_multiplicity_by_term("abc:xyz+") should be (GROneMore)
      }
      "zero-more" in {
        get_multiplicity_by_term("abc:xyz*") should be (GRZeroMore)
      }
    }
  }
}
