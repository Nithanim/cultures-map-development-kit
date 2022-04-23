package me.nithanim.cultures.lsp;

import org.h2.util.JdbcUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class Main {
  public static void main(String[] args) throws Exception {
    JdbcUtils.serializer = new CustomShitSerializer();
    // FUCK, THIS WAS REMOVED:
    // https://github.com/h2database/h2database/commit/ca18e5a6d630593065da3643e96b6daf13715f3a
    // SysProperties.serializeJavaObject
    // JUST WHY? I hate you.
    // I just want to store and load the objects 1:1 no serialization needed!
    SpringApplication.run(Main.class, args);
  }
}
