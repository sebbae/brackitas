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
package org.brackit.as.xquery.function.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * 
 * @author Henrique Valer
 * 
 */
@SuppressWarnings("deprecation")
public class NullHttpSession implements HttpSession {

	private ArrayList<String> attributeNames;
	private Object attribute;
	private Long creationTime;
	private String Id;
	private Long accessedTime;
	private int maxInactiveInterval;
	private boolean active;

	public NullHttpSession() {
		this.active = true;
		this.attributeNames = new ArrayList<String>();
		this.creationTime = System.currentTimeMillis();
		this.accessedTime = this.creationTime;
		this.maxInactiveInterval = 50;
		this.attribute = new Object();
	}

	public Long getAccessedTime() {
		if (active) {
			Long a = this.accessedTime;
			setAccessedTime();
			return a;
		} else {
			return null;
		}
	}

	public void setAccessedTime() {
		this.accessedTime = System.currentTimeMillis();
	}

	public Object getAttribute() {
		if (active) {
			setAccessedTime();
			return this.attribute;
		} else {
			return null;
		}
	}

	public void setAttribute(Object attribute) {
		if (active) {
			setAccessedTime();
			this.attribute = attribute;
		}
	}

	public void setCreationTime(Long creationTime) {
		if (active) {
			setAccessedTime();
			this.creationTime = creationTime;
		}
	}

	public ArrayList<Object> GetAttributes() {
		if (active) {
			setAccessedTime();
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(this.attribute);
			return a;
		} else {
			return null;
		}

	}

	@Override
	public Object getAttribute(String name) {
		if (active) {
			return getAttribute();
		} else {
			return null;
		}
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		if (active) {
			return Collections.enumeration(this.attributeNames);
		} else {
			return null;
		}
	}

	@Override
	public long getCreationTime() {
		if (active) {
			return this.creationTime;
		} else {
			return 0;
		}
	}

	@Override
	public String getId() {
		if (active) {
			return this.Id;
		} else {
			return null;
		}
	}

	public void setId(String Id) {
		if (active) {
			this.Id = Id;
		}
	}

	@Override
	public long getLastAccessedTime() {
		if (active) {
			return getAccessedTime();
		} else {
			return 0;
		}
	}

	@Override
	public int getMaxInactiveInterval() {
		if (active) {
			return this.maxInactiveInterval;
		} else {
			return 0;
		}
	}

	@Override
	public void invalidate() {
		if (active) {
			this.active = false;
		}
	}

	@Override
	public void removeAttribute(String name) {
		if (active) {
			this.attribute = null;
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (active) {
			attributeNames.add(name);
			setAttribute(value);
		}
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		if (active) {
			this.maxInactiveInterval = interval;
		}
	}

	@Override
	public void removeValue(String name) {
	}

	@Override
	public void putValue(String name, Object value) {
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public Object getValue(String name) {
		return null;
	}

	@Override
	public String[] getValueNames() {
		return null;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}
}
