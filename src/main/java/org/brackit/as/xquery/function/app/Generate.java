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

import org.brackit.as.context.BaseAppContext;
import org.brackit.as.http.HttpConnector;
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
				new File(String.format("%s/controllers", base)).mkdir();
				f = new FileWriter(String.format(
						"%s/controllers/%sController.xq", base, app));
				out = new BufferedWriter(f);
				out.write(BaseAppContext.BSDLicense);
				out.write(String.format(BaseAppContext.module, "controller",
						app, app, "Controller"));
				out.write(String.format(BaseAppContext.importModule, "model",
						app, app, "Model"));
				out.write(String.format(BaseAppContext.importModule, "view",
						app, app, "View"));
				out.close();
				new File(String.format("%s/models", base)).mkdir();
				f = new FileWriter(String.format("%s/models/%sModel.xq", base,
						app));
				out = new BufferedWriter(f);
				out.write(BaseAppContext.BSDLicense);
				out.write(String.format(BaseAppContext.module, "model", app,
						app, "Model"));
				out.close();
				new File(String.format("%s/resources", base)).mkdir();
				new File(String.format("%s/resources/images", base)).mkdir();
				new File(String.format("%s/resources/css", base)).mkdir();
				new File(String.format("%s/resources/js", base)).mkdir();
				new File(String.format("%s/templates", base)).mkdir();
				f = new FileWriter(String.format("%s/templates/%sTemplate.xq",
						base, app));
				out = new BufferedWriter(f);
				out.write(BaseAppContext.BSDLicense);
				out.write(String.format(BaseAppContext.module, "template", app,
						app, "Template"));
				out.close();
				new File(String.format("%s/views", base)).mkdir();
				f = new FileWriter(String.format("%s/views/%sView.xq", base,
						app));
				out = new BufferedWriter(f);
				out.write(BaseAppContext.BSDLicense);
				out.write(String.format(BaseAppContext.module, "view", app,
						app, "View"));
				out.write(String.format(BaseAppContext.importModule,
						"template", app, app, "Template"));
				out.close();
				HttpConnector.compileApplication(new File(String.format(
						"%s/%s", HttpConnector.APPS_PATH, app)));
				return Bool.TRUE;
			} else if (model.equals("REG")) {
				new File(base).mkdir();
				new File(String.format("%s/queries", base)).mkdir();
				f = new FileWriter(String.format("%s/queries/%sQuery.xq", base,
						app));
				out = new BufferedWriter(f);
				out.write(BaseAppContext.BSDLicense);
				out.write(String.format(BaseAppContext.importModule,
						"template", app, app, "Template"));
				out.write(BaseAppContext.todo);
				out.close();
				new File(String.format("%s/resources", base)).mkdir();
				new File(String.format("%s/resources/images", base)).mkdir();
				new File(String.format("%s/resources/css", base)).mkdir();
				new File(String.format("%s/resources/js", base)).mkdir();
				new File(String.format("%s/templates", base)).mkdir();
				f = new FileWriter(String.format("%s/templates/%sTemplate.xq",
						base, app));
				out = new BufferedWriter(f);
				out.write(BaseAppContext.BSDLicense);
				out.write(String.format(BaseAppContext.module, "template", app,
						app, "Template"));
				out.close();
				HttpConnector.compileApplication(new File(String.format(
						"%s/%s", HttpConnector.APPS_PATH, app)));
				return Bool.TRUE;
			} else {
				return Bool.FALSE;
			}
		} catch (Exception e) {
			return Bool.FALSE;
		}
	}
}
