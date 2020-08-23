package me.nithanim.cultures.lsp;

import org.h2.engine.SysProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class Main {
  public static void main(String[] args) throws Exception {
    SysProperties.serializeJavaObject = false;
    // ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
    // ctx.
    SpringApplication.run(Main.class, args);
  }
}
