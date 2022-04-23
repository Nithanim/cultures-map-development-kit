package me.nithanim.cultures.lsp.processor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.eclipse.lsp4j.Range;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Origin {
  @NonNull SourceFile sourceFile;
  @NonNull Range range;
}
