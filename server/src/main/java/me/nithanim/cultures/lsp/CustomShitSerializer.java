package me.nithanim.cultures.lsp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.h2.api.JavaObjectSerializer;

public class CustomShitSerializer implements JavaObjectSerializer {
  private final ObjectMapper mapper =
      JsonMapper.builder()
          .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
          .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
          .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
          .visibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE)
          .activateDefaultTyping(
              LaissezFaireSubTypeValidator.instance,
              ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE)
          .build();

  @Override
  public byte[] serialize(Object obj) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (var out = new DataOutputStream(baos)) {
      out.writeUTF(obj.getClass().getName());
    }
    mapper.writeValue(baos, obj);
    return baos.toByteArray();
  }

  @Override
  public Object deserialize(byte[] bytes) throws Exception {
    try (var in = new DataInputStream(new ByteArrayInputStream(bytes))) {
      String typeName = in.readUTF();
      return mapper.readValue((DataInput) in, Class.forName(typeName));
    }
  }
}
