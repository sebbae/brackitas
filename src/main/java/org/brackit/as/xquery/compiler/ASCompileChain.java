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
package org.brackit.as.xquery.compiler;

import org.brackit.as.xquery.function.app.AppFun;
import org.brackit.as.xquery.function.http.HttpFun;
import org.brackit.as.xquery.function.io.IOFunAS;
import org.brackit.as.xquery.function.request.RequestFun;
import org.brackit.as.xquery.function.resource.ResourceFun;
import org.brackit.as.xquery.function.session.SessionFun;
import org.brackit.as.xquery.function.util.UtilFun;
import org.brackit.as.xquery.function.xqfile.XqfileFun;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.tx.Tx;
import org.brackit.server.xquery.DBCompileChain;
import org.brackit.xquery.compiler.ModuleResolver;
import org.brackit.xquery.module.Namespaces;

/**
 * @author Sebastian Baechle
 * @author Henrique Valer
 * 
 */
public class ASCompileChain extends DBCompileChain {

	static {
		Namespaces.predefine(IOFunAS.IO_PREFIX, IOFunAS.IO_NSURI);
		Namespaces.predefine(UtilFun.UTIL_PREFIX, UtilFun.UTIL_NSURI);
		Namespaces.predefine(SessionFun.SESSION_PREFIX,
				SessionFun.SESSION_NSURI);
		Namespaces.predefine(RequestFun.REQUEST_PREFIX,
				RequestFun.REQUEST_NSURI);
		Namespaces.predefine(AppFun.APP_PREFIX, AppFun.APP_NSURI);
		Namespaces.predefine(XqfileFun.XQFILE_PREFIX, XqfileFun.XQFILE_NSURI);
		Namespaces.predefine(ResourceFun.RESOURCE_PREFIX,
				ResourceFun.RESOURCE_NSURI);
		Namespaces.predefine(HttpFun.HTTP_PREFIX, HttpFun.HTTP_NSURI);
	}

	private ASBaseResolver resolver;

	@Override
	public ModuleResolver getModuleResolver() {
		return this.resolver;
	}

	public ASCompileChain(MetaDataMgr mdm, Tx tx, ASBaseResolver res) {
		super(mdm, tx);
		this.resolver = res;
	}

	public ASCompileChain(MetaDataMgr mdm, Tx tx) {
		super(mdm, tx);
	}

	public ASBaseResolver getResolver() {
		return this.resolver;
	}
}