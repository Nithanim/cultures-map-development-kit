/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
'use strict';

import * as fs from "fs"
import * as path from 'path';
import * as net from 'net';
import * as child_process from "child_process";

import * as vscode from "vscode";

import { workspace, Disposable, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, StreamInfo } from 'vscode-languageclient';
import { prototype } from "mocha";

const isDevMode = () => process.env.DEV_MODE === "true";

//https://stackoverflow.com/questions/53594657/how-to-build-language-server-protocol-in-vscode-by-using-java-code-in-server-sid

export function activate(context: ExtensionContext) {

	function createServer(): Promise<StreamInfo> {
		return new Promise((resolve, reject) => {

			var server = net.createServer((socket) => {
				console.log("Creating server");

				resolve({
					reader: socket,
					writer: socket
				});

				socket.on('end', () => console.log("Disconnected"));
			}).on('error', (err) => {
				// handle errors here
				throw err;
			});

			//let javaExecutablePath = findJavaExecutable('java');

			let port = isDevMode() ? 9826 : undefined;
			
			server.listen(port, () => {
				if(isDevMode()) {
					return; // In dev we do not spawn the server
				}
				// Start the child java process
				let options = { cwd: workspace.rootPath };

				let serverPath = path.resolve(context.extensionPath, "server");
				let javaExecutable = addOsSpecificSuffix(path.resolve(serverPath, "jre", "bin", "java"));
				let jarPath = path.resolve(serverPath, "server.jar");

				

				let args = [
					'-Xmx50m',
					'-Xshare:off',
					'-XX:+UseSerialGC',
					'-XX:MaxRAM=200M',
					'-jar',
					jarPath,
					"--languageserver.port=" + server.address()["port"]
				]

				let process = child_process.spawn(javaExecutable, args, options);
				process.on("error", (err: Error) => {
					console.log(err);
				})

				// Send raw output to a file
				if (!fs.existsSync(context.storagePath))
					fs.mkdirSync(context.storagePath);

				let logFile = context.storagePath + '/vscode-languageserver-java-example.log';
				let logStream = fs.createWriteStream(logFile, { flags: 'w' });

				process.stdout.pipe(logStream);
				process.stderr.pipe(logStream);

				console.log(`Storing log in '${logFile}'`);
			});
		});
	}; //END SERVER FUNCTION

	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents
		documentSelector: ['cultures-ini'],
		synchronize: {
			// Synchronize the setting section 'languageServerExample' to the server
			configurationSection: 'culturesDevelopmentKit',
			// Notify the server about file changes to '.clientrc files contain in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	// Create the language client and start the client.
	let disposable = new LanguageClient('culturesDevelopmentKit', 'Cultures Development Kit', createServer, clientOptions).start();

	// Push the disposable to the context's subscriptions so that the 
	// client can be deactivated on extension deactivation
	context.subscriptions.push(disposable);
}

// MIT Licensed code from: https://github.com/georgewfraser/vscode-javac
function findJavaExecutable(binname: string) {
	binname = addOsSpecificSuffix(binname);

	// First search each JAVA_HOME bin folder
	if (process.env['JAVA_HOME']) {
		let workspaces = process.env['JAVA_HOME'].split(path.delimiter);
		for (let i = 0; i < workspaces.length; i++) {
			let binpath = path.join(workspaces[i], 'bin', binname);
			if (fs.existsSync(binpath)) {
				return binpath;
			}
		}
	}

	// Then search PATH parts
	if (process.env['PATH']) {
		let pathparts = process.env['PATH'].split(path.delimiter);
		for (let i = 0; i < pathparts.length; i++) {
			let binpath = path.join(pathparts[i], binname);
			if (fs.existsSync(binpath)) {
				return binpath;
			}
		}
	}


	/* https://github.com/lannonbr/vscode-js-annotations/blob/master/src/extension.ts
	// Update when a file opens
	vscode.window.onDidChangeActiveTextEditor((editor) => {
		run(ctx, editor);
	  });
	
	  // Update when a file saves
	  vscode.workspace.onWillSaveTextDocument((event) => {
		const openEditor = vscode.window.visibleTextEditors.filter((editor) => editor.document.uri === event.document.uri)[0];
	
		run(ctx, openEditor);
	  });
	
	  vscode.workspace.onDidChangeTextDocument((event) => {
		if (timeoutId) {
		  clearTimeout(timeoutId);
		}
	
		timeoutId = setTimeout(() => {
		  const openEditor = vscode.window.visibleTextEditors.filter((editor) => editor.document.uri === event.document.uri)[0];
		  run(ctx, openEditor);
		}, 100);
	  });
	
	  // Update if the config was changed
	  vscode.workspace.onDidChangeConfiguration((event) => {
		if (event.affectsConfiguration("jsannotations")) {
		  run(ctx, vscode.window.activeTextEditor);
		}
	});*/


	return null;
}

function addOsSpecificSuffix(binname: string) {
	if (process.platform === 'win32')
		return binname + '.exe';
	else
		return binname;
}
