package org.simplemodeling.SimpleModeler.entities.extjs

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Mar. 31, 2012
 * @version Apr.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsEntityEntity(aContext: ExtjsEntityContext) extends ExtjsObjectEntity(aContext) with PEntityEntity {
/*
Ext.define('App1.model.Account', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'sectionname'},
        {name: 'produceamount',  type: 'long'},
        {name: 'loadship',  type: 'long'},
        {name: 'productship',  type: 'long'},
        {name: 'internalconvert',  type: 'long'},
        {name: 'sumofinputcost',  type: 'long'},
        {name: 'laborexpense',  type: 'long'},
        {name: 'expense',  type: 'long'},
        {name: 'sumofcausecost',  type: 'long'},
        {name: 'initialstock',  type: 'long'},
        {name: 'endstock',  type: 'long'},
        {name: 'stockinprocess',  type: 'long'},
        {name: 'shipinprocess',  type: 'long'},
        {name: 'sumofcost',  type: 'long'},
        {name: 'costratio', type: 'float'}
    ]
});
*/
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ExtjsClassDefinition(aContext, Nil, ExtjsEntityEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
