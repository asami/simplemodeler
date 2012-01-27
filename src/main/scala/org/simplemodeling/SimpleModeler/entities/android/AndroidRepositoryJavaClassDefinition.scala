package org.simplemodeling.SimpleModeler.entities.android

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * @since   Jul. 17, 2011
 * @version Oct. 10, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidRepositoryJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends RepositoryJavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  private def _table_name_const(entity: PEntityEntity) = {
    entity.term.toUpperCase + "_TABLE" // XXX pContext
  }

  private def _column_name(entity: PEntityEntity, attr: PAttribute) = { 
    attr.name // XXX pContext
  }

  private def _id_column_name(entity: PEntityEntity) = {
    entity.idName
  }

  private def _id_attr_name(entity: PEntityEntity) = {
    entity.idName
  }

  private def _database_helper(entity: PEntityEntity) = {
    pContext.className(entity.term + "DatabaseHelper")
  }

  private def _contract_name = {
    pContext.className(modelPackage.name + "Contract")
  }

  private def _projection_map_name(entity: PEntityEntity) = {
    pContext.className(entity.term + "ProjectionMap")
  }

  private def _entity_class_name(entity: PEntityEntity) = {
    pContext.className(entity.term)
  }

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("android.net.Uri")
    jm_import("android.content.*")
    jm_import("android.database.*")
    jm_import("android.database.sqlite.*")
  }

  override protected def attribute_variables_extension {
    jm_pln("""private static final String DATABASE_NAME = "%s";""", "data")
    jm_pln("""private static final int DATABASE_VERSION = 1;""")
    package_children_entity_map { entity =>
      jm_pln("""private static final String %s = "%s";""", _table_name_const(entity), entity.term_en)
    }
    package_children_entity_map { entity =>
      jm_pln("private static HashMap<String, String> %s = new HashMap<String, String>();", _projection_map_name(entity));
    }
    jm_pln("{")
    package_children_entity_map { entity =>
      val pmname = _projection_map_name(entity)
      val entitydef = _contract_name + "." + _entity_class_name(entity)
      jm_pln("""%s.put(%s._ID, "_id");""", pmname, entitydef)
      for (a <- entity.attributes) {
        jm_pln("""%s.put(%s.%s, "%s");""", pmname, entitydef, pContext.constantName(a.name), a.name)
      }
    }    
    jm_pln("}")
/*
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        jm_pln("""private static final String %s = "%s";""", _table_name_const(entity), entity.term_en)
      }
    })
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        jm_pln("private static HashMap<String, String> %s = new HashMap<String, String>();", _projection_map_name(entity));
      }
    })
    jm_static {
      traverse(new JavaMakerModelElementVisitor(jm_maker) {
        override protected def visit_Entity(entity: SMDomainEntity) {
          val pmname = _projection_map_name(entity)
          val entitydef = _contract_name + "." + _entity_class_name(entity)
          jm_pln("""%s.put(%s._ID, "_id");""", pmname, entitydef)
          for (a <- entity.attributes) {
            jm_pln("""%s.put(%s.%s, "%s");""", pmname, entitydef, pContext.constantName(a.name), a.name)
          }
        }
      })
    }
*/
    jm_pln("private DatabaseHelper db;")
  }

  override protected def lifecycle_methods_open_method {
    jm_public_method("void open()") {
//      jm_pln("helper = new DatabaseHelper(context);")
//      jm_pln("db = helper.getWritableDatabase();")
    }
  }
  
  override protected def lifecycle_methods_close_method {
    jm_public_method("void close()") {
//      jm_pln("db.close();")
//      jm_pln("helper.close();")
    }
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    val termname = entity.term.capitalize
    val entityname = entity.name
    val docname = pContext.entityDocumentName(entity)
    val dcursor = "Cursor".format(docname) // XXX Feed<Document>
    val ecursor = "Cursor".format(entityname) // XXX Feed<Entity>
    val query = "Query"
    val idtype = "long" // XXX
    val tablename = _table_name_const(entity)
    val idcolumnname = _id_column_name(entity) 
    val idattrname = _id_attr_name(entity)
    // table
    jm_public_method("void create%s() throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void drop%s() throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    // document
    jm_public_method("%s get%s(%s id) throws IOException", docname, termname, idtype) {
      jm_code("""String columns[] = new String[] {""};
String selection = "";
String selectionArgs[] = new String[] { Long.toString(id) };
String groupBy = null;
String having = null;
String orderBy = null;
//Cursor c = db.query(%table%, columns, selection, selectionArgs, groupBy, having, orderBy);
//while (c.moveToNext()) {
//    Builder builder = %class%.Builder();
//    builder.with_cursor(c);
//    return builder.build();    
//}
return null;""", Map("%table%" -> tablename, "%class%" -> docname))
    }
    jm_public_method("Cursor query%s(String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IOException", termname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("Cursor query%s(String query) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("Cursor queryId%s(String id) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("long insert%s(%s data) throws IOException", termname, docname) {
//      jm_return("insertCustomer(data.toContentValues())")
      jm_UnsupportedOperationException
    }
    jm_public_method("long insert%s(ContentValues values) throws IOException", termname) {
//      jm_return("db.insert(%s, null, value)");
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(%s data) throws IOException", termname, docname) {
//      jm_pln("""db.update(CUSTOMER_TABLE, data.toContentValuesWithoutId(), "customerId = ?", data.customerId);""")
//      jm_pln("""db.update(%s, data.toContentValuesWithoutId(), "%s = ?", data.%s);""", tablename, idcolumnname, idattrname)      
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(String data) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(String[] data) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(Map<String, Object> data) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("int update%s(ContentValues values, String selection, String[] selectionArgs) throws IOException", termname) {
//      jm_return("db.update(%s, values, whereClause, whereArgs)", "CUSTOMER_TABLE")
      jm_UnsupportedOperationException
    }
    jm_public_method("int updateId%s(String id, ContentValues values) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void delete%s(%s id) throws IOException", termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void delete%s(%s id, String selection, String[] selectionArgs) throws IOException", termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("int delete%s(String selection, String[] selectionArgs) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("int deleteId%s(String id) throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    // document
    jm_public_method("%s getDocument%s(%s id) throws IOException", docname, termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s queryDocuments%s(String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IOException", dcursor, termname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s queryDocuments%s(String query) throws IOException", dcursor, termname) {
      jm_UnsupportedOperationException
    }
    // entity
    jm_public_method("%s getEntity%s(%s id) throws IOException", entityname, termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s queryEntities%s(String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IOException", ecursor, termname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s queryEntities%s(String query) throws IOException", ecursor, termname) {
      jm_UnsupportedOperationException
    }
  }

  override protected def object_auxiliary {
    jm_pln("private static class DatabaseHelper extends SQLiteOpenHelper {")
    jm_indent_up
    package_children_entity_map { entity =>
      val tablename = _table_name_const(entity)
      val sqlcreate = _sql_create(entity)
      jm_pln("""private static final String CREATE_%s_TABLE_SQL = "create table " +""", tablename)
      jm_pln("""        %s + " (%s)";""", tablename, sqlcreate)
      jm_pln("""private static final String DROP_%s_TABLE_SQL = "drop table if exists " + %s;""", tablename, tablename)
    }
/*
    traverse {
      new JavaMakerModelElementVisitor(jm_maker) {
        override protected def visit_Entity(entity: SMDomainEntity) {
          val tablename = _table_name_const(entity)
          val sqlcreate = _sql_create(entity)
          jm_pln("""private static final String CREATE_%s_TABLE_SQL = "create table " +""", tablename)
          jm_pln("""        %s + " (%s)";""", tablename, sqlcreate)
          jm_pln("""private static final String DROP_%s_TABLE_SQL = "drop table if exists " + %s;""", tablename, tablename)
        }
      }
    }
*/
    jm_code("""
DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
""")
    jm_override_public_method("void onCreate(SQLiteDatabase db)") {
      package_children_entity_map { entity =>
        val tablename = _table_name_const(entity)
        jm_pln("db.execSQL(CREATE_%s_TABLE_SQL);", tablename)
      }
/*
      traverse {
        new JavaMakerModelElementVisitor(jm_maker) {
          override protected def visit_Entity(entity: SMDomainEntity) {
            val tablename = _table_name_const(entity)
            jm_pln("db.execSQL(CREATE_%s_TABLE_SQL);", tablename)
          }
        }
      }
*/      
    }
    jm_override_public_method("void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)") {
      package_children_entity_map { entity =>
        val tablename = _table_name_const(entity)
        jm_pln("db.execSQL(DROP_%s_TABLE_SQL);", tablename)
      }
/*
      traverse {
        new JavaMakerModelElementVisitor(jm_maker) {
          override protected def visit_Entity(entity: SMDomainEntity) {
            val tablename = _table_name_const(entity)
            jm_pln("db.execSQL(DROP_%s_TABLE_SQL);", tablename)
          }
        }
      }
*/      
      jm_pln("onCreate(db);")
    }
    jm_indent_down
    jm_pln("}")
  }

  private def _sql_create(entity: PEntityEntity): String = {
    (for (a <- entity.attributes) yield {
      if (a.isId) {
        "_id integer primary key autoincrement"
      } else {
        a.multiplicity match {
          case POne => "%s %s not null".format(_column_name(entity, a), _datatype(a)) 
          case PZeroOne => "%s %s".format(_column_name(entity, a), _datatype(a))
          case POneMore => sys.error("unsupported yet");
          case PZeroMore => "%s %s".format(_column_name(entity, a), _datatype(a))
          case _: PRange => sys.error("unsupported yet");
        }
      }
    }).mkString(", ")
  }

/*
  private def _sql_create(entity: SMDomainEntity): String = {
    (for (a <- entity.attributes) yield {
      if (a.isId) {
        "_id integer primary key autoincrement"
      } else {
        a.multiplicity.kind match {
          case SMMultiplicityOne => "%s %s not null".format(_column_name(entity, a), _datatype(a)) 
          case SMMultiplicityZeroOne => "%s %s".format(_column_name(entity, a), _datatype(a))
          case SMMultiplicityOneMore => sys.error("unsupported yet");
          case SMMultiplicityZeroMore => sys.error("unsupported yet");
          case SMMultiplicityRange => sys.error("unsupported yet");
        }
      }
    }).mkString(", ")
  }
*/
  //_id integer primary key autoincrement," +
//                " date long not null, message text not null, url text)";
  private def _datatype(a: PAttribute): String = {
    if (a.modelAttribute != null) {
      _datatype(a.modelAttribute)
    } else if (a.modelAssociation != null) {
      _datatype_association(a.modelAssociation)
    } else if (a.modelPowertype != null) {
      _datatype_powertype(a.modelPowertype)
    } else {
      sys.error("Not reached")
    }
  }

  private def _datatype(a: SMAttribute): String = {
    a.attributeType.typeObject match {
      case dt: SMDatatype => _datatype_datatype(dt);
      case v: SMDomainValue => _datatype_datatype(v.datatype)
    }
  }

  private def _datatype_datatype(datatype: SMDatatype): String = {
    _datatype_datatype(datatype.dslDatatype)
  }

  private def _datatype_datatype(datatype: SDatatype): String = {
    datatype(new SDatatypeFunction[String] {

      override protected def apply_AnyURI(datatype: XAnyURI): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Base64Binary(datatype: XBase64Binary): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Boolean(datatype: XBoolean): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Byte(datatype: XByte): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Date(datatype: XDate): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DateTime(datatype: XDateTime): String = "date"

      override protected def apply_Decimal(datatype: XDecimal): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Double(datatype: XDouble): String = "decimal"

      override protected def apply_Duration(datatype: XDuration): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Float(datatype: XFloat): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GDay(datatype: XGDay): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GMonth(datatype: XGMonth): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GMonthDay(datatype: XGMonthDay): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GYear(datatype: XGYear): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GYearMonth(datatype: XGYearMonth): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_HexBinary(datatype: XHexBinary): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Int(datatype: XInt): String = "INTEGER"

      override protected def apply_Integer(datatype: XInteger): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Language(datatype: XLanguage): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Long(datatype: XLong): String = "INTEGER"

      override protected def apply_NegativeInteger(datatype: XNegativeInteger): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_NonNegativeInteger(datatype: XNonNegativeInteger): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PositiveInteger(datatype: XPositiveInteger): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Short(datatype: XShort): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_String(datatype: XString): String = "text"

      override protected def apply_Time(datatype: XTime): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedByte(datatype: XUnsignedByte): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedInt(datatype: XUnsignedInt): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedLong(datatype: XUnsignedLong): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedShort(datatype: XUnsignedShort): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Category(datatype: XCategory): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Email(datatype: XEmail): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GeoPt(datatype: XGeoPt): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_IM(datatype: XIM): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Link(datatype: XLink): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PhoneNumber(datatype: XPhoneNumber): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PostalAddress(datatype: XPostalAddress): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Rating(datatype: XRating): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Text(datatype: XText): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_User(datatype: XUser): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }
    })
  }

  private def _datatype_association(a: SMAssociation): String = {
    a.associationType.typeObject.attributes.find(_.isId).cata(
        _datatype(_), "String")
  }

  private def _datatype_powertype(a: SMPowertypeRelationship): String = {
    "String"
  }
}
