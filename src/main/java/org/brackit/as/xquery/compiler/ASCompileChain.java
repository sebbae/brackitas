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
package org.brackit.as.xquery.compiler;

import org.brackit.as.xquery.function.bit.DropCollection;
import org.brackit.as.xquery.function.bit.Eval;
import org.brackit.as.xquery.function.bit.FtIndexStore;
import org.brackit.as.xquery.function.bit.LoadFile;
import org.brackit.as.xquery.function.bit.MakeDirectory;
import org.brackit.as.xquery.function.bit.Render;
import org.brackit.as.xquery.function.bit.StoreDoc;
import org.brackit.as.xquery.function.session.Clear;
import org.brackit.as.xquery.function.session.GetAttributeNames;
import org.brackit.as.xquery.function.session.GetCreationTime;
import org.brackit.as.xquery.function.session.GetLastAccessedTime;
import org.brackit.as.xquery.function.session.GetMaxInactiveInterval;
import org.brackit.as.xquery.function.session.GetSessionAtt;
import org.brackit.as.xquery.function.session.Invalidate;
import org.brackit.as.xquery.function.session.RemoveSessionAtt;
import org.brackit.as.xquery.function.session.SetMaxInactiveInterval;
import org.brackit.as.xquery.function.session.SetSessionAtt;
import org.brackit.as.xquery.function.util.Template;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.tx.Tx;
import org.brackit.server.xquery.DBCompileChain;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.Signature;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.module.Namespaces;
import org.brackit.xquery.sequence.type.AnyItemType;
import org.brackit.xquery.sequence.type.AtomicType;
import org.brackit.xquery.sequence.type.Cardinality;
import org.brackit.xquery.sequence.type.SequenceType;

/**
 * @author Sebastian Baechle
 * @author Henrique Valer
 * 
 */
public class ASCompileChain extends DBCompileChain {

	public ASCompileChain(MetaDataMgr mdm, Tx tx) {
		super(mdm, tx);
	}

}
