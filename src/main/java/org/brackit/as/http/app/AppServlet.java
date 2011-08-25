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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.TXServlet;
import org.brackit.as.util.FunctionUtils;
import org.brackit.server.ServerException;
import org.brackit.server.metadata.manager.impl.ItemNotFoundException;
import org.brackit.server.session.Session;
import org.brackit.server.tx.Tx;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class AppServlet extends TXServlet {

	protected static final String errorServ = "/app/error/";

	@Override
	public void init() throws ServletException {
		super.init();
		loadMimeTypes();
		createDefaultApplicationsBasic();
	};

	/**
	 * Create the application folders and the necessary extra folders for each
	 * applications
	 */
	private void createDefaultApplicationFiles(Session session) {
		Tx tx = null;
		try {
			tx = session.getTX();

			FunctionUtils fUtils = new FunctionUtils();

			// create a folder for each application
			List<File> folders = fUtils
					.getFoldersFileListing(new File("apps/"));
			for (File i : folders) {
				try {
					tx = session.getTX();
					metaDataMgr.mkdir(tx, i.getName());
					session.commit();

					// create the css files of the application
					// filter for css files
					FileFilter cssFileFilter = new FileFilter() {
						public boolean accept(File file) {
							return (file.getName().endsWith("css")) ? true
									: false;
						}
					};
					// create the image files of the application
					// filter for image files
					FileFilter imageFileFilter = new FileFilter() {
						public boolean accept(File file) {
							return ((file.getName().endsWith("jpg")) ? true
									: false || (file.getName().endsWith("png")) ? true
											: false || (file.getName()
													.endsWith("gif")) ? true
													: false);
						}
					};
					List<File> appFiles = fUtils.getFilteredFileListing(
							new File(i.getPath()), cssFileFilter);
					appFiles.addAll(fUtils.getFilteredFileListing(new File(i
							.getPath()), imageFileFilter));
					for (File f : appFiles) {
						metaDataMgr.create(tx, String.format("/%s/%s", i
								.getName(), f.getName()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			session.commit();

		} catch (Throwable e) {
			log.error(e);
			if (tx != null) {
				try {
					session.rollback();
				} catch (Throwable e1) {
					log.error(e1);
				}
			}
		} finally {
			if (session != null) {
				sessionMgr.logout(session.getSessionID());
			}
		}
	}

	/**
	 * Testing how to make multiple access at abstract servlet.
	 */
	private void createDefaultApplicationsBasic() {
		Session session = null;
		Tx tx = null;

		try {
			session = sessionMgr.getSession(sessionMgr.login());
			tx = session.getTX();
			metaDataMgr.mkdir(tx, "eCommerce");
			metaDataMgr.mkdir(tx, "eCommerce/items");
			session.commit();
		} catch (Throwable e) {
			log.error(e);
			if (tx != null) {
				try {
					session.rollback();
				} catch (Throwable e1) {
					log.error(e1);
				}
			}
		} finally {
			if (session != null) {
				sessionMgr.logout(session.getSessionID());
			}
		}
	}

	private void checkDefaultDocuments() {
		Session session = null;
		Tx tx = null;

		try {
			session = sessionMgr.getSession(sessionMgr.login());
			tx = session.getTX();
			try {
				metaDataMgr.getItem(tx, "/eCommerce.jpg");
			} catch (ItemNotFoundException e) {
				createDefaultApplicationFiles(session);
			}
			session.commit();
		} catch (Throwable e) {
			log.error(e);
			if (tx != null) {
				try {
					session.rollback();
				} catch (Throwable e1) {
					log.error(e1);
				}
			}
		} finally {
			if (session != null) {
				sessionMgr.logout(session.getSessionID());
			}
		}
	}

	private void loadMimeTypes() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream(
							"mime.types")));
			String strLine = null;
			while ((strLine = br.readLine()) != null) {
				mimeMap.addMimeTypes(strLine);
			}
			br.close();
		} catch (IOException e) {
			log.error("Could not load mime types", e);
		}
	}

	protected String getMimeType(String filename) {
		return mimeMap.getContentType(filename);
	}

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doGet(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doPost(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected final void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doPut(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected final void service(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			service(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	public final void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		super.service(arg0, arg1);
	}

	// /**
	// * Create the application folders and the necessary extra folders for each
	// applications
	// */
	// private void createDefaultApplicationFolders()
	// {
	// Session session = null;
	// Tx tx = null;
	//		
	// try
	// {
	// session = sessionMgr.getSession(sessionMgr.login());
	// tx = session.getTX();
	//
	// TXQueryContext ctx = new TXQueryContext(session.getTX(), metaDataMgr);
	// FunctionUtils fUtils = new FunctionUtils();
	// // filter for configuration application files
	// FileFilter appFileFilter = new FileFilter()
	// {
	// public boolean accept(File file)
	// {
	// return (file.getName().equals("appProperties.xml")) ? true : false;
	// }
	// };
	// String sessionAppName = null;
	//			
	// // create a folder for each application
	// List<File> folders = fUtils.getFoldersFileListing(new File("apps/"));
	// for (File i : folders)
	// {
	// try
	// {
	// // System.out.println(i.getPath());
	// metaDataMgr.mkdir(tx, i.getName());
	// sessionAppName = i.getName();
	//
	// // create the extra folders for each application
	// List<File> appFiles = fUtils.getFilteredFileListing(new
	// File(i.getPath()), appFileFilter);
	// ASXQuery xq = null;
	// File f = null;
	// for (File j : appFiles)
	// {
	// // stores the application configuration file
	// Collection<?> l = ctx.getStore().create(String.format("/%s/%s",
	// sessionAppName, j.getName(),new DocumentParser(j)));
	// //System.out.println(l.getName());
	//						
	// xq = new ASXQuery("let " +
	// "	$r := doc('" + l.getName() + "') " +
	// "return " +
	// "	for " +
	// "		$folders in $r/app/storageFolders/folderName" +
	// "	return" +
	// "		xtc:makeDirectory(concat('/','" + sessionAppName +
	// "','/',data($folders)))",metaDataMgr);
	//
	// xq.setPrettyPrint(true);
	// xq.serialize(ctx, System.out);
	//
	// xq = new ASXQuery("let " +
	// "	$r := doc('_master.xml') " +
	// "return" +
	// "	$r",metaDataMgr);
	// xq.setPrettyPrint(true);
	// xq.serialize(ctx, System.out);
	// }
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	//		
	// session.commit();
	//		
	// }
	// catch (Throwable e)
	// {
	// log.error(e);
	// if (tx != null)
	// {
	// try
	// {
	// session.rollback();
	// }
	// catch (Throwable e1)
	// {
	// log.error(e1);
	// }
	// }
	// }
	// finally
	// {
	// if (session != null)
	// {
	// sessionMgr.logout(session.getSessionID());
	// }
	// }
	// }

}
