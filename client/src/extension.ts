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
import {ExtensionContext, workspace} from "vscode";
import {LanguageClient, LanguageClientOptions, StreamInfo} from 'vscode-languageclient';

const isDevMode = () => process.env.DEV_MODE === "true";

//https://stackoverflow.com/questions/53594657/how-to-build-language-server-protocol-in-vscode-by-using-java-code-in-server-sid

export function activate(context: ExtensionContext) {
	function creServ(): Promise<StreamInfo> {
		return createServer(context.extensionPath, context.storagePath);
	}

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

	let disposable = new LanguageClient('culturesDevelopmentKit', 'Cultures Development Kit', creServ, clientOptions).start();

	// Push the disposable to the context's subscriptions so that the
	// client can be deactivated on extension deactivation
	context.subscriptions.push(disposable);
}

function createServer(extensionPath: string, storagePath: string): Promise<StreamInfo> {
	return new Promise((resolve, reject) => {

		const serverSocket: net.Server = net.createServer((socket: net.Socket) => {
			console.log("Client/LSP Server connected!");

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

		serverSocket.listen(port, () => {
			return onListening(serverSocket, extensionPath, storagePath);
		});
	});
}


function onListening(serverSocket: net.Server, extensionPath: string, storagePath: string) {
	if (isDevMode()) {
		return; // In dev we do not spawn the server
    } else {
        try {
            startServerExecutable(serverSocket, extensionPath, storagePath);
        } catch (e) {
            vscode.window.showErrorMessage("Unable to start language server: " + e.message);
            throw e;
        }
    }
}

function startServerExecutable(serverSocket: net.Server, extensionPath: string, storagePath: string) {
    let options = {cwd: workspace.rootPath};

    let serverPath = path.resolve(extensionPath, "server");

    let pathNativeImageExecutable = addOsSpecificSuffix(path.resolve(serverPath, "extension"));
    let pathJvmExecutable = addOsSpecificSuffix(path.resolve(serverPath, "jre", "bin", "java"));

    let pathExecutable: string;
    let args: string[];

    if (fs.existsSync(pathNativeImageExecutable)) {
        pathExecutable = pathNativeImageExecutable;
        args = [
            "--languageserver.port=" + serverSocket.address()["port"],
            "--languageserver.storagepath=" + storagePath
        ];
    } else if (fs.existsSync(pathJvmExecutable)) {
        pathExecutable = pathJvmExecutable;
        let jarPath = path.resolve(serverPath, "server.jar");
        args = [
            '-Xmx50m',
            '-Xshare:off',
            '-XX:+UseSerialGC',
            '-XX:MaxRAM=200M',
            '-jar',
            jarPath,
            "--languageserver.port=" + serverSocket.address()["port"],
            "--languageserver.storagepath=" + storagePath
        ]
    } else {
        throw new Error("No lsp server executable could be found! " + pathJvmExecutable);
    }

    let process = child_process.spawn(pathExecutable, args, options);
    process.on("error", (err: Error) => {
        console.log(err);
    })

    if (!fs.existsSync(storagePath)) {
        fs.mkdirSync(storagePath);
    }

    let logFile = path.resolve(storagePath, 'cultures-client.log');
    let logStream = fs.createWriteStream(logFile, {flags: 'w'});

	process.stdout.pipe(logStream);
	process.stderr.pipe(logStream);

	console.log(`Storing log in '${logFile}'`);
}

function addOsSpecificSuffix(binname: string) {
	if (process.platform === 'win32')
		return binname + '.exe';
	else
		return binname;
}
