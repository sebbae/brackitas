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
import java.util.Collections;
import java.util.Iterator;

import org.brackit.as.annotation.FunctionAnnotation;
import org.brackit.as.annotation.ModuleAnnotation;
import org.brackit.as.xquery.xdm.ComparableFunction;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Function;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.type.SequenceType;

/**
 * @author Roxana Zapata
 * @author Henrique Valer
 * 
 */
@ModuleAnnotation(description = "A module for extra utility functions.")
@FunctionAnnotation(description = "List all predefined functions of a given module. \n"
		+ "It returns an XML document with an element for each function. "
		+ "Each of these elements contains the name function, URI, description "
		+ " and signature of the given function.", parameters = "$moduleName")
public class ListPredefinedFunctions extends AbstractFunction {

	public ListPredefinedFunctions(QNm name, Signature signature) {
		super(name, signature, true);
	}

	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		String module = ((Atomic) args[0]).stringValue().trim();
		ArrayList<ComparableFunction> results = getPredefinedFunctions(module);
		Collections.sort(results);
		SequenceType[] params;
		if (results.size() > 0) {
			String result = "<module name=\"" + module + "\">";
			for (int i = 0; i < results.size(); i++) {
				if (results.get(i).getF().getName() instanceof QNm) {
					String description;
					String[] parameters = null;
					FunctionAnnotation annotation = results.get(i).getF()
							.getClass().getAnnotation(FunctionAnnotation.class);
					if (annotation != null) {
						description = annotation.description();
						parameters = annotation.parameters();
					} else {
						description = "No description present";
					}
					result += "<function>";
					result += "<name>"
							+ results.get(i).getF().getName().localName
							+ "</name>";
					result += "<nsURI>" + results.get(i).getF().getName().nsURI
							+ "</nsURI>";
					result += "<description>" + description + "</description>";
					result += "<signature>";
					result += "<return>";
					result += results.get(i).getF().getSignature()
							.getResultType();
					result += "</return>";
					result += "<parameters>";
					params = results.get(i).getF().getSignature().getParams();
					for (int j = 0; j < params.length; j++) {
						if (parameters == null) {
							result += "<parameter>"
									+ params[j].getItemType().toString()
									+ "</parameter>";
						} else {
							result += "<parameter description= \""
									+ annotation.parameters()[j] + "\">"
									+ params[j].getItemType().toString()
									+ "</parameter>";
						}
					}
					result += "</parameters>";
					result += "</signature>";
					result += "</function>";
				}
			}
			result += "</module>";
			XQuery xquery = new XQuery(result);
			return xquery.execute(ctx);
		}
		XQuery xquery = new XQuery("<module> no functions found! </module>");
		return xquery.execute(ctx);
	}

	private ArrayList<ComparableFunction> getPredefinedFunctions(String module) {
		ArrayList<ComparableFunction> result = new ArrayList<ComparableFunction>();
		Iterator<Function[]> i = new Functions().getPredefinedFunctions()
				.values().iterator();
		while (i.hasNext()) {
			Function[] f = i.next();
			for (int j = 0; j < f.length; j++) {
				if (f[j].getName().prefix.equals(module)) {
					result.add(new ComparableFunction(f[j]));
				}
			}
		}
		return result;
	}
}