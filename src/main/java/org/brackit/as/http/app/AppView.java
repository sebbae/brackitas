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
package org.brackit.as.http.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;

import org.brackit.as.http.HttpConnector;
import org.brackit.as.util.TemplateTreeNode;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.xquery.HttpSessionQueryContext;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Str;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class AppView extends AppServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			org.brackit.server.session.Session session) throws Exception {
		ServletContext context = getServletContext();
		try {
			String[] URI = req.getRequestURI().split("/");

			// TODO: Improve viewname get mechanism
			String viewName = URI[3];

			// Create tree with folder template structure
			DefaultMutableTreeNode tree = getFolderTree("apps/helloWorld/views/template/");

			HttpSessionQueryContext ctx = new HttpSessionQueryContext(req
					.getSession());
			ctx.getHttpSession().setAttribute("view", viewName);
			ctx.getHttpSession().setAttribute("viewTree", tree);
			File f = new File("apps/helloWorld/views/template/index.xq");
			XQuery x = new ASXQuery(f);
			x.setPrettyPrint(true);
			x.serialize(ctx, new PrintStream(resp.getOutputStream()));
		} catch (Exception e) {
			req.setAttribute("errorMsg", e.getMessage());
			RequestDispatcher dispatcher = context
					.getRequestDispatcher("/app/error/");
			dispatcher.forward(req, resp);
		}
	};

	/**
	 * Idea: implement a tree where each node is a list of files that are on the
	 * folder.
	 * 
	 * @param rootPath
	 * @return
	 * @throws Exception
	 */
	private DefaultMutableTreeNode getFolderTree(String rootPath)
			throws Exception {
		DefaultMutableTreeNode tree = null;
		File f1 = new File(rootPath);
		DefaultMutableTreeNode newTree = null;

		if (f1.isDirectory()) {
			System.out.println("Directory of " + f1.getName() + " PATH: "
					+ f1.getPath());
			tree = new DefaultMutableTreeNode(new TemplateTreeNode(f1));
			newTree = new DefaultMutableTreeNode(new TemplateTreeNode(f1));
			newTree.add(populateFolderTree(f1, tree));
		} else {
			throw new Exception("Cannot generate tree from a single file");
		}

		return newTree;
	}

	private DefaultMutableTreeNode populateFolderTree(File f1,
			DefaultMutableTreeNode tree) throws Exception {
		File[] subF = f1.listFiles();
		for (int i = 0; i < subF.length; i++) {
			if (subF[i].isDirectory()) {
				System.out.println("Directory2 of " + subF[i].getName()
						+ " PATH: " + subF[i].getPath());
				tree.add(new DefaultMutableTreeNode(new TemplateTreeNode(
						subF[i])));
				populateFolderTree(subF[i], ((DefaultMutableTreeNode) tree
						.getLastChild()));
			} else {
				System.out.println("File of " + subF[i].getName() + " PATH: "
						+ subF[i].getPath());
				((TemplateTreeNode) tree.getUserObject()).setFiles(subF[i]);
			}
		}

		return tree;
	}

}
