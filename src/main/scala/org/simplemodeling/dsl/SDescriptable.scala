package org.simplemodeling.dsl

import org.goldenport.sdoc._

/*
 * @since   Sep. 15, 2008
 *  version Aug.  8, 2009
 * @version Nov. 22, 2012
 * @author  ASAMI, Tomoharu
 */
trait SDescriptable {
  type Descriptable_TYPE
  val resume = new SSummary
  var title: SDoc = SEmpty
  var subtitle: SDoc = SEmpty
  var label: SDoc = SEmpty
  private var _description: SDoc = SEmpty
  private var _note: SDoc = SEmpty

  final def caption: SDoc = resume.caption

  final def caption_=(aCaption: SDoc) {
    require (aCaption != null)
    resume.caption = adjust_sdoc(aCaption)
  }

  final def brief: SDoc = resume.brief

  final def brief_=(aBrief: SDoc) {
    require (aBrief != null)
    resume.brief = adjust_sdoc(aBrief)
  }

  final def summary: SDoc = resume.summary

  final def summary_=(aSummary: SDoc) {
    require (aSummary != null)
    resume.summary = adjust_sdoc(aSummary)
  }

  final def description = _description

  final def description_=(aDoc: SDoc) {
    require (aDoc != null)
    _description = adjust_sdoc(aDoc)
  }

  final def note = _note

  final def note_=(aDoc: SDoc) {
    require (aDoc != null)
    _note = adjust_sdoc(aDoc)
  }

  final def description_is(aDoc: SDoc): Descriptable_TYPE = {
    description = aDoc
    this.asInstanceOf[Descriptable_TYPE]
  }

  final def because(aDoc: SDoc): Descriptable_TYPE = {
    description = aDoc
    this.asInstanceOf[Descriptable_TYPE]
  }

  final def note(aDoc: SDoc): Descriptable_TYPE = {
    note = aDoc
    this.asInstanceOf[Descriptable_TYPE]
  }

  private def adjust_sdoc(aDoc: SDoc): SDoc = {
    if (aDoc.isInstanceOf[SFragment]) {
      aDoc.length match {
	case 0 => SEmpty
	case 1 => aDoc.getChild(0)
	case _ => aDoc
      }
    } else aDoc
  }
}
