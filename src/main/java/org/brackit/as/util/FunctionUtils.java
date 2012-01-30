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
package org.brackit.as.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class FunctionUtils {

	public static PrintStream createBuffer() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return new PrintStream(out) {
			final OutputStream baos = out;

			public String toString() {
				return baos.toString();
			}
		};
	}

	/**
	 * Returns MD5 hash sequence for a given String input.
	 * 
	 * @param pInput
	 * @return
	 */
	public static String getMd5(String pInput) {
		try {
			MessageDigest lDigest;
			lDigest = MessageDigest.getInstance("MD5");
			lDigest.update(pInput.getBytes());
			BigInteger lHashInt = new BigInteger(1, lDigest.digest());
			return String.format("%1$032X", lHashInt);
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * Returns a list of files from a given folder, respecting the given filter.
	 * The list of files is extracted recursively.
	 * 
	 * @param dir
	 * @param fileFilter
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<File> getFilteredFileListing(File dir,
			FileFilter fileFilter) throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		List<File> filesDirs = Arrays.asList(dir.listFiles());

		for (File file : filesDirs) {
			if (file != null) {
				if (fileFilter.accept(file)) {
					result.add(file);
				}
			}
			if (file.isDirectory()) {
				result.addAll(getFilteredFileListing(file, fileFilter));
			}
		}
		return result;
	}

	/**
	 * Returns a list of folders from the given folder. The list is not
	 * recursive and removes svn folders from the result.
	 * 
	 * @param dir
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<File> getFoldersFileListing(File dir)
			throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		List<File> filesDirs = Arrays.asList(dir.listFiles());

		for (File file : filesDirs) {
			// remove svn files
			if ((file.isDirectory()) && (!file.getName().startsWith(".svn"))) {
				result.add(file);
			}
		}
		return result;
	}

	/**
	 * Returns the 'normalized' path from a file, i.e. the resulting path string
	 * contains only slashes as folder separators.
	 */
	public static String getNormalizedPath(File f) {
		return f.getPath().replaceAll(Pattern.quote(File.separator), "/");
	}
}
