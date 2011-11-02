/*
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
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

import org.brackit.as.http.HttpConnector;
import org.brackit.as.xquery.ASErrorCode;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Bool;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.function.Signature;
import org.brackit.xquery.xdm.Sequence;

/**
 * 
 * @author Henrique Valer
 * 
 */
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

	public static String viewBody = "declare function default($content as item()) as item() {\n"
			+ "  template:base(template:head(),\n"
			+ "                template:header(),\n"
			+ "                template:teaser(),\n"
			+ "                template:menu(),\n"
			+ "                $content,\n"
			+ "                template:footerBrackit(),\n"
			+ "                template:footerYAML())\n" + "};\n";

	public static String controllerBody = "declare function index() as item() {\n"
			+ "  let $msg :=\n"
			+ "    \"Hello World!\"\n"
			+ "  return\n"
			+ "    view:default($msg)\n" + "};";

	public static String tempHead = 
		"declare function head() as item() {\n" +
		"  <head>\n" +
		"    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
		"    <link href=\"http://localhost:8080/apps/templateMVC/resources/css/layoutTemplateMVC.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
		"  </head>\n" +
		"};";

	public static String tempHeader = 
		"declare function header() as item() {\n" +
		"  <table style=\"width:100%;\">\n" +
		"    <tr>\n" +
		"      <td>\n" +
		"        <div id=\"header\" align=\"center\">\n" +
		"          <h1>Insert header here!</h1>\n" +
		"        </div>\n" +
		"      </td>\n" +
		"    </tr>\n" +
		"  </table>\n" +
		"};";
	
	public static String tempTeaser = 
		"declare function teaser() as item() {\n" +
		"  <div id=\"teaser\" align=\"center\">\n" +
		"    <h2>Insert teaser here!</h2>\n" +
		"  </div>\n" +
		"};";

	public static String tempMenu = 
		"declare function menu() as item() {\n" +
		"  <div id=\"col1_content\" class=\"clearfix\">\n" +
		"    <table style=\"width:100%;\">\n" +
		"      <tr>\n" +
		"        <td>\n" +
		"          <ul class=\"vlist\">\n" +
		"            <li><zu><a href=\"./index\"><h6>Link 1</h6></a></zu></li>\n" +
		"            <li><zu><a href=\"./index\"><h6>Link 2</h6></a></zu></li>\n" +
		"          </ul>\n" +
		"        </td>\n" +
		"      </tr>\n" +
		"    </table>\n" +
		"  </div>\n" +
		"};";

	public static String tempFooterBrackit = 
		"declare function footerBrackit() as item() {\n" +
		"  <div id=\"footer\">\n" +
		"    <a href=\"http://brackit.org\">Brackit XQuery engine</a>\n" +
		"  </div>\n" +
		"};";
	
	public static String tempFooterYAML = 
		"declare function footerYAML() as item() {\n" +
		"  <div id=\"footer\">\n" +
		"    Layout based on <a href=\"http://www.yaml.de/\">YAML</a>\n" +
		"  </div>\n" +
		"};";

	public static String tempBaseBody = 
		"declare function baseBody($header as item(),\n" +
		"                          $teaser as item(),\n" +
		"                          $menu as item(),\n" +
		"                          $content as item(),\n" +
		"                          $footerBrackit as item(),\n" +
		"                          $footerYAML as item()) as item() {\n" +
		"  <div class=\"page_margins\">\n" +
		"    <div class=\"page\">\n" +
		"      {$header}\n" +
		"      {$teaser}\n" +
		"      <div id=\"main\">\n" +
		"        <div id=\"col1\" role=\"complementary\">\n" +
		"          {$menu}\n" +
		"        </div>\n" +
		"        <div id=\"col3\">\n" +
		"          <div id=\"col3_content\" class=\"clearfix\">\n" +
		"            {$content}\n" +
		"          </div>\n" +
		"          <div id=\"ie_clearing\"/>\n" +
		"        </div>\n" +
		"      </div>\n" +
		"      {$footerBrackit}\n" +
		"      {$footerYAML}\n" +
		"    </div>\n" +
		"  </div>\n" +
		"};";
	
	public static String tempBase = 
		"declare function base($head as item(),\n" +
		"                      $header as item(),\n" +
		"                      $teaser as item(),\n" +
		"                      $menu as item(),\n" +
		"                      $content as item(),\n" +
		"                      $footerBrackit as item(),\n" +
		"                      $footerYAML as item()) {\n" +
		"  <html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n" +
		"    {$head}\n" +
		"    <body>\n" +
		"      {baseBody($header,$teaser,$menu,$content,$footerBrackit,$footerYAML)}\n" +
		"    </body>\n" +
		"  </html>\n" +
		"};";	

	public static String regBody = "let $msg :=\n" + "  \"Hello World!\"\n"
			+ "return\n" + "  view:default($msg)";

	public Generate(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(QueryContext ctx, Sequence[] args)
			throws QueryException {
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
				genResources(base);
				genTemplate(app, base);
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
				out.write(regBody);
				out.close();
				genResources(base);
				genTemplate(app, base);
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

	private void genTemplate(String app, String base) throws IOException {
		FileWriter f;
		BufferedWriter out;
		f = new FileWriter(String.format("%s/templates/%sTemplate.xq", base,
				app));
		out = new BufferedWriter(f);
		out.write(BSDLicense);
		out.write(String.format(module, "template", app, app, "Template"));
		out.write("\n");
		out.write(tempHead);
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
		out.close();
	}
	
	private void genResources(String base) {
		new File(String.format("%s/resources", base)).mkdir();
		new File(String.format("%s/resources/images", base)).mkdir();
		new File(String.format("%s/resources/css", base)).mkdir();
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
