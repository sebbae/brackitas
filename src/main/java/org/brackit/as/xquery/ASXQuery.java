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
import java.io.InputStream;

import org.brackit.xquery.ErrorCode;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;

/**
 * @author Sebastian Baechle
 * @author Henrique Valer
 * 
 */
public class ASXQuery extends XQuery {

	public ASXQuery(InputStream in) throws QueryException {
		super(getStringFromInputStream(in));
	}

	private static String getStringFromInputStream(InputStream in)
			throws QueryException {

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
		} catch (IOException e) {
			throw new QueryException(e, ErrorCode.ERR_PARSING_ERROR, e
					.getMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ignored) {
				}
		}

		return out.toString();
	}

	public ASXQuery(String s) throws QueryException {
		super(s);
	}

	public ASXQuery(File f) throws QueryException {
		super(getStringFromFile(f));
	}

	private static String getStringFromFile(File pFile) throws QueryException {
		byte[] buffer = new byte[(int) pFile.length()];
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(pFile));
			in.read(buffer);
		} catch (IOException e) {
			throw new QueryException(e, ErrorCode.ERR_PARSING_ERROR, e
					.getMessage());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

}
