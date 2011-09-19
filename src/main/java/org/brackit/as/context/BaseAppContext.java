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
package org.brackit.as.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.compiler.BaseResolver;
import org.brackit.xquery.module.LibraryModule;
import org.brackit.xquery.module.Module;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class BaseAppContext {

	private String app;

	private ASCompileChain chain;

	private Map<String, ASXQuery> queries;
	
	private List<String> uncompiledQueries;

	public BaseAppContext(String app, ASCompileChain chain) {
		this.queries = new HashMap<String, ASXQuery>();
		this.uncompiledQueries = new ArrayList<String>();
		this.app = app;
		this.chain = chain;
	}

	public void register(String path) throws QueryException {
		ASXQuery target = null;
		target = queries.get(path);
		if (target == null) {
			try{
				putQuery(path);
			} catch (QueryException e) {
				uncompiledQueries.add(path);
			}
		}
	}

	public void registerUncompiledQueries() throws QueryException {
		if (!uncompiledQueries.isEmpty()) {
			for (int i = uncompiledQueries.size(); i > 0; i--) {
				putQuery(uncompiledQueries.get(i-1));
			}
		}
	}
	
	private void putQuery (String path) throws QueryException {
		ASXQuery target = new ASXQuery(chain, getClass().getResourceAsStream(path));
		Module module = target.getModule();
		if (module instanceof LibraryModule)
			((BaseResolver) chain.getModuleResolver()).register(
					((LibraryModule) module).getTargetNS().getUri(),
					(LibraryModule) module);
		queries.put(path, target);
	}

	public ASXQuery get(String path) {
		return queries.get(path);
	}

	public ASCompileChain getChain() {
		return this.chain;
	}

	public String getApp() {
		return this.app;
	}

}