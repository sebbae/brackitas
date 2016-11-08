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
package org.brackit.as.xquery;

import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.compiler.Bits;
import org.brackit.xquery.module.Namespaces;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class ASErrorCode extends ErrorCode {

	/**
	 * Errors for the predefined application functions
	 */
	public static final QNm APP_DELETE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0001");

	public static final QNm APP_DEPLOY_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0002");

	public static final QNm APP_EXISTS_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0003");

	public static final QNm APP_GENERATE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0004");

	public static final QNm APP_GETNAMES_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0005");

	public static final QNm APP_GETSTRUCTURE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0006");

	public static final QNm APP_ISRUNNING_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0007");

	public static final QNm APP_TERMINATE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "APP0008");

	/**
	 * Errors for the predefined bit functions
	 */
	public static final QNm BIT_ADDTOCOLLECTION_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0001");

	public static final QNm BIT_CREATECOLLECTION_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0002");

	public static final QNm BIT_DROPCOLLECTION_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0003");

	public static final QNm BIT_EVAL_INT_ERROR = new QNm(Bits.BIT_NSURI,
			Bits.BIT_PREFIX, "BIT0004");

	public static final QNm BIT_EXISTCOLLECTION_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0005");

	public static final QNm BIT_LOADFILE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0006");

	public static final QNm BIT_MAKEDIRECTORY_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0007");

	public static final QNm BIT_STOREDOC_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0008");

	/**
	 * Errors for the predefined request functions
	 */
	public static final QNm REQ_GETCOOKIE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0001");

	public static final QNm REQ_GETCOOKIENAMES_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0002");

	public static final QNm REQ_GETPARAMETER_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0003");

	public static final QNm REQ_GETPARAMETERNAMES_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0004");

	public static final QNm REQ_GETATTRIBUTE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0005");

	public static final QNm REQ_GETATTRIBUTENAMES_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0006");

	public static final QNm REQ_ISMULTIPARTCONTENT_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "BIT0007");

	/**
	 * Errors for the predefined session functions
	 */
	public static final QNm SESSION_CLEAR_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0001");

	public static final QNm SESSION_GETATTRIBUTE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0002");

	public static final QNm SESSION_GETATTRIBUTENAMES_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0003");

	public static final QNm SESSION_GETCREATIONTIME_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0004");

	public static final QNm SESSION_GETLASTACCESSEDTIME_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0005");

	public static final QNm SESSION_GETMAXINACTIVEINTERVAL_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0006");

	public static final QNm SESSION_INVALIDATE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0007");

	public static final QNm SESSION_REMOVEATTRIBUTE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0008");

	public static final QNm SESSION_SETATTRIBUTE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0009");

	public static final QNm SESSION_SETMAXINACTIVEINTERVAL_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "SESSION0010");

	/**
	 * Errors for the predefined util functions
	 */
	public static final QNm UTIL_MKDIRECTORY_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "UTIL0001");

	public static final QNm UTIL_PLAINPRINT_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "UTIL0002");

	public static final QNm UTIL_LISTPREDEFINEDFUNCTIONS_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "UTIL0003");

	public static final QNm UTIL_LISTPREDEFINEDMODULES_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "UTIL0004");

	public static final QNm UTIL_GETMIMETYPE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "UTIL0005");

	/**
	 * Errors for the predefined xqfile functions
	 */
	public static final QNm XQFILE_COMPILE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "XQFILE0001");

	public static final QNm XQFILE_CREATE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "XQFILE0002");

	public static final QNm XQFILE_DELETE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "XQFILE0003");

	public static final QNm XQFILE_SAVE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "XQFILE0004");

	public static final QNm XQFILE_ISMODULE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "XQFILE0005");

	public static final QNm XQFILE_GETCOMPILATIONERROR_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "XQFILE0006");

	/**
	 * Errors for resource handling functions
	 */
	public static final QNm RSC_DELETE_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "RSC0001");

	public static final QNm RSC_RENAME_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "RSC0002");

	public static final QNm RSC_UPLOAD_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "RSC0003");

	/**
	 * Errors for HTTP functions
	 */
	public static final QNm HTTP_SENDREQUEST_INT_ERROR = new QNm(
			Namespaces.ERR_NSURI, Namespaces.ERR_PREFIX, "HTTP0001");

}