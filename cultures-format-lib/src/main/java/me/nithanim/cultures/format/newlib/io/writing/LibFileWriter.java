package me.nithanim.cultures.format.newlib.io.writing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Value;

public class LibFileWriter implements AutoCloseable {
  private Map<String, ?> folders = new HashMap<>();
  private List<File> files = new ArrayList<>();

  public void addFile(String internalPath, Path data) throws IOException {
    InputStream in = Files.newInputStream(data);
    long length = Files.size(data);
    addFile(internalPath, in, (int) length);
  }

  public void addFile(String internalPath, byte[] data) {
    addFile(internalPath, new ByteArrayInputStream(data), data.length);
  }

  public void addFile(String internalPath, InputStream data, int dataLength) {
    List<String> pathSegments = splitPath(internalPath);
    addToFolderTree(removeLast(pathSegments));
    files.add(new File(pathSegments, dataLength, data));
  }

  @SuppressWarnings("unchecked")
  private void addToFolderTree(List<String> segments) {
    Map<String, Object> current = (Map<String, Object>) folders;
    for (String segment : segments) {
      current =
          (Map<String, Object>)
              current.computeIfAbsent(segment, k -> new HashMap<String, Object>());
    }
  }

  private List<String> splitPath(String path) {
    return Arrays.asList(path.split("\\\\"));
  }

  private List<String> removeLast(List<String> path) {
    return path.subList(0, path.size() - 1);
  }

  public void writeTo(OutputStream stream) throws IOException {
    LittleEndianDataOutput out = new LittleEndianDataOutput(new DataOutputStream(stream));
    out.writeInt(1);
    ByteArrayOutputStream folderBytes = new ByteArrayOutputStream();
    int nFolders = writeFolders(folderBytes);
    out.writeInt(nFolders);
    out.writeInt(files.size());
    out.write(folderBytes.toByteArray());
    writeFiles(out);
  }

  private void writeFiles(LittleEndianDataOutput out) throws IOException {
    List<FileInfo> fileInfos =
        files.stream()
            .map(
                file -> {
                  String filePath = String.join("\\", file.getPath());
                  byte[] fileNameBytes = filePath.getBytes(StandardCharsets.ISO_8859_1);
                  return new FileInfo(
                      fileNameBytes.length, fileNameBytes, file.getDataLength(), file.getData());
                })
            .collect(Collectors.toList());

    int additionalBytes = fileInfos.size() * (4 + 4 + 4);
    int sumNameBytes = fileInfos.stream().mapToInt(FileInfo::getNameSize).sum();
    int startPos = out.getBytesWritten() + additionalBytes + sumNameBytes;
    int currentPos = startPos;

    for (FileInfo fileInfo : fileInfos) {
      out.writeInt(fileInfo.getNameSize());
      out.write(fileInfo.getNameBytes());
      out.writeInt(currentPos);
      out.writeInt(fileInfo.getContentSize());
      currentPos += fileInfo.getContentSize();
    }

    for (FileInfo fileInfo : fileInfos) {
      try {
        writeData(out, fileInfo.getContentSize(), fileInfo.getContentBytes());
      } catch (Exception ex) {
        throw new IOException(
            "Unable to lib file content of "
                + new String(fileInfo.getNameBytes(), StandardCharsets.ISO_8859_1),
            ex);
      }
    }
  }

  private void writeData(LittleEndianDataOutput out, int contentSize, InputStream contentBytes)
      throws IOException {
    byte[] buffer = new byte[1024 * 8];
    int writtenTotal = 0;
    int read;
    while ((read = contentBytes.read(buffer)) != -1) {
      out.write(buffer, 0, read);
      writtenTotal += read;
    }
    if (writtenTotal != contentSize) {
      throw new IllegalStateException(
          "InputStream returned not exactly the expected number of bytes!");
    }
  }

  @Override
  public void close() throws Exception {
    for (File file : files) {
      try {
        file.getData().close();
      } catch (Exception ex) {
        // don't care
      }
    }
  }

  @Value
  private static class FileInfo {
    int nameSize;
    byte[] nameBytes;
    int contentSize;
    InputStream contentBytes;
  }

  private int writeFolders(OutputStream out) throws IOException {
    LittleEndianDataOutput stream = new LittleEndianDataOutput(new DataOutputStream(out));
    stream.writeInt(1);
    stream.write(new byte[] {'\\'});
    stream.writeInt(0);

    return writeFolderTree(stream, 1, "", (Map<String, Map<String, ?>>) folders) + 1;
  }

  @SuppressWarnings("unchecked")
  private int writeFolderTree(
      DataOutput out, int level, String prefix, Map<String, Map<String, ?>> subtree)
      throws IOException {
    int nFolders = 0;
    for (Map.Entry<String, Map<String, ?>> entry : subtree.entrySet()) {
      String fullPath = prefix + entry.getKey() + "\\";
      byte[] fullPathBytes = fullPath.getBytes(StandardCharsets.ISO_8859_1);
      out.writeInt(fullPathBytes.length);
      out.write(fullPathBytes);
      out.writeInt(level);
      nFolders++;

      if (entry.getValue() != null) {
        nFolders +=
            writeFolderTree(
                out, level + 1, fullPath, (Map<String, Map<String, ?>>) entry.getValue());
      }
    }
    return nFolders;
  }

  @Value
  private static class File {
    List<String> path;
    int dataLength;
    InputStream data;
  }
}
