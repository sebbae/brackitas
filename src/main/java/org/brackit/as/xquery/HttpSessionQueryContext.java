package org.brackit.as.xquery;

import javax.servlet.http.HttpSession;

import org.brackit.xquery.QueryContext;

public class HttpSessionQueryContext extends QueryContext{

	private HttpSession httpSession;
	
	public HttpSession getHttpSession() {
		return httpSession;
	}

	public HttpSessionQueryContext (HttpSession httpSession) {
		super();
		this.httpSession = httpSession;
	}
}
