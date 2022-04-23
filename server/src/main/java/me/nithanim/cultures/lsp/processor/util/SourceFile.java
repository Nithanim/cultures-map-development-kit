package me.nithanim.cultures.lsp.processor.util;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class SourceFile implements Serializable {
  Uri uri;
}
