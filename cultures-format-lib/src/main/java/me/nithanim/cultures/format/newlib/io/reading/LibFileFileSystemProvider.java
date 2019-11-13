/*
The basis for this file was a copy of fr.gnodet.githubfs.GitHubFileSystemProvider
from https://github.com/gnodet/githubfs/ created by Guillaume Nodet.
It was adapted to fit the need of accessing the contents of a container
file with custom format.
Many thanks to the original author!

The original file is licensed under:

 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package me.nithanim.cultures.format.newlib.io.reading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class LibFileFileSystemProvider extends FileSystemProvider {

    final Map<String, LibFileFileSystem> fileSystems = new HashMap<>();

    @Override
    public String getScheme() {
        //return "culures-lib-2";
        return "lib";
    }

    @Override
    public LibFileFileSystem newFileSystem(Path path, Map<String, ?> env) throws IOException {
        synchronized (fileSystems) {
            //String pathToFile = uri.getSchemeSpecificPart();
            String pathToFile = path.toString();
            int i = pathToFile.indexOf("!/");
            if (i >= 0) {
                pathToFile = pathToFile.substring(0, i);
            }
            LibFileFileSystem fileSystem = fileSystems.get(pathToFile);
            if (fileSystem != null) {
                throw new FileSystemAlreadyExistsException(pathToFile);
            }
            fileSystem = new LibFileFileSystem(this, Paths.get(pathToFile), env);
            fileSystems.put(pathToFile, fileSystem);
            return fileSystem;
        }
    }

    @Override
    public LibFileFileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LibFileFileSystem getFileSystem(URI uri) {
        return getFileSystem(uri, false);
    }

    public LibFileFileSystem getFileSystem(URI uri, boolean create) {
        synchronized (fileSystems) {
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            int i = schemeSpecificPart.indexOf("!/");
            if (i >= 0) {
                schemeSpecificPart = schemeSpecificPart.substring(0, i);
            }
            LibFileFileSystem fileSystem = fileSystems.get(schemeSpecificPart);
            if (fileSystem == null) {
                if (create) {
                    try {
                        fileSystem = newFileSystem(uri, null);
                    } catch (IOException e) {
                        throw (FileSystemNotFoundException) new FileSystemNotFoundException(schemeSpecificPart).initCause(e);
                    }
                } else {
                    throw new FileSystemNotFoundException(schemeSpecificPart);
                }
            }
            return fileSystem;
        }
    }

    @Override
    public Path getPath(URI uri) {
        String str = uri.getSchemeSpecificPart();
        int i = str.indexOf("!/");
        if (i == -1) {
            throw new IllegalArgumentException("URI: " + uri + " does not contain path info ex. " + getScheme() + ":apache/karaf#master!/");
        }
        return getFileSystem(uri, true).getPath(str.substring(i + 1));
    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... options) throws IOException {
        if (!(path instanceof LibFilePath)) {
            throw new ProviderMismatchException();
        }
        return ((LibFilePath) path).getFileSystem().newInputStream(path, options);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        if (!(dir instanceof LibFilePath)) {
            throw new ProviderMismatchException();
        }
        return ((LibFilePath) dir).getFileSystem().newDirectoryStream(dir, filter);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if (!(path instanceof LibFilePath)) {
            throw new ProviderMismatchException();
        }
        return ((LibFilePath) path).getFileSystem().newByteChannel(path, options, attrs);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return path.toAbsolutePath().equals(path2.toAbsolutePath());
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {

    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (!(path instanceof LibFilePath)) {
            throw new ProviderMismatchException();
        }
        return ((LibFilePath) path).getFileSystem().readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

}
