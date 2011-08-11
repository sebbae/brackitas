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
package org.brackit.as.xquery.function.session;

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
public class NullHttpSession implements HttpSession {

	private ArrayList<String> attributeNames;
	private Object attribute;
	private Long creationTime;
	private String Id;
	private Long accessedTime; 
	private int maxInactiveInterval;
	private boolean active;
	
	public NullHttpSession (){
		this.active = true;
	}
	
	public Long getAccessedTime() {
		return accessedTime;
	}

	public void setAccessedTime(Long accessedTime) {
		this.accessedTime = accessedTime;
	}

	public Object getAttribute() {
		return attribute;
	}

	public void setAttribute(Object attribute) {
		this.attribute = attribute;
	}

	public void setAttributeNames(ArrayList<String> attributeNames) {
		this.attributeNames = attributeNames;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public ArrayList<String> GetAttributes(){
		return this.attributeNames;
	}
	
	@Override
	public Object getAttribute(String name) {
		return this.attribute; 
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		try {
			return Collections.enumeration(GetAttributes());
		}catch (Exception e) {
			//TODO: Erase and use log4j
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		try {
			return this.creationTime;
		}catch (Exception e) {
			//TODO: Erase and use log4j
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String getId() {
		return this.Id;
	}
	
	public void setId(String Id) {
		this.Id = Id;
	}	

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return getAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return this.maxInactiveInterval;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		this.active = false;
	}

	@Override
	public void removeAttribute(String name) {
		this.attribute = null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attribute = value;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}
	
	@Override
	public void removeValue(String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void putValue(String name, Object value) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public Object getValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}	
}
