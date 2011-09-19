package org.brackit.as.xquery.node;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.brackit.as.util.FunctionUtils;
import org.brackit.xquery.node.SubtreePrinter;
import org.brackit.xquery.xdm.DocumentException;
import org.brackit.xquery.xdm.Node;

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
	
	public StringBuffer getOut () {
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
		out.append(String.format("%s\n",node.getValue()));
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
