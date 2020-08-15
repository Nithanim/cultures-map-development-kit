package me.nithanim.cultures.lsp;

import org.h2.engine.SysProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Main {
  public static void main(String[] args) throws Exception {
    SysProperties.serializeJavaObject = false;
    // ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
    // ctx.
    SpringApplication.run(Main.class, args);
  }
}
