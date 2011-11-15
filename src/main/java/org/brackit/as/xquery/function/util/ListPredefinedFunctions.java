/*
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of the <organization> nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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

import java.util.ArrayList;
import java.util.Iterator;

import org.brackit.as.annotation.FunctionAnnotation;
import org.brackit.as.annotation.ModuleAnnotation;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Function;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.type.SequenceType;

/**
 * @author Roxana Zapata
 * 
 */
@ModuleAnnotation(description = "Util module." )
@FunctionAnnotation(description = "List all predefined functions.", parameters = "")
public class ListPredefinedFunctions extends AbstractFunction {

	public ListPredefinedFunctions(QNm name, Signature signature) {
		super(name, signature, true);
	}

	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {

		String module = ((Str) args[0]).stringValue().trim();

		ArrayList<Function> results = getPredefinedFunctions(module);

		SequenceType[] params;

		if (results.size() > 0) {
			String result = "<Module name= \"" + module + "\">";

			for (int i = 0; i < results.size(); i++) {

				if (results.get(i).getName() instanceof QNm) {

					String description;
					String[] parameters = null;
					FunctionAnnotation annotation = results.get(i).getClass()
							.getAnnotation(FunctionAnnotation.class);
					if (annotation != null) {
						description = annotation.description();
						parameters = annotation.parameters();
					} else {
						description = "No description present";
					}

					result += "<Function>";
					result += "<Name>" + results.get(i).getName().localName
							+ "</Name>";
					result += "<NsURI>" + results.get(i).getName().nsURI
							+ "</NsURI>";

					result += "<Description>" + description + "</Description>";

					result += "<Signature>";

					result += "<Return>";

					result += results.get(i).getSignature().getResultType();

					result += "</Return>";

					result += "<Parameters>";

					params = results.get(i).getSignature().getParams();

					for (int j = 0; j < params.length; j++) {

						if (parameters == null) {

							result += "<Parameter>"
									+ params[j].getItemType().toString()
									+ "</Parameter>";

						} else {
							result += "<Parameter description= \""
									+ annotation.parameters()[j] + "\">"
									+ params[j].getItemType().toString()
									+ "</Parameter>";
						}

					}
					result += "</Parameters>";
					result += "</Signature>";
					result += "</Function>";
				}
			}
			result += "</Module>";

			XQuery xquery = new XQuery(result);

			return xquery.execute(ctx);
		}
		XQuery xquery = new XQuery("<Module> no functions found! </Module>");

		return xquery.execute(ctx);

	}

	private ArrayList<Function> getPredefinedFunctions(String module) {

		ArrayList<Function> result = new ArrayList<Function>();
		
		Iterator<Function[]> i = Functions.getPredefinedFunctions().values().iterator();

		while (i.hasNext()) {
			Function[] f = i.next();
			for (int j = 0; j < f.length; j++) {
				if (f[j].getName().prefix.equals(module)) {
					result.add(f[j]);
				}
			}
		}
		return result;
	}

}
