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
 * @since   Aug. 30, 2011
 * @version Oct. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidRestViewActivityJavaClassDefinition(
    pContext: PEntityContext,
    aspects: Seq[JavaAspect],
    pobject: PObjectEntity) extends JavaClassDefinition(pContext, aspects, pobject, null) {
  useDocument = false
  customBaseName = Some("GActivity<%s>".format(pContext.controllerName(pobject.packageName)))

  override protected def head_imports_Extension {
    jm_import("org.goldenport.android.*")
    jm_import("org.goldenport.android.traits.ListViewTrait")
    jm_import("android.os.Bundle")
    jm_import("android.content.Context")
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
  }

  override protected def object_auxiliary {
  val adaptername = "get%sRestFeedAdapter".format(termNameBase)
  jm_code("""
// @LayoutView(R.id.header)
// TextView mHeader;
// @ResourceString(R.string.header)
// String mHeaderLabel;
// @ResourceColor(R.color.header)
// int mHeaderColor;
// @IntentExtra("message")
// String mMessage;

public %name%() {
    addTrait(new ListViewTrait());
}

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
}

@Override
protected int get_Layout_id() {
    return R.layout.customer_rest_view;
}

@Override
protected void onStart() {
    super.onStart();
//    if (mMessage != null) {
//        mHeader.setText(mMessage);
//    } else {
//        mHeader.setText(mHeaderLabel);
//    }
//    mHeader.setTextColor(mHeaderColor);
    set_list_adapter(gcontroller.%adaptername%());
}
""", Map("%name%" -> name, "%adaptername%" -> adaptername))
  }
}
