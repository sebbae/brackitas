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
package org.brackit.as.xquery.function.app;

import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.module.Namespaces;

public class AppFun {

	public static final String APP_NSURI = "http://brackit.org/ns/app";

	public static final String APP_PREFIX = "app";

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

}
