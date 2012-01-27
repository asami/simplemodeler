package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Aug. 19, 2011
 * @version Aug. 19, 2011
 * @author  ASAMI, Tomoharu
 */
class BuilderScalaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity,
  maker: ScalaMaker
) extends ScalaClassDefinition(pContext, aspects, pobject, maker) {
  useDocument = false
  isStatic = true
  customName = Some("Builder")
  isCustomVariableImplementation = true

  override protected def constructors_copy_constructor {
    sm_public_constructor("%s(%s src)", name, pobject.name) {
      for (a <- attributeDefinitions) {
        sm_assign_this(a.varName, "src." + a.varName)
      }
    }
  }

  override protected def constructors_plain_constructor {
    constructors_plain_constructor_for_document
  }

  override protected def attribute(attr: PAttribute) = {
    new BuilderScalaClassAttributeDefinition(pContext, aspects, attr, this, sm_maker)
  }

  override protected def service_methods {
    sm_public_method("%s with_json(JSONObject json)", name) {
      // XXX
      sm_return_this;
    }
    sm_public_method("%s build()", pobject.name) {
      sm_p("return new %s(", pobject.name)
      sm_indent_up
      if (!attributeDefinitions.isEmpty) {
        sm_pln
        sm_p(attributeDefinitions.head.varName)
      }
      for (a <- attributeDefinitions.tail) {
        sm_pln(",")
        sm_p(a.varName)
      }
      sm_pln(");")
      sm_indent_down
    }
  }
}
