package org.simplemodeling.SimpleModeler.entities

/*
 * @since   May. 15, 2011
 *  version Sep.  1, 2011
 *  version Feb. 10, 2012
 *  version Oct. 30, 2012
 * @version Nov.  8, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaMakerHolder {
  private var _maker: JavaMaker = null

  protected def jm_to_text: String = {
    _maker.toString
  }

  protected def jm_open(m: JavaMaker) {
    _maker = m;
  }

  protected def jm_open(aspects: Seq[JavaAspect]) {
    jm_open(new JavaMaker, aspects)
  }

  protected def jm_open(m: JavaMaker, aspects: Seq[JavaAspect]) {
    require (m != null)
    _maker = m;
    aspects.foreach(_.open(_maker))
  }

  protected def jm_maker = {
    assert (_maker != null)
    _maker
  }

  //
  protected def jm_p(s: String, params: AnyRef*): String = {
    _maker.print(s, params: _*)
    s
  }

  protected def jm_pln(s: String, params: AnyRef*) {
    _maker.println(s, params: _*)
  }

  protected def jm_pln() {
    _maker.println()
  }

  protected final def jm_package(name: String) {
    println("JavaMakerHolder[%s] = %s / %s".format(this, name, _maker))
    require (_maker != null, "_maker should initialized")
    _maker.declarePackage(name)
  }

  protected final def jm_end_package_section() {
    _maker.endPackageSection()
  }

  protected final def jm_import(aName: String) {
    _maker.declareImport(aName)
  }

  protected final def jm_static_import(aName: String) {
    _maker.declareStaticImport(aName)
  }

  protected final def jm_end_import_section() {
    _maker.endImportSection()
  }

  protected final def jm_annotation(ano: String) {
    jm_pln("@%s".format(ano))
  }

  protected final def jm_annotation(ano: String, params: String) {
    jm_pln("@%s(%s)".format(ano, params))
  }

  protected final def jm_annotation_string(ano: String, params: String) {
    jm_pln("@%s(\"%s\")".format(ano, params))
  }

  protected final def jm_private_instance_variable(attr: PAttribute, typename: String = null, varname: String = null) {
    val tname = if (typename == null) attr.typeName else typename;
    val vname = if (varname == null) attr.name else varname;
    if (attr.isHasMany) {
      jm_private_instance_variable_list(tname, vname);
    } else {
      jm_private_instance_variable_single(tname, vname);
    }
  }

  protected final def jm_private_instance_variable_single(typename: String, varname: String) {
    _maker.privateInstanceVariableSingle(typename, varname);
  }

  protected final def jm_private_instance_variable_list(typename: String, varname: String) {
    _maker.privateInstanceVariableList(typename, varname);
  }

  protected final def jm_protected_instance_variable(attr: PAttribute, typename: String = null, varname: String = null) {
    val tname = if (typename == null) attr.typeName else typename;
    val vname = if (varname == null) attr.name else varname;
    if (attr.isHasMany) {
      jm_protected_instance_variable_list(tname, vname);
    } else {
      jm_protected_instance_variable_single(tname, vname);
    }
  }

  protected final def jm_protected_instance_variable_single(typename: String, varname: String) {
    _maker.protectedInstanceVariableSingle(typename, varname);
  }

  protected final def jm_protected_instance_variable_list(typename: String, varname: String) {
    _maker.protectedInstanceVariableList(typename, varname);
  }

  protected final def jm_public_final_instance_variable(attr: PAttribute, typename: String = null, varname: String = null) {
    val tname = if (typename == null) attr.typeName else typename;
    val vname = if (varname == null) attr.name else varname;
    if (attr.isHasMany) {
      jm_public_final_instance_variable_list(tname, vname);
    } else {
      jm_public_final_instance_variable_single(tname, vname);
    }
  }

  protected final def jm_public_final_instance_variable_single(typename: String, varname: String) {
    _maker.publicFinalInstanceVariableSingle(typename, varname);
  }

  protected final def jm_public_final_instance_variable_list(typename: String, varname: String) {
    _maker.publicFinalInstanceVariableList(typename, varname);
  }

  protected final def jm_public_instance_variable(attr: PAttribute, typename: String = null, varname: String = null) {
    val tname = if (typename == null) attr.typeName else typename;
    val vname = if (varname == null) attr.name else varname;
    if (attr.isHasMany) {
      jm_public_instance_variable_list(tname, vname);
    } else {
      jm_public_instance_variable_single(tname, vname);
    }
  }

  protected final def jm_public_instance_variable_single(typename: String, varname: String) {
    _maker.publicInstanceVariableSingle(typename, varname);
  }

  protected final def jm_public_instance_variable_list(typename: String, varname: String) {
    _maker.publicInstanceVariableList(typename, varname);
  }

  protected final def jm_private_transient_instance_variable(attr: PAttribute, typename: String = null, varname: String = null) {
    val tname = if (typename == null) attr.typeName else typename;
    val vname = if (varname == null) attr.name else varname;
    if (attr.isHasMany) {
      jm_private_transient_instance_variable_list(tname, vname);
    } else {
      jm_private_transient_instance_variable_single(tname, vname);
    }
  }

  protected final def jm_private_transient_instance_variable_single(typename: String, varname: String) {
    _maker.privateTransientInstanceVariableSingle(typename, varname);
  }

  protected final def jm_private_transient_instance_variable_list(typename: String, varname: String) {
    _maker.privateTransientInstanceVariableList(typename, varname);
  }

  protected final def jm_protected_transient_instance_variable(attr: PAttribute, typename: String = null, varname: String = null) {
    val tname = if (typename == null) attr.typeName else typename;
    val vname = if (varname == null) attr.name else varname;
    if (attr.isHasMany) {
      jm_protected_transient_instance_variable_list(tname, vname);
    } else {
      jm_protected_transient_instance_variable_single(tname, vname);
    }
  }

  protected final def jm_protected_transient_instance_variable_single(typename: String, varname: String) {
    _maker.protectedTransientInstanceVariableSingle(typename, varname);
  }

  protected final def jm_protected_transient_instance_variable_list(typename: String, varname: String) {
    _maker.protectedTransientInstanceVariableList(typename, varname);
  }

  protected final def jm_public_static_final_String_literal(varname: String, form: String, params: AnyRef*) {
    _maker.pln("""public static final String %s = "%s";""", varname, _format(form, params))
  }

  protected final def jm_public_static_final_int(varname: String, form: String, params: AnyRef*) {
    _maker.pln("""public static final int %s = %s;""", varname, _format(form, params))
  }

  protected final def jm_public_static_final(typename: String, varname: String, form: String, params: AnyRef*) {
    _maker.pln("""public static final %s %s = %s;""", typename, varname, _format(form, params))
  }

  protected final def jm_private_static_final_String_literal(varname: String, form: String, params: AnyRef*) {
    _maker.pln("""private static final String %s = "%s";""", varname, _format(form, params))
  }

  protected final def jm_private_static_final_int(varname: String, value: Int) {
    _maker.pln("""private static final int %s = %s;""", varname, value.toString)
  }

  protected final def jm_private_static_final_int(varname: String, form: String, params: AnyRef*) {
    _maker.pln("""private static final int %s = %s;""", varname, _format(form, params))
  }

  protected final def jm_private_static_final(typename: String, varname: String, form: String, params: AnyRef*) {
    _maker.pln("""private static final %s %s = %s;""", typename, varname, _format(form, params))
  }

  // method
  protected final def jm_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.method(signature, params: _*)(body)
  }

  protected final def jm_public_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.publicMethod(signature, params: _*)(body)
  }

  protected final def jm_public_abstract_method(signature: String, params: AnyRef*) {
    _maker.publicAbstractMethod(signature, params: _*)
  }

  protected final def jm_override_public_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.pln
    _maker.p("@Override") // XXX
    _maker.publicMethod(signature, params: _*)(body)
  }

  protected final def jm_public_void_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.publicVoidMethod(signature, params: _*)(body)
  }

  protected final def jm_public_void_abstract_method(signature: String, params: AnyRef*) {
    _maker.publicVoidAbstractMethod(signature, params: _*)
  }

  protected final def jm_public_get_method(typeName: String, attrName: String, expr: AnyRef*) {
    _maker.publicGetMethod(typeName, attrName, expr: _*)
  }

  protected final def jm_public_get_abstract_method(typeName: String, attrName: String, expr: AnyRef*) {
    _maker.publicGetAbstractMethod(typeName, attrName)
  }

  protected final def jm_public_get_or_null_method(typeName: String, attrName: String, varName: String, expr: String, params: AnyRef*) {
    _maker.publicGetOrNullMethod(typeName, attrName, varName, expr, params: _*)
  }

  protected final def jm_public_get_or_null_abstract_method(typeName: String, attrName: String) {
    _maker.publicGetOrNullAbstractMethod(typeName, attrName)
  }

  protected final def jm_public_is_method(attrName: String, expr: AnyRef*) {
    _maker.publicIsMethod(attrName, expr: _*)
  }

  protected final def jm_public_is_abstract_method(attrName: String, expr: AnyRef*) {
    _maker.publicIsAbstractMethod(attrName)
  }

  protected final def jm_public_set_method(attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicSetMethod(attrName, typeName, paramName, varName, expr)
  }

  protected final def jm_public_set_abstract_method(attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicSetAbstractMethod(attrName, typeName, paramName)
  }

  protected final def jm_public_set_or_null_method(attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicSetOrNullMethod(attrName, typeName, paramName, varName, expr)
  }

  protected final def jm_public_set_or_null_abstract_method(attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicSetOrNullAbstractMethod(attrName, typeName, paramName)
  }

  protected final def jm_public_with_method(className: String, attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicWithMethod(className, attrName, typeName, paramName, varName, expr)
  }

  protected final def jm_public_with_or_null_method(className: String, attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicWithOrNullMethod(className, attrName, typeName, paramName, varName, expr)
  }

  protected final def jm_public_with_or_null_abstract_method(className: String, attrName: String, typeName: String,
      paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    _maker.publicWithOrNullAbstractMethod(className, attrName, typeName, paramName)
  }

  protected final def jm_override_protected_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.pln
    _maker.p("@Override") // XXX
    _maker.protectedMethod(signature, params: _*)(body)
  }

  protected final def jm_protected_final_void_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.protectedFinalVoidMethod(signature, params: _*)(body)
  }

  protected final def jm_private_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.privateMethod(signature, params: _*)(body)
  }

  protected final def jm_public_static_method(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.publicStaticMethod(signature, params: _*)(body)
  }

  protected final def jm_public_set_list_method(attrName: String, elemTypeName: String, paramName: String, varName: String) {
    val pname = if (paramName != null) paramName else attrName
    val vname = if (varName != null) varName else attrName
    jm_public_void_method("set%s(List<%s> %s)", attrName.capitalize, elemTypeName, pname) {
      jm_assign_this(varName, "new ArrayList<%s>(%s)", elemTypeName, pname)
    }
  }

  protected final def jm_public_set_list_abstract_method(attrName: String, elemTypeName: String, paramName: String, varName: String) {
    val pname = if (paramName != null) paramName else attrName
    val vname = if (varName != null) varName else attrName
    jm_public_void_abstract_method("set%s(List<%s> %s)", attrName.capitalize, elemTypeName, pname)
  }

  protected final def jm_public_add_list_element_method(attrName: String, elemTypeName: String, paramName: String, varName: String) {
    val pname = if (paramName != null) paramName else attrName
    val vname = if (varName != null) varName else attrName
    jm_public_void_method("add%s(%s %s)", attrName, elemTypeName, pname) {
      jm_if("this." + vname + " == null") {
        jm_assign_this(vname, "new ArrayList<%s>()", elemTypeName) 
      }
      jm_pln("this.%s.add(%s);", vname, pname);
    }
  }

  protected final def jm_public_add_list_element_abstract_method(attrName: String, elemTypeName: String, paramName: String, varName: String) {
    val pname = if (paramName != null) paramName else attrName
    val vname = if (varName != null) varName else attrName
    jm_public_void_abstract_method("add%s(%s %s)", attrName, elemTypeName, pname)
  }

  protected final def jm_public_get_list_method(elemTypeName: String, attrName: String, varName: String) {
    val vname = if (varName != null) varName else attrName;  
    jm_public_method("%s get%s()", elemTypeName, attrName.capitalize) {
      jm_if_else(vname + " != null") {
        jm_pln("return Collections.unmodifiableList(%s);", vname)
      }
      jm_else {
        jm_pln("return Collections.emptyList();")
      }
    }
  }

  protected final def jm_public_get_list_abstract_method(elemTypeName: String, attrName: String, varName: String) {
    val vname = if (varName != null) varName else attrName;  
    jm_public_abstract_method("%s get%s()", elemTypeName, attrName.capitalize)
  }

  protected final def jm_public_get_list_method_prologue(elemTypeName: String, attrName: String, varName: String)(body: => Unit) {
    val vname = if (varName != null) varName else attrName;  
    jm_public_method("%s get%s()", elemTypeName, attrName.capitalize) {
      body
      jm_if_else(vname + " != null") {
        jm_pln("return Collections.unmodifiableList(%s);", vname)
      }
      jm_else {
        jm_pln("return Collections.emptyList();")
      }
    }
  }

  protected final def jm_public_get_list_abstract_method_prologue(elemTypeName: String, attrName: String, varName: String) {
    val vname = if (varName != null) varName else attrName;  
    jm_public_abstract_method("%s get%s()", elemTypeName, attrName.capitalize)
  }

  protected final def jm_interface_method(signature: String, params: AnyRef*) {
    jm_p(signature, params: _*)
    jm_pln(";")
  }

  protected final def jm_interface_void_method(signature: String, params: AnyRef*) {
    jm_p("void ")
    jm_p(signature, params: _*)
    jm_pln(";")
  }

  // constructor
  protected final def jm_public_constructor(signature: String, params: AnyRef*)(body: => Unit) {
    _maker.publicConstructor(signature, params: _*)(body)
  }
  
  // DI
  protected final def jm_inject {
    _maker.makeAnnotation("@Inject")
  }

  // static
  protected final def jm_static(body: => Unit) {
    _maker.staticBlock(body)
  }

  // if
  protected final def jm_if(condition: String, params: AnyRef*)(body: => Unit) {
    _maker.makeIf(condition, params: _*)(body)
  }

  protected final def jm_if_else(condition: String, params: AnyRef*)(body: => Unit) {
    _maker.makeIfElse(condition, params: _*)(body)
  }

  protected final def jm_else_if(condition: String, params: AnyRef*)(body: => Unit) {
    _maker.makeElseIf(condition, params: _*)(body)
  }

  protected final def jm_else_if_else(condition: String, params: AnyRef*)(body: => Unit) {
    _maker.makeElseIfElse(condition, params: _*)(body)
  }

  protected final def jm_else(body: => Unit) {
    _maker.makeElse(body)
  }

  // if variants
  protected final def jm_if_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeIfNull(expr, params: _*)(body)
  }

  protected final def jm_if_not_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeIfNotNull(expr, params: _*)(body)
  }

  protected final def jm_if_return(condition: String, cparams: AnyRef*) {
    _maker.makeIfReturn(condition, cparams: _*)
  }

  protected final def jm_if_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeIfReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_if_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeIfNullReturn(condition, cparams: _*)
  }

  protected final def jm_if_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeIfNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_if_not_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeIfNotNullReturn(condition, cparams: _*)
  }

  protected final def jm_if_not_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeIfNotNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  // XXX
  protected final def jm_if_not_equals_expr(lhs: String, lparams: AnyRef*)(rhs: String, rparams: AnyRef*)(body: => Unit) {
    _maker.makeIf("!%s.equals(%s)", _format(lhs, lparams), _format(rhs, rparams)) {
      body
    }
  }

  // if else variants
  protected final def jm_if_else_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeIfElseNull(expr, params: _*)(body)
  }

  protected final def jm_if_else_not_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeIfElseNotNull(expr, params: _*)(body)
  }

  protected final def jm_if_else_return(condition: String, cparams: AnyRef*) {
    _maker.makeIfElseReturn(condition, cparams: _*)
  }

  protected final def jm_if_else_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeIfElseReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_if_else_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeIfElseNullReturn(condition, cparams: _*)
  }

  protected final def jm_if_else_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeIfElseNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_if_else_not_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeIfElseNotNullReturn(condition, cparams: _*)
  }

  protected final def jm_if_else_not_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeIfElseNotNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }
  
  // else if variants
  protected final def jm_else_if_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeElseIfNull(expr, params: _*)(body)
  }

  protected final def jm_else_if_not_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeElseIfNotNull(expr, params: _*)(body)
  }

  protected final def jm_else_if_return(condition: String, cparams: AnyRef*) {
    _maker.makeElseIfReturn(condition, cparams: _*)
  }

  protected final def jm_else_if_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeElseIfReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_else_if_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeElseIfNullReturn(condition, cparams: _*)
  }

  protected final def jm_else_if_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeElseIfNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_else_if_not_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeElseIfNotNullReturn(condition, cparams: _*)
  }

  protected final def jm_else_if_not_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeElseIfNotNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  // else if else variants
  protected final def jm_else_if_else_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeElseIfElseNull(expr, params: _*)(body)
  }

  protected final def jm_else_if_else_not_null(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeElseIfElseNotNull(expr, params: _*)(body)
  }

  protected final def jm_else_if_else_return(condition: String, cparams: AnyRef*) {
    _maker.makeElseIfElseReturn(condition, cparams: _*)
  }

  protected final def jm_else_if_else_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeElseIfElseReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_else_if_else_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeElseIfElseNullReturn(condition, cparams: _*)
  }

  protected final def jm_else_if_else_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeElseIfElseNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_else_if_else_not_null_return(condition: String, cparams: AnyRef*) {
    _maker.makeElseIfElseNotNullReturn(condition, cparams: _*)
  }

  protected final def jm_else_if_else_not_null_return_expr(condition: String, cparams: AnyRef*)(expr: String, eparams: AnyRef*) {
    _maker.makeElseIfElseNotNullReturnExpr(condition, cparams: _*)(expr, eparams: _*)
  }

  protected final def jm_get_return_expr_or_null(typename: String, value: String, vparams: AnyRef*)(expr: String) {
    jm_var(typename, "elem", value, vparams: _*)
    jm_return_expr_or_null("elem", expr)
  }

  // for
  protected final def jm_for(typeName: String, varName: String, generator: String)(body: => Unit) {
    _maker.makeFor(typeName, varName, generator)(body)
  }

  protected final def jm_for(expr: String)(body: => Unit) {
    _maker.makeFor(expr)(body)
  }

  protected final def jm_while(expr: String, params: AnyRef*)(body: => Unit) {
    _maker.makeWhile(expr, params: _*)(body)
  }

  protected final def jm_switch(cond: String, params: AnyRef*)(body: => Unit) {
    _maker.makeSwitch(cond, params: _*)(body)
  }

  protected final def jm_case(pattern: String)(body: => Unit) {
    _maker.makeCase(pattern)(body)
  }

  protected final def jm_case_return(pattern: String, form: String, params: AnyRef*) {
    _maker.makeCaseReturn(pattern, form, params: _*)
  }

  protected final def jm_case_default(body: => Unit) {
    _maker.makeCaseDefault(body)
  }

  // try
  protected final def jm_try(body: => Unit) {
    _maker.makeTry(body)
  }

  protected final def jm_catch(condition: String)(body: => Unit) {
    _maker.makeCatch(condition)(body)
  }

  protected final def jm_catch_end(condition: String)(body: => Unit) {
    _maker.makeCatchEnd(condition)(body)
  }

  protected final def jm_catch_nop(condition: String) {
    _maker.makeCatchNop(condition)
  }

  protected final def jm_finally(body: => Unit) {
    _maker.makeFinally(body)
  }

  protected final def jm_return(expr: String, params: AnyRef*) {
    _maker.makeReturn(_format(expr, params))
  }

  protected final def jm_return() {
    _maker.makeReturn()
  }

  protected final def jm_return_null() {
    _maker.makeReturnNull()
  }

  protected final def jm_return_true() {
    _maker.makeReturnTrue()
  }

  protected final def jm_return_false() {
    _maker.makeReturnFalse()
  }

  protected final def jm_return_this() {
    _maker.makeReturnThis()
  }

  protected final def jm_return_var_or_null(
    varname: String
  ) {
    _maker.makeReturnVarOrNull(varname)
  }

  protected final def jm_return_expr_or_null(
    varname: String, expr: String
  ) {
    _maker.makeReturnExprOrNull(varname, expr)
  }

  // assign
  protected final def jm_assign(varname: String, expr: String) {
    _maker.assign(varname, expr)
  }

  protected final def jm_assign(varname: String, expr: String, args: AnyRef*) {
    _maker.assign(varname, expr, args: _*)
  }

  protected final def jm_assign_null(varname: String) {
    _maker.assignNull(varname)
  }

  protected final def jm_assign_true(varname: String) {
    _maker.assignTrue(varname)
  }

  protected final def jm_assign_new(varname: String, classname: String) {
    _maker.assignNew(varname, classname)
  }

  protected final def jm_assign_new(varname: String, classname: String, signature: String, params: AnyRef*) {
    _maker.assignNew(varname, classname, _format(signature, params))
  }

  protected final def jm_assign_new_Container(varname: String, classname: String, containee: String) {
    _maker.assignNewContainer(varname, classname, containee)
  }

  protected final def jm_assign_new_Container(varname: String, classname: String, containee: String, signature: String, params: AnyRef*) {
    _maker.assignNewContainer(varname, classname, containee, signature, params)
  }

  protected final def jm_assign_new_ArrayList(varname: String, classname: String) {
    _maker.assignNewArrayList(varname, classname)
  }

  protected final def jm_assign_new_ArrayList(varname: String, classname: String, signature: String, params: AnyRef*) {
    _maker.assignNewArrayList(varname, classname, signature, params: _*)
  }

  // assign this
  protected final def jm_assign_this(varname: String, expr: String, args: AnyRef*) {
    _maker.assignThis(varname, expr, args: _*)
  }

  protected final def jm_assign_this_null(varname: String) {
    _maker.assignThisNull(varname)
  }

  protected final def jm_assign_this_true(varname: String) {
    _maker.assignThisTrue(varname)
  }

  protected final def jm_assign_this_new(varname: String, classname: String) {
    _maker.assignThisNew(varname, classname)
  }

  protected final def jm_assign_this_new(varname: String, classname: String, signature: String, params: AnyRef*) {
    _maker.assignThisNew(varname, classname, _format(signature, params))
  }

  protected final def jm_assign_this_new_Container(varname: String, classname: String, containee: String) {
    _maker.assignThisNewContainer(varname, classname, containee)
  }

  protected final def jm_assign_this_new_Container(varname: String, classname: String, containee: String, signature: String, params: AnyRef*) {
    _maker.assignThisNewContainer(varname, classname, containee, signature, params)
  }

  protected final def jm_assign_this_new_ArrayList(varname: String, classname: String) {
    _maker.assignThisNewArrayList(varname, classname)
  }

  protected final def jm_assign_this_new_ArrayList(varname: String, classname: String, signature: String, params: AnyRef*) {
    _maker.assignThisNewArrayList(varname, classname, signature, params)
  }

  protected final def jm_assign_this_null_or_object(attrname: String, nullvalue: String = "null")
                                     (rawtype: String, rawform: String, rawparams: AnyRef*)
                                     (newform: String) {
    val varname = attrname + "value"
    jm_var(rawtype, varname, rawform, rawparams: _*)
    jm_if_else("%s == %s", varname, nullvalue) {
      jm_assign_this_null(attrname)
    }
    jm_else {
      jm_assign_this(attrname, newform, varname)
    }
  }

  // var
  protected final def jm_var(typename: String, varname: String, expr: String, params: AnyRef*) {
    _maker.makeVar(typename, varname, expr, params: _*)
  }

  protected final def jm_var_String(varname: String, expr: String, params: AnyRef*) {
    _maker.makeVarString(varname, expr, params: _*)
  }

  protected final def jm_var_null(varname: String, typename: String) {
    _maker.makeVarNull(typename, varname)
  }

  protected final def jm_var_new(typename: String, varname: String) {
    _maker.makeVar(typename, varname)
  }

  protected final def jm_var_List_new_ArrayList(classname: String, varname: String) {
    _maker.varListNewArrayList(classname, varname)
  }

  protected final def jm_var_new_StringBuilder() {
    _maker.makeStringBuilderVar()
  }

  protected final def jm_return_StringBuilder() {
    _maker.makeStringBuilderReturn()
  }

  protected final def jm_append_String(s: String, params: AnyRef*) {
    _maker.makeAppendString(s, params: _*)
  }

  protected final def jm_append_var_String(varname: String, s: String, params: AnyRef*) {
    _maker.makeAppendVarString(varname, s, params: _*)
  }

  protected final def jm_append_expr(s: String, params: AnyRef*) {
    _maker.makeAppendExpr(s, params: _*)
  }

  protected final def jm_var_append_var_expr(varname: String, s: String, params: AnyRef*) {
    _maker.makeAppendVarExpr(varname, s, params: _*)
  }

  protected final def jm_UnsupportedOperationException() {
    _maker.pln("throw new UnsupportedOperationException();")
  }

  protected final def jm_code(template: CharSequence, replaces: Map[String, String] = Map.empty) {
    _maker.code(template, replaces)
  }

  //
  protected final def jm_indent_up() {
    _maker.indentUp
  }

  protected final def jm_indent_down() {
    _maker.indentDown
  }

  private def _format(format: String, params: Seq[AnyRef]) = {
    if (params.isEmpty) format
    else format.format(params: _*)
  }
}
