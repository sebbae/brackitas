/*
 * [New BSD License]
 * Copyright (c) 2011-2012, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Brackit Project Team nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.xquery.function.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.brackit.xquery.util.annotation.FunctionAnnotation;
import org.brackit.as.http.HttpConnector;
import org.brackit.as.xquery.ASErrorCode;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Bool;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.Sequence;

/**
 * 
 * @author Henrique Valer
 * 
 */
@FunctionAnnotation(description = "Creates a predefined generic application "
		+ "skeleton. Automatically generated applications follow two main types, "
		+ "defined by the specified type ($applicationType): MVC applications "
		+ "($applicationType = \"MVC\") are created following the "
		+ "movel-view-controller style. Regular applications ($applicationType "
		+ "= REG) represents generic applications. In both cases, the skeleton "
		+ "includes a simple \"Hello world!\" initial page and some predefined "
		+ "CSS stylesheets.", parameters = { "$applicationName",
		"$applicationType" })
public class Generate extends AbstractFunction {

	public static String module = "module namespace %s=\"http://brackit.org/lib/%s/%s%s\"; \n";

	public static String importModule = "import module namespace %s=\"http://brackit.org/lib/%s/%s%s\"; \n";

	public static String todo = "\"TODO\" \n";

	public static String BSDLicense = "(: \n"
			+ " * \n"
			+ " * [New BSD License] \n"
			+ " * Copyright (c) 2011, Brackit Project Team <info@brackit.org> \n"
			+ " * All rights reserved. \n"
			+ " * \n"
			+ " * Redistribution and use in source and binary forms, with or without \n"
			+ " * modification, are permitted provided that the following conditions are met: \n"
			+ " *     * Redistributions of source code must retain the above copyright \n"
			+ " *       notice, this list of conditions and the following disclaimer. \n"
			+ " *     * Redistributions in binary form must reproduce the above copyright \n"
			+ " *       notice, this list of conditions and the following disclaimer in the \n"
			+ " *       documentation and/or other materials provided with the distribution. \n"
			+ " *     * Neither the name of the <organization> nor the \n"
			+ " *       names of its contributors may be used to endorse or promote products \n"
			+ " *       derived from this software without specific prior written permission. \n"
			+ " *  \n"
			+ " * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND \n"
			+ " * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED \n"
			+ " * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE \n"
			+ " * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY \n"
			+ " * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES \n"
			+ " * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; \n"
			+ " * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND \n"
			+ " * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT \n"
			+ " * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS \n"
			+ " * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. \n"
			+ " * \n" + " :)\n";

	public static String viewBody = "declare function view:default($content as item()) as item() {\n"
			+ "  template:base(template:head(),\n"
			+ "                template:header(),\n"
			+ "                template:teaser(),\n"
			+ "                template:menu(),\n"
			+ "                $content,\n"
			+ "                template:footerBrackit(),\n"
			+ "                template:footerYAML())\n" + "};\n";

	public static String controllerBody = "declare function controller:index() as item() {\n"
			+ "  let $msg :=\n"
			+ "    \"Hello World!\"\n"
			+ "  return\n"
			+ "    view:default($msg)\n" + "};\n";

	public static String tempHead = "declare function template:head() as item() {\n"
			+ "  <head>\n"
			+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n"
			+ "    <link href=\"http://localhost:8080/apps/%s/resources/css/%s.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
			+ "  </head>\n" + "};\n";

	public static String tempHeader = "declare function template:header() as item() {\n"
			+ "  <table style=\"width:100%;\">\n"
			+ "    <tr>\n"
			+ "      <td>\n"
			+ "        <div id=\"header\" align=\"center\">\n"
			+ "          <h1>Insert header here!</h1>\n"
			+ "        </div>\n"
			+ "      </td>\n" + "    </tr>\n" + "  </table>\n" + "};\n";

	public static String tempTeaser = "declare function template:teaser() as item() {\n"
			+ "  <div id=\"teaser\" align=\"center\">\n"
			+ "    <h2>Insert teaser here!</h2>\n" + "  </div>\n" + "};\n";

	public static String tempMenu = "declare function template:menu() as item() {\n"
			+ "  <div id=\"col1_content\" class=\"clearfix\">\n"
			+ "    <table style=\"width:100%;\">\n"
			+ "      <tr>\n"
			+ "        <td>\n"
			+ "          <ul class=\"vlist\">\n"
			+ "            <li><zu><a><h6>Link 1</h6></a></zu></li>\n"
			+ "            <li><zu><a><h6>Link 2</h6></a></zu></li>\n"
			+ "          </ul>\n"
			+ "        </td>\n"
			+ "      </tr>\n"
			+ "    </table>\n" + "  </div>\n" + "};\n";

	public static String tempFooterBrackit = "declare function template:footerBrackit() as item() {\n"
			+ "  <div id=\"footer\">\n"
			+ "    <a href=\"http://brackit.org\">Brackit XQuery engine</a>\n"
			+ "  </div>\n" + "};\n";

	public static String tempFooterYAML = "declare function template:footerYAML() as item() {\n"
			+ "  <div id=\"footer\">\n"
			+ "    Layout based on <a href=\"http://www.yaml.de/\">YAML</a>\n"
			+ "  </div>\n" + "};\n";

	public static String tempBaseBody = "declare function template:baseBody($header as item(),\n"
			+ "                                   $teaser as item(),\n"
			+ "                                   $menu as item(),\n"
			+ "                                   $content as item(),\n"
			+ "                                   $footerBrackit as item(),\n"
			+ "                                   $footerYAML as item()) as item() {\n"
			+ "  <div class=\"page_margins\">\n"
			+ "    <div class=\"page\">\n"
			+ "      {$header}\n"
			+ "      {$teaser}\n"
			+ "      <div id=\"main\">\n"
			+ "        <div id=\"col1\" role=\"complementary\">\n"
			+ "          {$menu}\n"
			+ "        </div>\n"
			+ "        <div id=\"col3\">\n"
			+ "          <div id=\"col3_content\" class=\"clearfix\">\n"
			+ "            {$content}\n"
			+ "          </div>\n"
			+ "          <div id=\"ie_clearing\"/>\n"
			+ "        </div>\n"
			+ "      </div>\n"
			+ "      {$footerBrackit}\n"
			+ "      {$footerYAML}\n" + "    </div>\n" + "  </div>\n" + "};\n";

	public static String tempBase = "declare function template:base($head as item(),\n"
			+ "                               $header as item(),\n"
			+ "                               $teaser as item(),\n"
			+ "                               $menu as item(),\n"
			+ "                               $content as item(),\n"
			+ "                               $footerBrackit as item(),\n"
			+ "                               $footerYAML as item()) {\n"
			+ "  <html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n"
			+ "    {$head}\n"
			+ "    <body>\n"
			+ "      {template:baseBody($header,$teaser,$menu,$content,$footerBrackit,$footerYAML)}\n"
			+ "    </body>\n" + "  </html>\n" + "};\n";

	public static String tempRegBody = "declare function template:default($content as item()) as item() {\n"
			+ "  template:base(template:head(),\n"
			+ "                template:header(),\n"
			+ "                template:teaser(),\n"
			+ "                template:menu(),\n"
			+ "                $content,\n"
			+ "                template:footerBrackit(),\n"
			+ "                template:footerYAML())\n" + "};\n";

	public static String regBody = "let $msg :=\n" + "  \"Hello World!\"\n"
			+ "return\n" + "  template:default($msg)\n";

	public static String mainCSS = "/**\n"
			+ " * \"Yet Another Multicolumn Layout\" - (X)HTML/CSS Framework\n"
			+ " *\n"
			+ " * (en) central stylesheet - layout example \"vertical_listnav\"\n"
			+ " * (de) Zentrales Stylesheet - Beispiellayout \"vertical_listnav\"\n"
			+ " *\n"
			+ " * @copyright       Copyright 2005-2011, Dirk Jesse\n"
			+ " * @license         CC-A 2.0 (http://creativecommons.org/licenses/by/2.0/),\n"
			+ " *                  YAML-C (http://www.yaml.de/en/license/license-conditions.html)\n"
			+ " * @link            http://www.yaml.de\n"
			+ " * @package         yaml\n"
			+ " * @version         3.3.1\n"
			+ " * @revision        $Revision: 501 $\n"
			+ " * @lastmodified    $Date: 2011-06-18 17:27:44 +0200 (Sa, 18 Jun 2011) $\n"
			+ " */\n" + "\n"
			+ "/* import core styles | Basis-Stylesheets einbinden */\n"
			+ "@import url(core/base.css);\n"
			+ "@import url(screen/basemod.css);\n"
			+ "@import url(screen/content.css);\n";

	public static String baseCSS = "/**\n"
			+ " * \"Yet Another Multicolumn Layout\" - (X)HTML/CSS Framework\n"
			+ " *\n"
			+ " * (en) YAML core stylesheet\n"
			+ " * (de) YAML Basis-Stylesheet\n"
			+ " *\n"
			+ " * Don't make any changes in this file!\n"
			+ " * Your changes should be placed in any css-file in your own stylesheet folder.\n"
			+ " *\n"
			+ " * @copyright       Copyright 2005-2011, Dirk Jesse\n"
			+ " * @license         CC-A 2.0 (http://creativecommons.org/licenses/by/2.0/),\n"
			+ " *                  YAML-C (http://www.yaml.de/en/license/license-conditions.html)\n"
			+ " * @link            http://www.yaml.de\n"
			+ " * @package         yaml\n"
			+ " * @version         3.3.1\n"
			+ " * @revision        $Revision: 501 $\n"
			+ " * @lastmodified    $Date: 2011-06-18 17:27:44 +0200 (Sa, 18 Jun 2011) $\n"
			+ " */\n"
			+ "\n"
			+ "@media all\n"
			+ "{\n"
			+ " /**\n"
			+ "  * @section browser reset\n"
			+ "  * @see     http://www.yaml.de/en/documentation/css-components/base-stylesheet.html\n"
			+ "  */\n"
			+ "\n"
			+ "  /* (en) Global reset of paddings and margins for all HTML elements */\n"
			+ "  /* (de) Globales Zurücksetzen der Innen- und Außenabstände für alle HTML-Elemente */\n"
			+ "  * { margin:0; padding:0; }\n"
			+ "\n"
			+ "  /* (en) Correction:margin/padding reset caused too small select boxes. */\n"
			+ "  /* (de) Korrektur:Das Zurücksetzen der Abstände verursacht zu kleine Selectboxen. */\n"
			+ "  option { padding-left:0.4em; } /* LTR */\n"
			+ "  select { padding:1px; }\n"
			+ "\n"
			+ " /**\n"
			+ "  * (en) Global fix of the Italics bugs in IE 5.x and IE 6\n"
			+ "  * (de) Globale Korrektur des Italics Bugs des IE 5.x und IE 6\n"
			+ "  *\n"
			+ "  * @bugfix\n"
			+ "  * @affected   IE 5.x/Win, IE6\n"
			+ "  * @css-for    IE 5.x/Win, IE6\n"
			+ "  * @valid      yes\n"
			+ "  */\n"
			+ "  * html body * { overflow:visible; }\n"
			+ "\n"
			+ "  body {\n"
			+ "    /* (en) Fix for rounding errors when scaling font sizes in older versions of Opera browser */\n"
			+ "    /* (de) Beseitigung von Rundungsfehler beim Skalieren von Schriftgrößen in älteren Opera Versionen */\n"
			+ "    font-size:100.01%;\n"
			+

			"    /* (en) Standard values for colors and text alignment */\n"
			+ "    /* (de) Vorgabe der Standardfarben und Textausrichtung */\n"
			+ "    background:#fff;\n"
			+ "    color:#000;\n"
			+ "    text-align:left; /* LTR */\n"
			+ "  }\n"
			+ "\n"
			+ "  /* (en) avoid visible outlines on DIV containers in Webkit browsers */\n"
			+ "  /* (de) Vermeidung sichtbarer Outline-Rahmen in Webkit-Browsern */\n"
			+ "  div { outline:0 none; }\n"
			+ "\n"
			+ "  /* (en) HTML 5 - adjusting visual formatting model to block level */\n"
			+ "  /* (en) HTML 5 - Anpassung des visuellen Formatmodells auf Blockelemente */\n"
			+ "  article,aside,canvas,details,figcaption,figure,\n"
			+ "  footer,header,hgroup,menu,nav,section,summary { \n"
			+ "  	display:block;\n"
			+ "  }\n"
			+ "  \n"
			+ "  /* (en) Clear borders for <fieldset> and <img> elements */\n"
			+ "  /* (de) Rahmen für <fieldset> und <img> Elemente löschen */\n"
			+ "  fieldset, img { border:0 solid; }\n"
			+ "\n"
			+ "  /* (en) new standard values for lists, blockquote and cite */\n"
			+ "  /* (de) Neue Standardwerte für Listen & Zitate */\n"
			+ "  ul, ol, dl { margin:0 0 1em 1em; } /* LTR */\n"
			+ "  li {\n"
			+ "    line-height:1.5em;\n"
			+ "    margin-left:0.8em; /* LTR */\n"
			+ "  }\n"
			+ "\n"
			+ "  dt { font-weight:bold; }\n"
			+ "  dd { margin:0 0 1em 0.8em; } /* LTR */\n"
			+ "\n"
			+ "  blockquote { margin:0 0 1em 0.8em; } /* LTR */\n"
			+ "\n"
			+ "  blockquote:before, blockquote:after,\n"
			+ "  q:before, q:after { content:\"\"; }\n"
			+ "\n"
			+ " /*------------------------------------------------------------------------------------------------------*/\n"
			+ "\n"
			+ " /**\n"
			+ "  * @section clearing methods\n"
			+ "  * @see     http://yaml.de/en/documentation/basics/general.html\n"
			+ "  */\n"
			+

			"  /* (en) clearfix method for clearing floats */\n"
			+ "  /* (de) Clearfix-Methode zum Clearen der Float-Umgebungen */\n"
			+ "  .clearfix:after {\n"
			+ "    clear:both;\n"
			+ "    content:\".\";\n"
			+ "    display:block;\n"
			+ "    font-size:0;\n"
			+ "    height:0;\n"
			+ "    visibility:hidden;\n"
			+ "  }\n"
			+ "\n"
			+ "  /* (en) essential for Safari browser !! */\n"
			+ "  /* (de) Diese Angabe benötigt der Safari-Browser zwingend !! */\n"
			+ "  .clearfix { display:block; }\n"
			+ "\n"
			+ "  /* (en) alternative solution to contain floats */\n"
			+ "  /* (de) Alternative Methode zum Einschließen von Float-Umgebungen */\n"
			+ "  .floatbox { display:table; width:100%; }\n"
			+ "\n"
			+ "  /* (en) IE-Clearing:Only used in Internet Explorer, switched on in iehacks.css */\n"
			+ "  /* (de) IE-Clearing:Benötigt nur der Internet Explorer und über iehacks.css zugeschaltet */\n"
			+ "  #ie_clearing { display:none; }\n"
			+ "\n"
			+ " /*------------------------------------------------------------------------------------------------------*/\n"
			+ "\n"
			+ " /**\n"
			+ "  * @section hidden elements | Versteckte Elemente\n"
			+ "  * @see     http://www.yaml.de/en/documentation/basics/skip-links.html\n"
			+ "  *\n"
			+ "  * (en) skip links and hidden content\n"
			+ "  * (de) Skip-Links und versteckte Inhalte\n"
			+ "  */\n"
			+ "\n"
			+ "  /* (en) classes for invisible elements in the base layout */\n"
			+ "  /* (de) Klassen für unsichtbare Elemente im Basislayout */\n"
			+ "  .skip, .hideme, .print {\n"
			+ "    position:absolute;\n"
			+ "    top:-32768px;\n"
			+ "    left:-32768px; /* LTR */\n"
			+ "  }\n"
			+ "\n"
			+ "  /* (en) make skip links visible when using tab navigation */\n"
			+ "  /* (de) Skip-Links für Tab-Navigation sichtbar schalten */\n"
			+ "  .skip:focus, .skip:active {\n"
			+ "    position:static;\n"
			+ "    top:0;\n"
			+ "    left:0;\n"
			+ "  }\n"
			+ "\n"
			+ "  /* skiplinks:technical setup */\n"
			+ "  #skiplinks { \n"
			+ "    position:absolute;\n"
			+ "    top:0px; \n"
			+ "    left:-32768px; \n"
			+ "    z-index:1000; \n"
			+ "    width:100%;\n"
			+ "    margin:0; \n"
			+ "    padding:0; \n"
			+ "    list-style-type:none;   \n"
			+ "  }\n"
			+ "  \n"
			+ "  #skiplinks .skip:focus,\n"
			+ "  #skiplinks .skip:active {\n"
			+ "    left:32768px; \n"
			+ "    outline:0 none;\n"
			+ "    position:absolute; \n"
			+ "    width:100%;\n"
			+ "  }  \n"
			+ "}\n"
			+ "\n"
			+ "@media screen, projection\n"
			+ "{\n"
			+ "\n"
			+ " /**\n"
			+ "  * @section base layout | Basis Layout\n"
			+ "  * @see     http://www.yaml.de/en/documentation/css-components/base-stylesheet.html\n"
			+ "  *\n"
			+ "  * |-------------------------------|\n"
			+ "  * | #col1   | #col3     | #col2   |\n"
			+ "  * | 20%     | flexible  | 20%     |\n"
			+ "  * |-------------------------------|\n"
			+ "  */\n"
			+ "\n"
			+ "  #col1 { float:left; width:20%; }\n"
			+ "  #col2 { float:right; width:20%; }\n"
			+ "  #col3 { width:auto; margin:0 20%; }\n"
			+ "\n"
			+ "  /* (en) Preparation for absolute positioning within content columns */\n"
			+ "  /* (de) Vorbereitung für absolute Positionierungen innerhalb der Inhaltsspalten */\n"
			+ "  #col1_content, #col2_content, #col3_content { position:relative; }\n"
			+ "\n"
			+ " /*------------------------------------------------------------------------------------------------------*/\n"
			+ "\n"
			+ " /**\n"
			+ "  * @section subtemplates\n"
			+ "  * @see     http://www.yaml.de/en/documentation/practice/subtemplates.html\n"
			+ "  */\n"
			+ "  .subcolumns { display:table; width:100%; table-layout:fixed; }\n"
			+ "  .subcolumns_oldgecko { width: 100%; float:left; }\n"
			+ "\n"
			+ "  .c20l, .c25l, .c33l, .c40l, .c38l, .c50l, .c60l, .c62l, .c66l, .c75l, .c80l { float:left; }\n"
			+ "  .c20r, .c25r, .c33r, .c40r, .c38r, .c50r, .c60r, .c66r, .c62r, .c75r, .c80r { float:right; margin-left:-5px; }\n"
			+ "\n"
			+ "  .c20l, .c20r { width:20%; }\n"
			+ "  .c40l, .c40r { width:40%; }\n"
			+ "  .c60l, .c60r { width:60%; }\n"
			+ "  .c80l, .c80r { width:80%; }\n"
			+ "  .c25l, .c25r { width:25%; }\n"
			+ "  .c33l, .c33r { width:33.333%; }\n"
			+ "  .c50l, .c50r { width:50%; }\n"
			+ "  .c66l, .c66r { width:66.666%; }\n"
			+ "  .c75l, .c75r { width:75%; }\n"
			+ "  .c38l, .c38r { width:38.2%; }\n"
			+ "  .c62l, .c62r { width:61.8%; }\n"
			+ "\n"
			+ "  .subc  { padding:0 0.5em; }\n"
			+ "  .subcl { padding:0 1em 0 0; }\n"
			+ "  .subcr { padding:0 0 0 1em; }\n"
			+ "\n"
			+ "  .equalize, .equalize .subcolumns { table-layout:fixed; }\n"
			+ "\n"
			+ "  .equalize > div {\n"
			+ "    display:table-cell;\n"
			+ "    float:none; \n"
			+ "    margin:0; \n"
			+ "    overflow:hidden;\n"
			+ "    vertical-align:top;\n"
			+ "  }\n"
			+ "}\n"
			+ "\n"
			+ "@media print\n"
			+ "{\n"
			+ " /**\n"
			+ "  * (en) float clearing for subtemplates. Uses display:table to avoid bugs in FF & IE\n"
			+ "  * (de) Float Clearing für Subtemplates. Verwendet display:table, um Darstellungsprobleme im FF & IE zu vermeiden\n"
			+ "  */\n"
			+ "\n"
			+ "  .subcolumns,\n"
			+ "  .subcolumns > div {\n"
			+ "    overflow:visible; \n"
			+ "    display:table;\n"
			+ "  } \n"
			+ "\n"
			+ "  /* (en) make .print class visible */\n"
			+ "  /* (de) .print-Klasse sichtbar schalten */\n"
			+ "  .print { \n"
			+ "    position:static; \n"
			+ "    left:0;\n"
			+ "  }\n"
			+ "\n"
			+ "  /* (en) generic class to hide elements for print */\n"
			+ "  /* (de) Allgemeine CSS Klasse, um beliebige Elemente in der Druckausgabe auszublenden */\n"
			+ "  .noprint { display:none !important; }\n" + "}\n";

	public static String basemodCSS = "/**\n"
			+ " * \"Yet Another Multicolumn Layout\" - (X)HTML/CSS framework\n"
			+ " * (en) stylesheet for screen layout\n"
			+ " * (de) Stylesheet für das Bildschirm-Layout\n"
			+ " *\n"
			+ " * @creator       YAML Builder V1.2.1 (http://builder.yaml.de)\n"
			+ " * @file          basemod.css\n"
			+ " * @-yaml-minver  3.3\n"
			+ " */\n"
			+ "\n"
			+ "@media screen, projection\n"
			+ "{\n"
			+ "  /** \n"
			+ "   * (en) Forcing vertical scrollbars in IE8, Firefox, Webkit & Opera \n"
			+ "   * (de) Erzwingen vertikaler Scrollbalken in IE8, Firefox, Webkit & Opera \n"
			+ "   *\n"
			+ "   * @workaround\n"
			+ "   * @affected IE8, FF, Webkit, Opera\n"
			+ "   * @css-for all\n"
			+ "   * @valid CSS3\n"
			+ "   */\n"
			+ "\n"
			+ "  body { overflow-y: scroll; }\n"
			+ "\n"
			+ "  /*-------------------------------------------------------------------------*/\n"
			+ "\n"
			+ "  /* (en) Marginal areas & page background */\n"
			+ "  /* (de) Randbereiche & Seitenhintergrund */\n"
			+ "  body { background: #4d87c7 repeat-x top left fixed; padding: 10px 0; }\n"
			+ "\n"
			+ "  /* Layout Alignment | Layout-Ausrichtung */\n"
			+ "  .page_margins { margin: 0 auto; }\n"
			+ "\n"
			+ "  /* Layout Properties | Layout-Eigenschaften */\n"
			+ "  .page_margins { width: auto;  min-width: 740px; max-width: 95%; background: #fff; }\n"
			+ "  .page { padding: 10px; }\n"
			+ "  #header { padding: 45px 2em 1em 20px; color: #000; background: #fff; }\n"
			+ "  #topnav { color: #aaa; background: transparent; }\n"
			+ "  #nav { overflow:hidden; }\n"
			+ "  #main { margin: 10px 0; background: #fff; }\n"
			+ "  #teaser { clear:both; padding: 0 20px; margin: 10px 0; }\n"
			+ "  #footer { padding: 10px 20px; color:#666; background: #f9f9f9; border-top: 5px #efefef solid; }\n"
			+ "\n"
			+ "  /* (en) navigation: horizontal adjustment | (de) horizontale Ausrichtung  */\n"
			+ "  #nav ul { margin-left: 20px; }\n"
			+ "\n"
			+ "  /*-------------------------------------------------------------------------*/\n"
			+ "\n"
			+ "  /**\n"
			+ "   * (en) Formatting content container\n"
			+ "   * (de) Formatierung der Inhalts-Container\n"
			+ "   *\n"
			+ "   */\n"
			+ "\n"
			+ "  #col1 { float: left; width: 25%}\n"
			+ "  #col2 { display:none}\n"
			+ "  #col3 { width: auto; margin: 0 0 0 25%}\n"
			+ "  #col1_content { padding: 0 10px 0 20px }\n"
			+ "  #col3_content { padding: 0 20px 0 10px }\n"
			+ "  #colleft_intern { padding: 0 1% 0 1%;\n"
			+ "  					margin: 0 0 0 0 ;\n"
			+ "  				    border: 2px solid #426FD9;\n"
			+ "  					float:left;\n"
			+ "  					width: 47%; }\n"
			+ "  #colright_intern { padding: 0 1% 0 1%;\n"
			+ "  					 margin:0 0 0 0 ;\n"
			+ "  				     border: 2px solid #426FD9;\n"
			+ "  					 float:right;\n"
			+ "  					 width: 47%; }\n"
			+ "  #coll_intern { padding: 0 1% 0 1%;\n"
			+ "  				 margin: 0;\n"
			+ "  				 border: 2px solid #426FD9;\n"
			+ "  				 float:left;\n"
			+ "  				 width: 98%; }\n"
			+ "\n"
			+ "  .CodeMirror-line-numbers {\n"
			+ "    width: 2.2em;\n"
			+ "    color: #aaa;\n"
			+ "    background-color: #eee;\n"
			+ "    text-align: right;\n"
			+ "    padding-right: .3em;\n"
			+ "    font-size: 10pt;\n"
			+ "    font-family: monospace;\n"
			+ "    padding-top: .4em;\n"
			+ "  }\n"
			+ "  body {\n"
			+ "      font-family: helvetica;\n"
			+ "      font-weight:bold;\n"
			+ "      max-width:4000px;\n"
			+ "  }\n"
			+ "  a {\n"
			+ "      color: #EB1D1D;\n"
			+ "      text-decoration: none;\n"
			+ "  }     \n"
			+ "  a:hover {\n"
			+ "      text-decoration: underline;\n"
			+ "  }\n"
			+ "  div.border {\n"
			+ "    border: 1px solid black;\n"
			+ "  }\n"
			+ "  .css-switch {\n"
			+ "      margin-right:15px;\n"
			+ "      padding-bottom:5px;\n"
			+ "  }\n"
			+ "	\n"
			+ "	\n"
			+ "  textarea { width:100%; }\n"
			+ "  .textwrapper {\n"
			+ "    margin: 0;\n"
			+ "    padding: 0; }\n"
			+ "\n"
			+ "\n"
			+ "\n"
			+ "  /* set column dividers */ \n"
			+ "  #col3 { border-left: 1px #ddd solid }\n"
			+ "	\n"
			+ "  /*-------------------------------------------------------------------------*/\n"
			+ "}\n";

	public static String contentCSS = "/**\n"
			+ " * \"Yet Another Multicolumn Layout\" - (X)HTML/CSS Framework\n"
			+ " *\n"
			+ " * (en) Uniform design of standard content elements\n"
			+ " * (de) Einheitliche Standardformatierungen für die wichtigten Inhalts-Elemente\n"
			+ " *\n"
			+ " * @copyright       Copyright 2005-2011, Dirk Jesse\n"
			+ " * @license         CC-A 2.0 (http://creativecommons.org/licenses/by/2.0/),\n"
			+ " *                  YAML-C (http://www.yaml.de/en/license/license-conditions.html)\n"
			+ " * @link            http://www.yaml.de\n"
			+ " * @package         yaml\n"
			+ " * @version         3.3.1\n"
			+ " * @revision        $Revision:392 $\n"
			+ " * @lastmodified    $Date:2009-07-05 12:18:40 +0200 (So, 05. Jul 2009) $\n"
			+ " * @appdef yaml\n"
			+ " */\n"
			+ "\n"
			+ "@media all\n"
			+ "{\n"
			+ " /**\n"
			+ "  * Fonts\n"
			+ "  *\n"
			+ "  * (en) global settings of font-families and font-sizes\n"
			+ "  * (de) Globale Einstellungen für Zeichensatz und Schriftgrößen\n"
			+ "  *\n"
			+ "  * @section content-global-settings\n"
			+ "  */\n"
			+ "\n"
			+ "  /* (en) reset font size for all elements to standard (16 Pixel) */\n"
			+ "  /* (de) Alle Schriftgrößen auf Standardgröße (16 Pixel) zurücksetzen */\n"
			+ "  html * { font-size:100.01%; }\n"
			+ "\n"
			+ " /**\n"
			+ "  * (en) reset monospaced elements to font size 16px in all browsers\n"
			+ "  * (de) Schriftgröße von monospaced Elemente in allen Browsern auf 16 Pixel setzen\n"
			+ "  *\n"
			+ "  * @see: http://webkit.org/blog/67/strange-medium/\n"
			+ "  */\n"
			+ "\n"
			+ "  textarea, pre, code, kbd, samp, var, tt {\n"
			+ "    font-family:Consolas, \"Lucida Console\", \"Andale Mono\", \"Bitstream Vera Sans Mono\", \"Courier New\", Courier;\n"
			+ "  }\n"
			+ "\n"
			+ "  /* (en) base layout gets standard font size 12px */\n"
			+ "  /* (de) Basis-Layout erhält Standardschriftgröße von 12 Pixeln */\n"
			+ "  body {\n"
			+ "    font-family:Arial, Helvetica, sans-serif;\n"
			+ "    font-size:75.00%;\n"
			+ "    color:#444;\n"
			+ "  }\n"
			+ "\n"
			+ "  /*--- Headings | Überschriften ------------------------------------------------------------------------*/\n"
			+ "\n"
			+ "  h1,h2,h3,h4,h5,h6 {\n"
			+ "    font-family:\"Times New Roman\", Times, serif;\n"
			+ "    font-weight:normal;\n"
			+ "    color:#222;\n"
			+ "    margin:0 0 0.25em 0;\n"
			+ "  }\n"
			+ "\n"
			+ "  h1 { font-size:250%; }                       /* 30px */\n"
			+ "  h2 { font-size:200%; }                       /* 24px */\n"
			+ "  h3 { font-size:150%; }                       /* 18px */\n"
			+ "  h4 { font-size:133.33%; }                    /* 16px */\n"
			+ "  h5 { font-size:116.67%; }                    /* 14px */\n"
			+ "  h6 { font-size:116.67%; }                    /* 14px */\n"
			+ "\n"
			+ "  /* --- Lists | Listen  -------------------------------------------------------------------------------- */\n"
			+ "\n"
			+ "  ul, ol, dl { line-height:1.5em; margin:0 0 1em 1em; }\n"
			+ "  ul { list-style-type:disc; }\n"
			+ "  ul ul { list-style-type:circle; margin-bottom:0; }\n"
			+ "\n"
			+ "  ol { list-style-type:decimal; }\n"
			+ "  ol ol { list-style-type:lower-latin; margin-bottom:0; }\n"
			+ "\n"
			+ "  li { margin-left:0.8em; line-height:1.5em; }\n"
			+ "\n"
			+ "  dt { font-weight:bold; }\n"
			+ "  dd { margin:0 0 1em 0.8em; }\n"
			+ "\n"
			+ "  /* --- general text formatting | Allgemeine Textauszeichnung ------------------------------------------ */\n"
			+ "\n"
			+ "  p { line-height:1.5em; margin:0 0 1em 0; }\n"
			+ "\n"
			+ "  blockquote, cite, q {\n"
			+ "    font-family:Georgia, \"Times New Roman\", Times, serif;\n"
			+ "    font-style:italic;\n"
			+ "  }\n"
			+ "  blockquote { margin:0 0 1em 1.6em; color:#666; }\n"
			+ "\n"
			+ "  strong,b { font-weight:bold; }\n"
			+ "  em,i { font-style:italic; }\n"
			+ "\n"
			+ "  big { font-size:116.667%; }\n"
			+ "  small { font-size:91.667%; }\n"
			+ " \n"
			+ "  pre { line-height:1.5em; margin:0 0 1em 0; }\n"
			+ "  pre, code, kbd, tt, samp, var { font-size:100%; }\n"
			+ "  pre, code { color:#800; }\n"
			+ "  kbd, samp, var, tt { color:#666; font-weight:bold; }\n"
			+ "  var, dfn { font-style:italic; }\n"
			+ "\n"
			+ "  acronym, abbr {\n"
			+ "    border-bottom:1px #aaa dotted;\n"
			+ "    font-variant:small-caps;\n"
			+ "    letter-spacing:.07em;\n"
			+ "    cursor:help;\n"
			+ "  }\n"
			+ "  \n"
			+ "  sub { vertical-align: sub; font-size: smaller; }\n"
			+ "  sup { vertical-align: super; font-size: smaller; }\n"
			+ "\n"
			+ "  hr {\n"
			+ "    color:#fff;\n"
			+ "    background:transparent;\n"
			+ "    margin:0 0 0.5em 0;\n"
			+ "    padding:0 0 0.5em 0;\n"
			+ "    border:0;\n"
			+ "    border-bottom:1px #eee solid;\n"
			+ "  }\n"
			+ "\n"
			+ "  /*--- Links ----------------------------------------------------------------------------------------- */\n"
			+ "\n"
			+ "  a { color:#4D87C7; background:transparent; text-decoration:none; }\n"
			+ "  a:visited  { color:#036; }\n"
			+ "\n"
			+ "  /* (en) maximum constrast for tab focus - change with great care */\n"
			+ "  /* (en) Maximaler Kontrast für Tab Focus - Ändern Sie diese Regel mit Bedacht */\n"
			+ "  a:focus { text-decoration:underline; color:#000; background: #fff; outline: 3px #f93 solid; }\n"
			+ "  a:hover,\n"
			+ "  a:active { color:#182E7A; text-decoration:underline; outline: 0 none; }\n"
			+ "\n"
			+ "  /* --- images (with optional captions) | Bilder (mit optionaler Bildunterschrift) ------------------ */\n"
			+ "\n"
			+ "  p.icaption_left { float:left; display:inline; margin:0 1em 0.15em 0; }\n"
			+ "  p.icaption_right { float:right; display:inline; margin:0 0 0.15em 1em; }\n"
			+ "\n"
			+ "  p.icaption_left img,\n"
			+ "  p.icaption_right img { padding:0; border:1px #888 solid; }\n"
			+ "\n"
			+ "  p.icaption_left strong,\n"
			+ "  p.icaption_right strong { display:block; overflow:hidden; margin-top:2px; padding:0.3em 0.5em; background:#eee; font-weight:normal; font-size:91.667%; }\n"
			+ "\n"
			+ " /**\n"
			+ "  * ------------------------------------------------------------------------------------------------- #\n"
			+ "  *\n"
			+ "  * Generic Content Classes\n"
			+ "  *\n"
			+ "  * (en) standard classes for positioning and highlighting\n"
			+ "  * (de) Standardklassen zur Positionierung und Hervorhebung\n"
			+ "  *\n"
			+ "  * @section content-generic-classes\n"
			+ "  */\n"
			+ "\n"
			+ "  .highlight { color:#c30; }\n"
			+ "  .dimmed { color:#888; }\n"
			+ "\n"
			+ "  .info { background:#f8f8f8; color:#666; padding:10px; margin-bottom:0.5em; font-size:91.7%; }\n"
			+ "\n"
			+ "  .note { background:#efe; color:#040; border:2px #484 solid; padding:10px; margin-bottom:1em; }\n"
			+ "  .important { background:#ffe; color:#440; border:2px #884 solid; padding:10px; margin-bottom:1em; }\n"
			+ "  .warning { background:#fee; color:#400; border:2px #844 solid; padding:10px; margin-bottom:1em; }\n"
			+ "\n"
			+ "  .float_left { float:left; display:inline; margin-right:1em; margin-bottom:0.15em; }\n"
			+ "  .float_right { float:right; display:inline; margin-left:1em; margin-bottom:0.15em; }\n"
			+ "  .center { display:block; text-align:center; margin:0.5em auto; }\n"
			+ "\n"
			+ " /**\n"
			+ "  * ------------------------------------------------------------------------------------------------- #\n"
			+ "  *\n"
			+ "  * Tables | Tabellen\n"
			+ "  *\n"
			+ "  * (en) Generic classes for table-width and design definition\n"
			+ "  * (de) Generische Klassen für die Tabellenbreite und Gestaltungsvorschriften für Tabellen\n"
			+ "  *\n"
			+ "  * @section content-tables\n"
			+ "  */\n"
			+ "\n"
			+ "  table { width:auto; border-collapse:collapse; margin-bottom:0.5em; border-top:2px #888 solid; border-bottom:2px #888 solid; }\n"
			+ "  table caption { font-variant:small-caps; }\n"
			+ "  table.full { width:100%; }\n"
			+ "  table.fixed { table-layout:fixed; }\n"
			+ "\n"
			+ "  th,td { padding:0.5em; }\n"
			+ "  thead th { color:#000; border-bottom:2px #800 solid; }\n"
			+ "  tbody th { background:#e0e0e0; color:#333; }\n"
			+ "  tbody th[scope=\"row\"], tbody th.sub { background:#f0f0f0; }\n"
			+ "\n"
			+ "  tbody th { border-bottom:1px solid #fff; text-align:left; }\n"
			+ "  tbody td { border-bottom:1px solid #eee; }\n" + "\n"
			+ "  tbody tr:hover th[scope=\"row\"],\n"
			+ "  tbody tr:hover tbody th.sub { background:#f0e8e8; }\n"
			+ "  tbody tr:hover td { background:#fff8f8; }\n" + "\n" + "}\n";

	public Generate(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		String app = ((Atomic) args[0]).atomize().stringValue().trim();
		String model = ((Atomic) args[1]).atomize().stringValue().trim();
		String base = String.format("%s/%s", HttpConnector.APPS_PATH, app);
		FileWriter f;
		BufferedWriter out;
		try {
			if (model.equals("MVC")) {
				new File(base).mkdir();
				genController(app, base);
				genModel(app, base);
				genView(app, base);
				genResources(app, base);
				genTemplate(app, base, false);
				HttpConnector.compileApplication(new File(String.format(
						"%s/%s", HttpConnector.APPS_PATH, app)));
				return Bool.TRUE;
			} else if (model.equals("REG")) {
				new File(base).mkdir();
				new File(String.format("%s/queries", base)).mkdir();
				f = new FileWriter(String.format("%s/queries/%sQuery.xq", base,
						app));
				out = new BufferedWriter(f);
				out.write(BSDLicense);
				out.write(String.format(importModule, "template", app, app,
						"Template"));
				out.write("\n");
				out.write(regBody);
				out.close();
				genResources(app, base);
				genTemplate(app, base, true);
				HttpConnector.compileApplication(new File(String.format(
						"%s/%s", HttpConnector.APPS_PATH, app)));
				return Bool.TRUE;
			} else {
				throw new QueryException(ASErrorCode.APP_GENERATE_INT_ERROR,
						"Application type not supported");
			}
		} catch (Exception e) {
			throw new QueryException(e, ASErrorCode.APP_GENERATE_INT_ERROR, e
					.getMessage());
		}
	}

	private void genTemplate(String app, String base, Boolean defTemp)
			throws IOException {
		FileWriter f;
		BufferedWriter out;
		f = new FileWriter(String.format("%s/templates/%sTemplate.xq", base,
				app));
		out = new BufferedWriter(f);
		out.write(BSDLicense);
		out.write(String.format(module, "template", app, app, "Template"));
		out.write("\n");
		out.write(String.format(tempHead, app, app));
		out.write("\n");
		out.write(tempHeader);
		out.write("\n");
		out.write(tempTeaser);
		out.write("\n");
		out.write(tempMenu);
		out.write("\n");
		out.write(tempFooterBrackit);
		out.write("\n");
		out.write(tempFooterYAML);
		out.write("\n");
		out.write(tempBaseBody);
		out.write("\n");
		out.write(tempBase);
		if (defTemp) {
			out.write("\n");
			out.write(tempRegBody);
		}
		out.close();
	}

	private void genResources(String app, String base) throws IOException {
		FileWriter f;
		BufferedWriter out;
		new File(String.format("%s/resources", base)).mkdir();
		new File(String.format("%s/resources/images", base)).mkdir();
		new File(String.format("%s/resources/css", base)).mkdir();
		new File(String.format("%s/resources/css/core", base)).mkdir();
		new File(String.format("%s/resources/css/screen", base)).mkdir();
		f = new FileWriter(String.format("%s/resources/css/%s.css", base, app));
		out = new BufferedWriter(f);
		out.write(mainCSS);
		out.close();
		f = new FileWriter(String
				.format("%s/resources/css/core/base.css", base));
		out = new BufferedWriter(f);
		out.write(baseCSS);
		out.close();
		f = new FileWriter(String.format("%s/resources/css/screen/basemod.css",
				base));
		out = new BufferedWriter(f);
		out.write(basemodCSS);
		out.close();
		f = new FileWriter(String.format("%s/resources/css/screen/content.css",
				base));
		out = new BufferedWriter(f);
		out.write(contentCSS);
		out.close();
		new File(String.format("%s/resources/js", base)).mkdir();
		new File(String.format("%s/templates", base)).mkdir();
	}

	private void genView(String app, String base) throws IOException {
		FileWriter f;
		BufferedWriter out;
		new File(String.format("%s/views", base)).mkdir();
		f = new FileWriter(String.format("%s/views/%sView.xq", base, app));
		out = new BufferedWriter(f);
		out.write(BSDLicense);
		out.write(String.format(module, "view", app, app, "View"));
		out
				.write(String.format(importModule, "template", app, app,
						"Template"));
		out.write("\n");
		out.write(viewBody);
		out.close();
	}

	private void genModel(String app, String base) throws IOException {
		FileWriter f;
		BufferedWriter out;
		new File(String.format("%s/models", base)).mkdir();
		f = new FileWriter(String.format("%s/models/%sModel.xq", base, app));
		out = new BufferedWriter(f);
		out.write(BSDLicense);
		out.write(String.format(module, "model", app, app, "Model"));
		out.close();
	}

	private void genController(String app, String base) throws IOException {
		FileWriter f;
		BufferedWriter out;
		new File(String.format("%s/controllers", base)).mkdir();
		f = new FileWriter(String.format("%s/controllers/%sController.xq",
				base, app));
		out = new BufferedWriter(f);
		out.write(BSDLicense);
		out.write(String.format(module, "controller", app, app, "Controller"));
		out.write(String.format(importModule, "model", app, app, "Model"));
		out.write(String.format(importModule, "view", app, app, "View"));
		out.write("\n");
		out.write(controllerBody);
		out.close();
	}
}