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
package org.brackit.as.xquery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.brackit.as.xquery.function.bit.DeleteFile;
import org.brackit.as.xquery.function.bit.Eval;
import org.brackit.as.xquery.function.bit.LoadFile;
import org.brackit.as.xquery.function.bit.MakeDirectory;
import org.brackit.as.xquery.function.bit.StoreFile;
import org.brackit.as.xquery.function.http.GetSessionAtt;
import org.brackit.as.xquery.function.http.RemoveSessionAtt;
import org.brackit.as.xquery.function.http.SetSessionAtt;
import org.brackit.as.xquery.function.util.Template;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.xquery.compiler.DBCompiler;
import org.brackit.server.xquery.optimizer.DBOptimizer;
import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.compiler.optimizer.DefaultOptimizer;
import org.brackit.xquery.compiler.parser.ANTLRParser;
import org.brackit.xquery.compiler.translator.PipelineCompiler;
import org.brackit.xquery.function.Signature;
import org.brackit.xquery.module.Functions;
import org.brackit.xquery.module.Namespaces;
import org.brackit.xquery.sequence.type.AnyItemType;
import org.brackit.xquery.sequence.type.AtomicType;
import org.brackit.xquery.sequence.type.Cardinality;
import org.brackit.xquery.sequence.type.SequenceType;

/**
 * @author Sebastian Baechle
 * 
 */
public class ASXQuery extends XQuery {

	static {

		// Bit
		Functions.predefine(new DeleteFile(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "deleteFile"), new Signature(
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One))));

		Functions.predefine(new Eval(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "eval"), new Signature(new SequenceType(
				AtomicType.STR, Cardinality.ZeroOrOne), // result
				new SequenceType(AtomicType.STR, Cardinality.One)))); // query

		Functions.predefine(new LoadFile(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "loadFile"), new Signature(
				// file as string result
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One)))); // pathname

		Functions.predefine(new MakeDirectory(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "makeDirectory"), new Signature(
				// true: dir created, false otherwise
				new SequenceType(AtomicType.STR, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One)))); // pathname

		Functions.predefine(new StoreFile(new QNm(Namespaces.BIT_NSURI,
				Namespaces.BIT_PREFIX, "storeFile"), new Signature(
				new SequenceType(AtomicType.STR, Cardinality.ZeroOrOne),
				new SequenceType(AtomicType.STR, Cardinality.One), // doc name
				new SequenceType(AnyItemType.ANY, Cardinality.One))));

		// HTTP
		Functions.predefine(new SetSessionAtt(new QNm(Namespaces.BIT_NSURI,
				Namespaces.HTTP_PREFIX, "setSessionAtt"), new Signature(
				// output: true OK or exception
				new SequenceType(AtomicType.BOOL, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One), // att name
				new SequenceType(AnyItemType.ANY, Cardinality.One))));// attribute

		Functions.predefine(new GetSessionAtt(new QNm(Namespaces.BIT_NSURI,
				Namespaces.HTTP_PREFIX, "getSessionAtt"), new Signature(
				// output: true OK or exception
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One)))); // att
																		// name

		Functions.predefine(new RemoveSessionAtt(new QNm(Namespaces.BIT_NSURI,
				Namespaces.HTTP_PREFIX, "removeSessionAtt"), new Signature(
				// output: true OK or exception
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AtomicType.STR, Cardinality.One)))); // att
																		// name

		// Util
		Functions.predefine(new Template(new QNm(Namespaces.BIT_NSURI,
				Namespaces.UTIL_PREFIX, "template"), new Signature(
				// Result output template page
				new SequenceType(AnyItemType.ANY, Cardinality.One),
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne), // head
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne), // header
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne), // menu
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne), // content
				new SequenceType(AnyItemType.ANY, Cardinality.ZeroOrOne)))); // footer

		Functions.predefine(new Template(new QNm(Namespaces.BIT_NSURI,
				Namespaces.UTIL_PREFIX, "template"), new Signature(
				new SequenceType(AnyItemType.ANY, Cardinality.One), // result
				new SequenceType(AnyItemType.ANY, Cardinality.One)))); // content
	}

	public ASXQuery(String query, MetaDataMgr mdm) throws QueryException {
		super(query, new ANTLRParser(), new DBOptimizer(mdm), new DBCompiler());
	}
	
	/**
	 * Constructor receiving file directly, instead of pure XQuery as text.
	 * 
	 * @param pFile
	 * @throws QueryException
	 * @throws IOException
	 */
	public ASXQuery(File pFile, MetaDataMgr mdm) throws QueryException {
		super(pFile, new ANTLRParser(), new DBOptimizer(mdm), new DBCompiler());
	}

	public ASXQuery(File pFile) throws QueryException {
		super(pFile, new ANTLRParser(), new DefaultOptimizer(), new DBCompiler());
	}
	
	
}
