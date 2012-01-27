package org.simplemodeling.SimpleModeler.entities.android

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
 * @since   Jul. 18, 2011
 * @version Oct. 10, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidContentProviderJavaClassDefinition(
    pContext: PEntityContext,
    aspects: Seq[JavaAspect],
    pobject: PObjectEntity) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  private def _table_name_const(entity: SMDomainEntity) = {
    pContext.constantName(entity.term + "_TABLE")
  }

  private def _database_helper(entity: SMDomainEntity) = {
    pContext.className(entity.term + "DatabaseHelper")
  }

  private def _resource_const_name(entity: SMDomainEntity) = {
    pContext.constantName(entity.term)
  }

  private def _resource_id_const_name(entity: SMDomainEntity) = {
    pContext.constantName(entity.term + "_ID")
  }

  private def _contract_name = {
    pContext.className(modelPackage.name + "Contract")
  }

  /*
  private def _projection_map_name(entity: SMDomainEntity) = { // XXX
    pContext.className(entity.term + "ProjectionMap")
  }
*/
  override protected def head_imports_Extension {
    jm_import("java.io.IOException")
    jm_import("android.net.Uri")
    jm_import("android.content.*")
    jm_import("android.database.*")
  }

  override protected def attribute_variables_extension {
    /*
    jm_pln("""private static final String DTABASE_NAME = "%s";""", "data")
    jm_pln("""private static final int DTABASE_VERSION = 1;""")
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        jm_pln("""private static final String %s = "%s";""", _table_name_const(entity), entity.term)
      }
    })
*/
    var count = 1
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        jm_private_static_final_int(_resource_const_name(entity), count)
        count += 1
        jm_private_static_final_int(_resource_id_const_name(entity), count)
        count += 1
      }
    })
    jm_pln("private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);");
    /*
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        jm_pln("private static HashMap<String, String> %s = new HashMap<String, String();", _projection_map_name(entity));
      }
    })*/
    jm_static {
      traverse(new JavaMakerModelElementVisitor(jm_maker) {
        override protected def visit_Entity(entity: SMDomainEntity) {
          jm_pln("""uriMatcher.addURI(%s.AUTHORITY, "%s", %s);""", _contract_name, entity.term, _resource_const_name(entity))
          jm_pln("""uriMatcher.addURI(%s.AUTHORITY, "%s/#", %s_ID);""", _contract_name, entity.term, _resource_const_name(entity))
        }
      })
      /*      traverse(new JavaMakerModelElementVisitor(jm_maker) {
        override protected def visit_Entity(entity: SMDomainEntity) {
          val pmname = _projection_map_name(entity) 
          jm_pln("""%s.put(BaseColumn._ID, "_id");""", pmname)
          for (a <- entity.attributes) {
            jm_pln("""%s.put(%s.%s, "%s");""", pmname, _contract_name, a.name, a.name)
          }
        }
      })*/
    }
    jm_pln("private %s factory;", pContext.factoryName(pobject))
    jm_pln("private %s repository;", pContext.repositoryName(pobject))
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def service_methods {
    jm_override_public_method("boolean onCreate()") {
      jm_assign("factory", "%s.getFactory()", pContext.factoryName(pobject))
      jm_assign("repository", "factory.create%s()", pContext.repositoryName(pobject))
      jm_return("true")
    }
    jm_override_public_method("String getType(Uri uri)") {
      jm_switch("uriMatcher.match(uri)") {
        traverse(new JavaMakerModelElementVisitor(jm_maker) {
          override protected def visit_Entity(entity: SMDomainEntity) {
            jm_case_return(_resource_const_name(entity), "%s.%s.CONTENT_TYPE", _contract_name, pContext.className(entity.term))
            jm_case_return(_resource_id_const_name(entity), "%s.%s.CONTENT_ITEM_TYPE", _contract_name, pContext.className(entity.term))
          }
        })
        jm_case_default {
          jm_pln("""throw new IllegalArgumentException("Unknown URL " + uri);""")
        }
      }
    }
    jm_override_public_method("Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)") {
      jm_try {
        jm_switch("uriMatcher.match(uri)") {
          traverse(new JavaMakerModelElementVisitor(jm_maker) {
            override protected def visit_Entity(entity: SMDomainEntity) {
              jm_case_return(_resource_const_name(entity), "repository.query%s(projection, selection, selectionArgs, sortOrder)", entity.term.capitalize)
              jm_case(_resource_id_const_name(entity)) {
                jm_pln("String id = uri.getPathSegments().get(1);")
                jm_pln("return repository.queryId%s(id);", entity.term.capitalize)
              }
            }
          })
          jm_case_default {
            jm_pln("""throw new IllegalArgumentException("Unknown URL " + uri);""")
          }
        }
      }
      jm_catch_end("IOException e") {
        jm_return_null
      }
    }
    jm_override_public_method("Uri insert(Uri uri, ContentValues values)") {
      jm_try {
        jm_switch("uriMatcher.match(uri)") {
          traverse(new JavaMakerModelElementVisitor(jm_maker) {
            override protected def visit_Entity(entity: SMDomainEntity) {
              jm_case_return(_resource_const_name(entity), "Uri.withAppendedPath(uri, Long.toString(repository.insert%s(values)))", entity.term.capitalize)
              jm_case(_resource_id_const_name(entity)) {
                jm_pln("""throw new IllegalArgumentException("Unknown URL " + uri);""")
              }
            }
          })
          jm_case_default {
            jm_pln("""throw new IllegalArgumentException("Unknown URL " + uri);""")
          }
        }
      }
      jm_catch_end("IOException e") {
        jm_return_null
      }
    }
    jm_override_public_method("int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)") {
      jm_try {
        jm_switch("uriMatcher.match(uri)") {
          traverse(new JavaMakerModelElementVisitor(jm_maker) {
            override protected def visit_Entity(entity: SMDomainEntity) {
              jm_case_return(_resource_const_name(entity), "repository.update%s(values, selection, selectionArgs)", entity.term.capitalize)
              jm_case(_resource_id_const_name(entity)) {
                jm_pln("String id = uri.getPathSegments().get(1);")
                jm_pln("return repository.updateId%s(id, values);", entity.term.capitalize)
              }
            }
          })
          jm_case_default {
            jm_pln("""throw new IllegalArgumentException("Unknown URL " + uri);""")
          }
        }
      }
      jm_catch_end("IOException e") {
        jm_return("0")
      }
    }
    jm_override_public_method("int delete(Uri uri, String selection, String[] selectionArgs)") {
      jm_try {
        jm_switch("uriMatcher.match(uri)") {
          traverse(new JavaMakerModelElementVisitor(jm_maker) {
            override protected def visit_Entity(entity: SMDomainEntity) {
              jm_case_return(_resource_const_name(entity), "repository.delete%s(selection, selectionArgs)", entity.term.capitalize)
              jm_case(_resource_id_const_name(entity)) {
                jm_pln("String id = uri.getPathSegments().get(1);")
                jm_pln("return repository.deleteId%s(id);", entity.term.capitalize)
              }
            }
          })
          jm_case_default {
            jm_pln("""throw new IllegalArgumentException("Unknown URL " + uri);""")
          }
        }
      }
      jm_catch_end("IOException e") {
        jm_return("0")
      }
    }
  }
  /*
  // OLD
  override protected def package_methods_Entity(entity: SMDomainEntity) {
    val termname = entity.term.capitalize
    val entityname = entity.name
    val docname = pContext.entityDocumentName(entity)
    val cursor = "Cursor<%s>".format(docname)
    val ecursor = "Cursor<%s>".format(entityname)
    val query = "Query"
    val idtype = "long"
    // table
    jm_public_method("void create%s() throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void drop%s() throws IOException", termname) {
      jm_UnsupportedOperationException
    }
    // document
    jm_public_method("%s get%s(%s id) throws IOException", docname, termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s query%s(String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IOException", cursor, termname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s query%s(String query) throws IOException", cursor, termname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void insert%s(%s data) throws IOException", termname, docname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(%s data) throws IOException", termname, docname) {
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
    jm_public_method("void delete%s(%s id) throws IOException", termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void delete%s(%s id, String selection, String[] selectionArgs) throws IOException", termname, idtype) {
      jm_UnsupportedOperationException
    }
    // entity
    jm_public_method("%s getEntity%s(%s id) throws IOException", entityname, termname, idtype) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s queryEntities%s(String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IOException", ecursor, termname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s queryEntities%s(String query) throws IOException", entityname, termname) {
      jm_UnsupportedOperationException
    }
  }

  override protected def object_auxiliary {
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        jm_code("""
pprivate static class %dbhelper% extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_SQL = "create table " +
            %tablename% + " (%sqlcreate%)";
    private static final String DROP_TABLE_SQL = "drop table if exists " + %tablename%;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_SQL);
        onCreate(db);
    }
}
""", Map("%tablename%" -> _table_name_const(entity),
         "%dbhelper%" -> _database_helper(entity),
         "%sqlcreate%" -> _sql_create(entity)))
      }
    });
  }

  private def _sql_create(entity: SMDomainEntity): String = {
    (for (a <- entity.attributes) yield {
      if (a.isId) {
        "_id integer primary key autoincrement"
      } else {
        a.multiplicity.kind match {
          case SMMultiplicityOne => "%s %s not null".format(a.name, _datatype(a)) 
          case SMMultiplicityZeroOne => "%s %s".format(a.name, _datatype(a))
          case SMMultiplicityOneMore => sys.error("unsupported yet");
          case SMMultiplicityZeroMore => sys.error("unsupported yet");
          case SMMultiplicityRange => sys.error("unsupported yet");
        }
      }
    }).mkString(", ")
  }
  //_id integer primary key autoincrement," +
//                " date long not null, message text not null, url text)";
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

      override protected def apply_Double(datatype: XDouble): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

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

      override protected def apply_Int(datatype: XInt): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Integer(datatype: XInteger): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Language(datatype: XLanguage): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_Long(datatype: XLong): String = {
        throw new UnsupportedOperationException("not implemented yet")
      }

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
  */
}
