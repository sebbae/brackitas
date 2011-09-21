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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

	private static class UncompiledQuery {

		private String path;

		private long lastModified;

		public UncompiledQuery(String p, long l) {
			this.path = p;
			this.lastModified = l;
		}

		public String getPath() {
			return path;
		}

		public long getLastModified() {
			return lastModified;
		}
	}

	private String app;

	private ASCompileChain chain;

	private Map<String, ASXQuery> queries;

	private List<UncompiledQuery> uncompiledQueries;

	public BaseAppContext(String app, ASCompileChain chain) {
		this.queries = new HashMap<String, ASXQuery>();
		this.uncompiledQueries = new ArrayList<UncompiledQuery>();
		this.app = app;
		this.chain = chain;
	}

	public void register(String path, long lastModified) {
		try {
			putQuery(path, lastModified);
		} catch (QueryException e) {
			uncompiledQueries.add(new UncompiledQuery(path, lastModified));
		}
	}

	public void registerUncompiledQueries() throws QueryException {
		if (!uncompiledQueries.isEmpty()) {
			Iterator<UncompiledQuery> i = uncompiledQueries.iterator();
			while (i.hasNext()) {
				UncompiledQuery uq = i.next();
				try {
					putQuery(uq.getPath(), uq.getLastModified());
					i.remove();
				} catch (QueryException e) {
					System.out.println(String.format(
							"Problems while compiling %s. %s", uq.getPath(), e
									.getMessage()));
				}
			}
		}
	}

	private void putQuery(String path, long lastModified) throws QueryException {
		ASXQuery target = new ASXQuery(chain, getClass().getResourceAsStream(
				path));
		Module module = target.getModule();
		if (module instanceof LibraryModule)
			((BaseResolver) chain.getModuleResolver()).register(
					((LibraryModule) module).getTargetNS().getUri(),
					(LibraryModule) module);
		target.setLastModified(lastModified);
		queries.put(path, target);
	}

	public ASXQuery get(String path) {
		// Check if query need to be recompiled
		File f = new File("src/main/resources" + path);
		ASXQuery x = queries.get(path);
		if (f.lastModified() != x.getLastModified()) {
			// recompile
			register(path, f.lastModified());
			// TODO: Might have to recompile import modules
		}
		return queries.get(path);
	}

	public ASCompileChain getChain() {
		return this.chain;
	}

	public String getApp() {
		return this.app;
	}

}