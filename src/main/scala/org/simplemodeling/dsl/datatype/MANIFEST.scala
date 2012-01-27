package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * Nov.  6, 2008
 * Nov. 15, 2008
 */
class MANIFEST extends SManifest {
  caption = "データタイプ。"
  brief = ""
  description = <text>
  </text>
  objects()
}

object asami extends SPerson {
  family_name = "浅海"
  given_name = "智晴"
}

/**
<table title="データタイプ" id="primitiveDatatype">
<attribute format='latex2e pdf' name='style'>width:8cm</attribute>
<colgroup>
<col></col>
<col></col>
<col></col>
<col></col>
</colgroup>
<thead>
カテゴリ,タイプ,意味,Java(参考)
</thead>
<tbody>
<ul>
  <li>文字列</li>
  <ul>
<tr>
  <td><code>string</code></td>
  <td>文字列</td>
  <td>java.lang.String</td>
</tr>
<!--
<tr>
  <td><code>Name</code></td>
  <td>XML文法上のName</td>
  <td>java.lang.String</td>
</tr>
<tr>
  <td><code>QName</code></td>
  <td>XML文法上のQName</td>
  <td>java.lang.String</td>
</tr>
<tr>
  <td><code>NCName</code></td>
  <td>XML文法上のNCName</td>
  <td>java.lang.String</td>
</tr>
-->

  </ul>
  <li>数値</li>
  <ul>

<tr>
  <td><code>decilal</code></td>
  <td>無限精度数値</td>
  <td>java.math.BigDecimal</td>
</tr>
<tr>
  <td><code>boolean</code></td>
  <td>ブール値</td>
  <td>boolean, java.lang.Boolean</td>
</tr>
<tr>
  <td><code>float</code></td>
  <td>16bit浮動小数点数</td>
  <td>float, java.lang.Float</td>
</tr>
<tr>
  <td><code>double</code></td>
  <td>32bit浮動小数点数</td>
  <td>double, java.lang.Double</td>
</tr>
<tr>
  <td><code>integer</code></td>
  <td>無限精度整数</td>
  <td>java.math.BigInteger</td>
</tr>
<tr>
  <td><code>nonPositiveInteger</code></td>
  <td>0以下の無限精度整数</td>
  <td>java.math.BigInteger</td>
</tr>
<tr>
  <td><code>negativeInteger</code></td>
  <td>0未満の無限精度整数</td>
  <td>java.math.BigInteger</td>
</tr>
<tr>
  <td><code>long</code></td>
  <td>64bit整数</td>
  <td>long, java.lang.Long</td>
</tr>
<tr>
  <td><code>int</code></td>
  <td>32bit整数</td>
  <td>int, java.lang.Integer</td>
</tr>
<tr>
  <td><code>short</code></td>
  <td>16bit整数</td>
  <td>short, java.lang.Short</td>
</tr>
<tr>
  <td><code>byte</code></td>
  <td>8bit整数</td>
  <td>byte, java.lang.Byte</td>
</tr>
<tr>
  <td><code>nonNegativeInteger</code></td>
  <td>0以上の無限精度整数</td>
  <td>java.math.BigInteger</td>
</tr>
<tr>
  <td><code>positiveInteger</code></td>
  <td>1以上の無限精度整数</td>
  <td>java.math.BigInteger</td>
</tr>
<tr>
  <td><code>unsignedLong</code></td>
  <td>0以上の64bit整数</td>
  <td>java.math.BigInteger</td>
</tr>
<tr>
  <td><code>unsignedInt</code></td>
  <td>0以上の32bit整数</td>
  <td>long, java.lang.Long</td>
</tr>
<tr>
  <td><code>unsignedShort</code></td>
  <td>0以上の16bit整数</td>
  <td>int, java.lang.Integer</td>
</tr>
<tr>
  <td><code>unsignedByte</code></td>
  <td>0以上の8bit整数</td>
  <td>short, java.lang.Short</td>
</tr>
  
  </ul>
  <li>時間</li>
  <ul>

<tr>
  <td><code>duration</code></td>
  <td>経過時間</td>
  <td>javax.xml.datatype.Duration</td>
</tr>
<tr>
  <td><code>dateTime</code></td>
  <td>日付+時間</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>time</code></td>
  <td>日付</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>date</code></td>
  <td>日付</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>gYearMonth</code></td>
  <td>年月</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>gYear</code></td>
  <td>年</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>gMonthDay</code></td>
  <td>月日</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>gDay</code></td>
  <td>日</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>
<tr>
  <td><code>gMonth</code></td>
  <td>月</td>
  <td>javax.xml.datatype.XMLGregorianCalendar</td>
</tr>

  </ul>

  <li>その他</li>
  <ul>

<tr>
  <td><code>hexBinary</code></td>
  <td>HEX形式バイナリ</td>
  <td>byte配列</td>
</tr>
<tr>
  <td><code>base64Binary</code></td>
  <td>BASE64形式バイナリ</td>
  <td>byte配列</td>
</tr>
<tr>
  <td><code>anyURI</code></td>
  <td>URI</td>
  <td>java.net.URI</td>
</tr>
<tr>
  <td><code>language</code></td>
  <td>言語</td>
  <td>java.util.Locale</td>
</tr>
  
  </ul>
</ul>
</tbody>
</table>
**/
