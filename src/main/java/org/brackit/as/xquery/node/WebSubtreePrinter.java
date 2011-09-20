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
package org.brackit.as.xquery.node;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.brackit.xquery.node.SubtreePrinter;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Node;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class WebSubtreePrinter extends SubtreePrinter {

	protected static final PrintStream createBuffer() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return new PrintStream(out) {
			final OutputStream baos = out;

			public String toString() {
				return baos.toString();
			}
		};
	}

	public WebSubtreePrinter() {
		super(new PrintWriter(createBuffer()));
		out = new StringBuffer();
	}

	protected StringBuffer out;

	public StringBuffer getOut() {
		return this.out;
	}

	@Override
	public void startDocument() throws DocumentException {
		this.level = 0;
		this.levelWithoutContent = -1;

		if (printXmlHead)
			out.append("&lt;?xml version=\"1.0\"?&gt;\n");

	}

	@Override
	public <T extends Node<?>> void endElement(T node) throws DocumentException {
		if (level == levelWithoutContent) {
			out.append("/&gt;");
			openElement = false;
			level--;
			levelWithoutContent = -1;
		} else {
			checkOpenElement();
			level--;
			indent();
			out.append("&lt;/");
			out.append(node.getName());
			out.append("&gt;");
		}
		if (prettyPrint) {
			out.append("\n");
		}
	}

	//	
	@Override
	public <T extends Node<?>> void startElement(T node)
			throws DocumentException {
		checkOpenElement();
		indent();
		out.append("&lt;");
		out.append(node.getName());
		openElement = true;
		level++;
		levelWithoutContent = level;
	}

	@Override
	public <T extends Node<?>> void comment(T node) throws DocumentException {
		checkOpenElement();
		indent();
		out.append("&lt;!-- ");
		if (prettyPrint) {
			out.append("\n");
		}
		indent();
		out.append(node.getValue());
		if (prettyPrint) {
			out.append("\n");
		}
		indent();
		out.append(" --&gt;");
		if (prettyPrint) {
			out.append("\n");
		}
		levelWithoutContent = -1;
	}

	@Override
	public <T extends Node<?>> void processingInstruction(T node)
			throws DocumentException {
		checkOpenElement();
		indent();
		out.append("&lt;? \n");
		out.append(String.format("%s\n", node.getValue()));
		out.append(" ?&gt;\n");
		levelWithoutContent = -1;
	}

	@Override
	protected void checkOpenElement() {
		if (openElement) {
			out.append("&gt;");
			openElement = false;
			if (prettyPrint) {
				out.append("\n");
			}
		}
	}
}
