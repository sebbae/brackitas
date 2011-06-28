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
package org.brackit.as.xquery.function.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.servlet.http.HttpSession;

import org.brackit.as.Util.FunctionUtils;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.xquery.HttpSessionQueryContext;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.function.Signature;
import org.brackit.xquery.node.SubtreePrinter;
import org.brackit.xquery.xdm.Node;
import org.brackit.xquery.xdm.Sequence;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class Template extends AbstractFunction {

	public Template(QNm name, Signature signature) {
		super(name, signature, true);
		this.populateFields();
	}

	private String[] tempFields = new String[5];

	private FunctionUtils fUtils = new FunctionUtils();

	public String getTempField(int i) {
		return this.tempFields[i];
	}

	private void populateFields() {
		this.tempFields[0] = "content";
		this.tempFields[1] = "head";
		this.tempFields[2] = "header";
		this.tempFields[3] = "menu";
		this.tempFields[4] = "footer";
	}

	public Sequence execute(QueryContext ctx, Sequence[] args)
			throws QueryException {
		/**
		 * If the field is null, the template load a default file for it and
		 * executes it. If It's a file, it is loaded and executed Otherwise, the
		 * string is executed
		 * 
		 * TODO: Use app name, instead of eCommerce
		 * 
		 */

		String arg = null;
		String toBeEval = null;
		HttpSession httpSession = ((HttpSessionQueryContext) ctx)
				.getHttpSession();
		String appName = ((Atomic) httpSession.getAttribute("appName"))
				.stringValue();

		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Atomic) {
				arg = ((Atomic) args[i]).stringValue();
			} else {
				PrintStream buf = fUtils.createBuffer();
				SubtreePrinter.print((Node<?>) args[i], buf);
				arg = buf.toString();
			}
			if (arg.length() == 0) {
				toBeEval = "bit:eval(bit:loadFile('apps/" + appName
						+ "/views/default/" + getTempField(i) + ".xq'))";
			} else {
				try {
					FileInputStream in = new FileInputStream("apps/" + appName
							+ "/views/" + getTempField(i) + ".xq");
					toBeEval = "bit:eval(bit:loadFile('apps/" + appName
							+ "/views/" + getTempField(i) + ".xq'))";
				} catch (FileNotFoundException e) {
					toBeEval = arg;
				}
			}
			ctx.bind(new QNm(getTempField(i)), new Str(toBeEval));
		}
		// case of call only with content parameter
		if (args.length == 1) {
			for (int i = 1; i < this.tempFields.length; i++) {
				toBeEval = "bit:eval(bit:loadFile('apps/" + appName
						+ "/views/default/" + getTempField(i) + ".xq'))";
				ctx.bind(new QNm(getTempField(i)), new Str(toBeEval));
			}
		}
		File f = new File("apps/" + appName + "/views/default/template.xq");
		XQuery x = new ASXQuery(f);
		x.setPrettyPrint(true);
		return x.execute(ctx);
	}

}
