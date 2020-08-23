package me.nithanim.cultures.lsp.base;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class LanguageServerLifecycle {
  private static final Logger logger = LoggerFactory.getLogger(LanguageServerLifecycle.class);

  private final ConfigurableApplicationContext applicationContext;
  private final AutowireCapableBeanFactory autowireCapableBeanFactory;
  private final ConfigurableBeanFactory configurableBeanFactory;

  private Future<Void> future;

  @Bean
  public LanguageClient languageClient(@Value("${languageserver.port:9826}") int port)
      throws IOException {
    logger.info("Connecting to clients server socket on port " + port);
    Socket socket;
    try {
      socket = new Socket("localhost", port);
    } catch (IOException e) {
      throw new IOException("Unable to connect to the language server client!");
    }

    CulturesIniLanguageServer server = createCulturesLanguageServer();

    Launcher<LanguageClient> launcher =
        LSPLauncher.createServerLauncher(server, socket.getInputStream(), socket.getOutputStream());
    LanguageClient client = launcher.getRemoteProxy();
    server.connect(client);

    future = launcher.startListening();
    return client;
  }

  private CulturesIniLanguageServer createCulturesLanguageServer() {
    CulturesIniLanguageServer server = new CulturesIniLanguageServer();
    server.setOnExit(this::onExit);
    autowireCapableBeanFactory.autowireBean(server);
    // autowireCapableBeanFactory.autowire(CulturesIniLanguageServer.class,
    // AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    configurableBeanFactory.registerSingleton("culturesIniLanguageServer", server);
    return server;
  }

  private void onExit() {
    applicationContext.stop();
  }

  @EventListener
  public void onStop(ContextStoppedEvent evt) throws IOException {
    future.cancel(true);
  }

  /*@EventListener
  public void onStart(ContextStartedEvent evt) throws IOException {
      Socket socket = new Socket("localhost", port);

      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();

      CulturesIniLanguageServer server = new CulturesIniLanguageServer();
      Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);

      LanguageClient client = launcher.getRemoteProxy();
      server.connect(client);
      beanFactory.registerSingleton("client", client);

      future = launcher.startListening();
  }*/
}
