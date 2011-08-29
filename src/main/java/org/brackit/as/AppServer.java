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
package org.brackit.as;

import org.brackit.as.http.HttpConnector;
import org.brackit.as.http.HttpConnectorOld;
import org.brackit.server.BrackitDB;
import org.brackit.server.ServerException;

/**
 * 
 * @author Sebastian Baechle
 * 
 */
public class AppServer {

	private class ShutdownThread extends Thread {
		@Override
		public void run() {
			System.out.print("Shutdown HTTP interface ... ");
			try {
				connector.stop();
			} catch (Exception e) {
				System.out.print("failed: ");
				System.out.println(e.getMessage());
			}
			System.out.println("done.");
			System.out.print("Shutdown DB ... ");
			try {
				db.shutdown();
			} catch (ServerException e) {
				System.out.print("failed: ");
				System.out.println(e.getMessage());
			}
			System.out.println("done.");
		}
	}

	private BrackitDB db;

//	private HttpConnectorOld connector;
	private HttpConnector connector;

	public AppServer(boolean install) throws Exception {
		startDB(install);
		startHTTP(8080);
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());
	}

	private void startHTTP(int port) throws Exception {
		System.out.print("Start HTTP interface on port ");
		System.out.println(port);
		System.out.print(" ... ");
		try {
			connector = new HttpConnector(db.getMetadataMgr(), db
					.getSessionMgr(), port);
			connector.start();
		} catch (Exception e) {
			System.out.print("failed: ");
			System.out.println(e.getMessage());
		}
		System.out.println("done.");
	}

	private void startDB(boolean install) throws ServerException {
		if (install) {
			System.out.print("Install DB ... ");

		} else {
			System.out.print("Start DB ... ");
		}
		try {
			db = new BrackitDB(install);
		} catch (ServerException e) {
			System.out.print("failed: ");
			System.out.println(e.getMessage());
			throw e;
		}
		System.out.println("done.");
	}

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				illegalArguments();
			} else if ("install".equals(args[0].toLowerCase())) {
				new AppServer(true);
			} else if ("start".equals(args[0].toLowerCase())) {
				new AppServer(false);
			} else {
				illegalArguments();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void illegalArguments() {
		System.out.println("Invalid arguments:");
		System.out.println("start\t - start the server");
		System.out
				.println("install\t - make a fresh install and start the server");
		System.exit(-1);
	}
}
