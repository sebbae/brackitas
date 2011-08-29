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
package org.brackit.as.http.uiOld;

import org.brackit.as.http.AbstractServlet;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class Helper extends AbstractServlet {

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public Helper() {
	}

	public static enum FILE_TYPE {
		XML, BLOB
	};

	public String LN = "\n";
	public String FORM_RESULT_AREA = "<textarea cols=\"100\" name=\"result\" rows=\"6\"/>";
	public String FORM_QUERY_AREA = "<textarea cols=\"100\" name=\"query\" rows=\"6\"/>";
	public String FORM_SECONDS_AREA = "<input type=\"hidden\" value=\"In ... seconds\"/>";
	public String FORM_PROCEDURE_SELECT = "<select id=\"select_procedure\" name=\"select_procedure\"/>";
	public String FORM_PROCEDURE_DIV_LIST = "<div id=\"selected_procedure_parameters\"/>";
	public String FORM_PROCEDURE_ONLOAD = "fieldsCountNumber";
	public String FORM_ERROR_MESSAGE = "ERROR_MSG";

	/**
	 * GetHtmlGraph return a HTML like graph for the given parameters.
	 * 
	 * @param dblMinVal
	 *            : Minimal value of the graphic.
	 * @param dblMaxVal
	 *            : Maximal value of the graphic.
	 * @param dblCurrentVal
	 *            : Current value of the graphic.
	 * @param Name
	 *            : Name of the graphic.
	 * @param booPercentView
	 *            : Show a percentage view or the whole graph.
	 * @return the HTML string corresponding to the graph.
	 * 
	 *         TODO: Improve method, including value exceptions (maximum smaller
	 *         then minimum, etc ..
	 * 
	 */
	public String getHtmlGraph(double dblMinVal, double dblMaxVal,
			double dblCurrentVal, String Name, boolean booPercentView) {

		String strReturn = null;
		double outMinVal = 0;
		double outMaxVal = 0;
		double outCurVal = 0;
		String outMinStr = null;
		String outMaxStr = null;
		String outCurStr = null;
		String outCurWidth = null;

		if (booPercentView) {
			// values been showed
			outMinVal = 0;
			outMaxVal = 100;
			outCurVal = ((dblCurrentVal - dblMinVal) * 100)
					/ (dblMaxVal - dblMinVal);

			// Displayed values
			outMinStr = Double.toString(outMinVal) + "%";
			outMaxStr = Double.toString(outMaxVal) + "%";
			outCurStr = Double.toString(outCurVal) + "%";

			outCurWidth = Double.toString(outCurVal) + "%";
		} else {
			// values been showed
			outMinVal = dblMinVal;
			outMaxVal = dblMaxVal;
			outCurVal = ((dblCurrentVal - dblMinVal) * 100)
					/ (dblMaxVal - dblMinVal);

			// displayed values
			outMinStr = Double.toString(dblMinVal);
			outMaxStr = Double.toString(dblMaxVal);
			outCurStr = Double.toString(dblCurrentVal);

			outCurWidth = Double.toString(outCurVal) + "%";
		}

		// Table with graphic statistic
		strReturn = Name
				+ " <BR /> "
				+ LN
				+ "<TABLE cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" align=\"left\" border=\"1\"> "
				+ LN + "	<TR> " + LN + "		<td align=\"left\" width=\""
				+ outCurWidth + "\">" + outMinStr + "</td> " + LN
				+ "		<td align=\"left\" width=\"1%\">" + outCurStr + "</td> "
				+ LN + "		<td align=\"right\" width=\"" + (100 - outCurVal - 1)
				+ "%\">" + outMaxStr + "</td> " + LN + "	</TR> " + LN
				+ "	<TR height=\"10\"> " + LN
				+ "		<TD bgcolor=\"magenta\" width=\"" + outCurWidth
				+ "\"></TD> " + LN + "		<TD colspan=\"2\" width=\""
				+ (100 - outCurVal) + "%\"></TD> " + LN + "	</TR> " + LN
				+ "</TABLE>" + LN + "<BR />";

		return strReturn;
	}

}
