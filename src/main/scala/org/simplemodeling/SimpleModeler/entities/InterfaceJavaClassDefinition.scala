package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  6, 2012
 * @version Nov.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class InterfaceJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  isData = false
  useDocument = false

  override protected def attribute(attr: PAttribute) = {
    new InterfaceJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }

  override protected def class_open_body {
    jm_p("public interface ")
    jm_p(name)
    for (base <- customBaseName orElse baseObject.map(_.name)) {
      jm_p(" extends ")
      jm_p(base)
    }
    jm_pln(" {")
    jm_indent_up
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
  }

  override protected def constructors_doc_constructor {
  }

  override protected def to_methods_feed {
  }

  override protected def to_methods_entry {
  }
}
