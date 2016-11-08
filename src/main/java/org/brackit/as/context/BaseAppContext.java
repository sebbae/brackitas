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
package org.brackit.as.context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.brackit.as.http.HttpConnector;
import org.brackit.as.xquery.ASUncompiledQuery;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.compiler.ASBaseResolver;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.module.LibraryModule;
import org.brackit.xquery.module.Module;
import org.brackit.xquery.util.io.IOUtils;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class BaseAppContext {

	private String app;

	private ASCompileChain chain;

	private Map<String, String> libraries;

	private Map<String, ASXQuery> queries;

	private List<ASUncompiledQuery> uncompiledQueries;

	private boolean running;

	public List<ASUncompiledQuery> getUncompiledQueries() {
		return this.uncompiledQueries;
	}

	public ASCompileChain getChain() {
		return this.chain;
	}

	public String getApp() {
		return this.app;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void terminate() {
		this.running = false;
	}

	public BaseAppContext(String app, ASCompileChain chain) {
		this.libraries = new HashMap<String, String>();
		this.queries = new HashMap<String, ASXQuery>();
		this.uncompiledQueries = new ArrayList<ASUncompiledQuery>();
		this.app = app;
		this.chain = chain;
		this.running = true;
	}

	public void register(String path, long lastModified) {
		try {
			putQuery(path, lastModified);
		} catch (QueryException e) {
			uncompiledQueries.add(new ASUncompiledQuery(path, lastModified, e));
		}
	}

	public void unregister(String path) {
		if (queries != null) {
			queries.remove(path);
		}
	}

	/**
	 * TODO: Create better compilation mechanism.
	 * 
	 * @throws QueryException
	 */
	public void registerUncompiledQueries() throws QueryException {
		if (!uncompiledQueries.isEmpty()) {
			Iterator<ASUncompiledQuery> i = uncompiledQueries.iterator();
			while (i.hasNext()) {
				ASUncompiledQuery uq = i.next();
				try {
					putQuery(uq.getPath(), uq.getLastModified());
					i.remove();
				} catch (QueryException e) {
				}
			}
			i = uncompiledQueries.iterator();
			while (i.hasNext()) {
				ASUncompiledQuery uq = i.next();
				try {
					putQuery(uq.getPath(), uq.getLastModified());
					i.remove();
				} catch (QueryException e) {
				}
			}
			i = uncompiledQueries.iterator();
			while (i.hasNext()) {
				ASUncompiledQuery uq = i.next();
				try {
					putQuery(uq.getPath(), uq.getLastModified());
					i.remove();
				} catch (QueryException e) {
					// System.out.println(String.format(
					// "Problems compiling: %s. \n %s \n", uq.getPath(), e
					// .getMessage()));
					uq.setE(e);
				}
			}
		}
	}

	private void putQuery(String path, long lastModified) throws QueryException {
		File f = new File(path);
		ASXQuery target = new ASXQuery(chain, f);
		target.setLastModified(lastModified);
		Module module = target.getModule();
		if (module instanceof LibraryModule) {
			String uri = ((LibraryModule) module).getTargetNS();
			libraries.put(uri, path);
		}
		queries.put(path, target);
	}

	public ASXQuery get(String path) throws Exception {
		path = (path.startsWith("/")) ? path.substring(1) : path;
		File f = new File(String.format("%s/%s", HttpConnector.APPS_PATH, path));
		path = IOUtils.getNormalizedPath(f);
		ASXQuery x = queries.get(path);
		if (x != null) {
			if (f.lastModified() != x.getLastModified()) {
				register(path, f.lastModified());
			}
			// Check if imported modules need to be recompiled
			Iterator<Module> i = x.getModule().getImportedModules().iterator();
			while (i.hasNext()) {
				Module m = i.next();
				String mPath = libraries.get(m.getTargetNS());
				File fm = new File(mPath);
				ASXQuery qm = queries.get(mPath);
				if (fm.lastModified() != qm.getLastModified()) {
					chain.getResolver().unregister(qm.getModule().getTargetNS());
					register(mPath, fm.lastModified());
					register(path, f.lastModified());
				}
			}
			return queries.get(path);
		} else
			return x;
	}
}