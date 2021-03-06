package org.simplemodeling.SimpleModeler.entities;

import com.asamioffice.goldenport.text.JavaTextMaker
import scala.collection.mutable.ArrayBuffer

/*
 * @since   Aug. 19, 2011
 * @version Aug. 11, 2014
 * @author  ASAMI, Tomoharu
 */
class ScalaMaker extends JavaTextMaker {
  private var _use_package = false
  private var _imports = new ArrayBuffer[String]
  methodAutoIndent = false

  def p(s: String, params: Any*) {
    this.print(format_params(s, params));
  }

  def pln(s: String, params: Any*) {
    this.println(format_params(s, params));
  }

  def pln() {
    this.println();
  }

  def declarePackage(name: String) {
      p("package ")
      p(name)
      pln()
      _use_package = true
  }

  def endPackageSection() {
    if (_use_package) {
      pln
    }
  }

  def declareImport(name: String) {
    _imports += name
  }
  
  def endImportSection() {
    val imports = _unify_imports
    for (name <- imports) {
      p("import ")
      p(name)
      pln()
    }
    if (!imports.isEmpty) {
      pln
    }
  }

  private def _unify_imports: List[String] = {
    val (astas, plains) = _imports.toList.partition(_.endsWith("._"))
    val astaspkgs = astas.map(s => s.dropRight("._".length))
    val independents = plains.filter(s => {
      val index = s.lastIndexOf(".")
      if (index  == -1) true
      else {
        val pkg = s.substring(0, index)
        !astaspkgs.exists(_ == pkg)
      }
    })
    (astas ::: independents).distinct.sorted
  }

  def publicFinalInstanceVariableSingle(typename: String, varname: String) {
    pln("public final %s %s;".format(typename, varname))    
  }

  def publicFinalInstanceVariableList(typename: String, varname: String) {
    pln("public final List<%s> %s;".format(typename, varname))
  }

  def publicInstanceVariableSingle(typename: String, varname: String) {
    pln("public %s %s;".format(typename, varname))    
  }

  def publicInstanceVariableList(typename: String, varname: String) {
    pln("public List<%s> %s;".format(typename, varname))
  }

  def privateInstanceVariableSingle(typename: String, varname: String) {
    pln("private %s %s;".format(typename, varname))    
  }

  def privateInstanceVariableList(typename: String, varname: String) {
    pln("private List<%s> %s = new ArrayList<%s>();".format(typename, varname, typename))
  }

  def privateTransientInstanceVariableSingle(typename: String, varname: String) {
    pln("private transient %s %s;".format(typename, varname))    
  }

  def privateTransientInstanceVariableList(typename: String, varname: String) {
    pln("private transient List<%s> %s = new ArrayList<%s>();".format(typename, varname, typename))
  }

  override def method(signature: String, params: AnyRef*)(body: => Unit) {
    super.method("def " + format_params(signature, params) + " = ")(body)
  }

  def publicMethod(signature: String, params: AnyRef*)(body: => Unit) {
    method(format_params(signature, params))(body)
  }

  def overridePublicMethod(signature: String, params: AnyRef*)(body: => Unit) {
    super.method("override def " + format_params(signature, params) + " = ")(body)
  }

/*
  private def _format(signature: String, params: Seq[AnyRef]) = {
    if (params.isEmpty) signature
    else {
      require (!params.exists(_.isInstanceOf[Seq[_]]), "ScalaMaker#_format: invalid sequence parameter")
      signature.format(params: _*)
    }
  }
*/

  def publicVoidMethod(signature: String, params: AnyRef*)(body: => Unit) {
    method("public void " + format_params(signature, params)) {
      body
    }
  }

  def publicGetMethod(typeName: String, attrName: String, expr: AnyRef*) {
    method("public %s get%s()", typeName, attrName.capitalize) {
      makeReturn(attr_expr(attrName, expr))
    }
  }
/*
  def publicSetMethod(typeName: String, attrName: String, varName: String = null, paramName: String = null) {
    val vname = if (varName != null) varName else attrName
    val pname = if (paramName != null) paramName else attrName
    method("public void set%s(%s %s)", attrName.capitalize, typeName, pname) {
      assignThis(vname, pname)
    }
  }

  def publicWithMethod(className: String, typeName: String, attrName: String, varName: String = null, paramName: String = null) {
    val vname = if (varName != null) varName else attrName
    val pname = if (paramName != null) paramName else attrName
    method("public %s with%s(%s %s)", className, attrName.capitalize, typeName, pname) {
      assignThis(vname, pname)
      makeReturnThis
    }
  }
*/
  /**
   * publicGetOrNullMethod("int", "price", "goodPrice", "intValue()")
   */
  def publicGetOrNullMethod(typeName: String, attrName: String,
      varName: String, expr: String, params: AnyRef*) {
    val vname = _var_name(attrName, varName);
    method("public %s get%s()", typeName, attrName.capitalize) {
      makeIf("%s == null", vname) {
        makeReturn("null");
      }
      makeReturn("%s.%s;", vname, format_params(expr, params))
    }
  }

  private def _var_name(attrname: String, varname: String) = {
    if (varname != null) varname
    else attrname
  }

  protected final def attr_expr(attrName: String, expr: Seq[AnyRef]) = {
    if (expr.isEmpty) attrName
    else if (expr.length == 1) expr(0).toString
    else format_expr_params_array(expr)
  }

  protected final def format_expr_params_array(expr: Seq[AnyRef]) = {
    format_params(expr(0).toString, expr.tail)
  }

  def publicIsMethod(attrName: String, expr: AnyRef*) {
    method("public boolean is%s()", attrName.capitalize, attr_expr(attrName, expr)) {
    }
  }

  def publicSetMethod(attrName: String, typeName: String,
       paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    val pname = if (paramName == null) attrName else paramName
    val vname = if (varName == null) attrName else varName
    method("public void set%s(%s %s)", attrName.capitalize, typeName, pname) {
      if (!expr.isEmpty) {
        assignThis(vname, expr)
      } else {
        assignThis(vname, pname)
      }
    } 
  }

  def publicSetOrNullMethod(attrName: String, typeName: String,
       paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    val pname = if (paramName == null) attrName else paramName
    val vname = if (varName == null) attrName else varName
    method("public void set%s(%s %s)", attrName.capitalize, typeName, pname) {
      makeIf("%s == null", pname) {
        assignThisNull(vname);
      }
      if (!expr.isEmpty) {
        assignThis(vname, expr)
      } else {
        assignThis(vname, pname)
      }
    } 
  }

  def publicWithMethod(className: String, attrName: String, typeName: String,
       paramName: String = null, varName: String = null, expr: Seq[AnyRef] = Nil) {
    val pname = if (paramName == null) attrName else paramName
    val vname = if (varName == null) attrName else varName
    method("public %s with%s(%s %s)", className, attrName.capitalize, typeName, pname) {
      if (!expr.isEmpty) {
        assignThis(vname, expr)
      } else {
        assignThis(vname, pname)
      }
      makeReturnThis
    } 
  }

  def publicWithOrNullMethod(className: String, attrName: String, typeName: String,
       paramName: String, varName: String, expr: Seq[AnyRef]) {
    val pname = if (paramName == null) attrName else paramName
    val vname = if (varName == null) attrName else varName
    method("public %s with%s(%s %s)", className, attrName.capitalize, typeName, pname) {
      makeIf("%s == null", pname) {
        assignThisNull(vname);
      }
      if (!expr.isEmpty) {
        assignThis(vname, expr)
      } else {
        assignThis(vname, pname)
      }
      makeReturnThis
    } 
  }

  def protectedMethod(signature: String, params: AnyRef*)(body: => Unit) {
    method("protected " + format_params(signature, params)) {
      body
    }
  }

  def protectedFinalVoidMethod(signature: String, params: AnyRef*)(body: => Unit) {
    method("protected final void " + format_params(signature, params)) {
      body
    }
  }

  def privateMethod(signature: String, params: AnyRef*)(body: => Unit) {
    method("private " + format_params(signature, params)) {
      body
    }
  }

  def publicStaticMethod(signature: String, params: AnyRef*)(body: => Unit) {
    method("public static " + format_params(signature, params)) {
      body
    }
  }

  // constructor
  def publicConstructor(signature: String, params: AnyRef*)(body: => Unit) {
    constructor("public " + format_params(signature, params)) {
      body
    }
  }
  
  // static
  def staticBlock(body: => Unit) {
    pln("static {")
    indentUp
    body
    indentDown
    pln("}")
  }

  //
  def ifReturn(condition: String, return_value: String = null) {
    makeIf(condition) {
      if (return_value != null) {
        pln("return %s;".format(return_value))
      } else {
        pln("return;")
      }
    }
  }

  // var
  def varListNewArrayList(varname: String, classname: String) {
    pln("List<%s> %s = new ArrayList<%s>();".format(classname, varname, classname))
  }

  // assign
  def assign(varname: String, expr: String) {
      pln("%s = %s;".format(varname, expr))
  }

  def assign(varname: String, expr: String, args: AnyRef*) {
      pln("%s = %s;".format(varname, format_params(expr, args)))
  }

  def assignNull(varname: String) {
      pln("%s = null;".format(varname))
  }

  def assignTrue(varname: String) {
      pln("%s = true;".format(varname))
  }

  def assignFalse(varname: String) {
      pln("%s = false;".format(varname))
  }

  def assignNew(varname: String, newclass: String) {
    pln("%s = new %s();".format(varname, newclass))
  }

  def assignNew(varname: String, newclass: String, signature: String, params: AnyRef*) {
    val s = if (params.isEmpty) signature
            else format_params(signature, params)
    pln("%s = new %s(%s);".format(varname, newclass, s))
  }

  def assignNewContainer(varname: String, newclass: String, containee: String) {
    pln("%s = new %s<%s>();".format(varname, newclass, containee))
  }

  def assignNewContainer(varname: String, newclass: String, containee: String, signature: String, params: Seq[AnyRef] = Nil) {
    val s = if (params.isEmpty) signature
            else format_params(signature, params)
    pln("%s = new %s<%s>(%s);".format(varname, newclass, containee, s))
  }

  def assignNewArrayList(varname: String, classname: String) {
    assignNewContainer(varname, "ArrayList", classname)
  }

  def assignNewArrayList(varname: String, classname: String, signature: String, params: AnyRef*) {
    assignNewContainer(varname, "ArrayList", classname, signature, params)
  }

  // assignThis
  def assignThis(varname: String, expr: String) {
    pln("this.%s = %s;".format(varname, expr))
  }

  def assignThis(varname: String, expr: Seq[AnyRef]) {
    pln("this.%s = %s;".format(varname, format_expr(expr)))
  }

  def assignThis(varname: String, expr: String, args: AnyRef*) {
    pln("this.%s = %s;".format(varname, format_params(expr, args)))
  }

  def assignThisNull(varname: String) {
    pln("this.%s = null;".format(varname))
  }

  def assignThisTrue(varname: String) {
    pln("this.%s = true;".format(varname))
  }

  def assignThisNew(varname: String, newclass: String) {
    pln("this.%s = new %s();".format(varname, newclass))
  }

  def assignThisNew(varname: String, newclass: String, signature: String, params: Seq[AnyRef] = Nil) {
    val s = if (params.isEmpty) signature
            else format_params(signature, params)
    pln("this.%s = new %s(%s);".format(varname, newclass, s))
  }

  def assignThisNewContainer(varname: String, newclass: String, containee: String) {
    pln("this.%s = new %s<%s>();".format(varname, newclass, containee))
  }

  def assignThisNewContainer(varname: String, newclass: String, containee: String, signature: String, params: Seq[AnyRef] = Nil) {
    val s = if (params.isEmpty) signature
            else format_params(signature, params)
    pln("this.%s = new %s<%s>(%s);".format(varname, newclass, containee, s))
  }

  def assignThisNewArrayList(varname: String, classname: String) {
    assignThisNewContainer(varname, "ArrayList", classname)
  }

  def assignThisNewArrayList(varname: String, classname: String, signature: String, params: Seq[AnyRef] = Nil) {
    assignThisNewContainer(varname, "ArrayList", classname, signature, params)
  }
}
