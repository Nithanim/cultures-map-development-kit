package me.nithanim.cultures.lsp.config;

import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.h2.tools.Server;
import org.jooq.ConnectionProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.ExecutorProvider;
import org.jooq.RecordListenerProvider;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordUnmapperProvider;
import org.jooq.TransactionListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jooq.JooqProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:create_database.properties")
public class PersistenceConfiguration {
  @Autowired private Environment environment;

  @Bean
  @SneakyThrows
  public DataSource dataSource() {
    // JdbcDataSource dataSource = new JdbcDataSource();
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    dataSource.setUrl(environment.getRequiredProperty("db.url"));
    dataSource.setUsername(environment.getRequiredProperty("db.username"));
    dataSource.setPassword(environment.getRequiredProperty("db.password"));

    Server.createTcpServer().start();
    applySchema(dataSource);

    return dataSource;
  }

  private void applySchema(DriverManagerDataSource dataSource) {
    // schema init
    Resource initSchema = new ClassPathResource("create_database.sql");
    DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema);
    DatabasePopulatorUtils.execute(databasePopulator, dataSource);
  }

  @Bean
  @ConditionalOnMissingBean({org.jooq.Configuration.class})
  public DefaultConfiguration jooqConfiguration(
      JooqProperties properties,
      ConnectionProvider connectionProvider,
      DataSource dataSource,
      ObjectProvider<TransactionProvider> transactionProvider,
      ObjectProvider<RecordMapperProvider> recordMapperProvider,
      ObjectProvider<RecordUnmapperProvider> recordUnmapperProvider,
      ObjectProvider<Settings> settings,
      ObjectProvider<RecordListenerProvider> recordListenerProviders,
      ObjectProvider<ExecuteListenerProvider> executeListenerProviders,
      ObjectProvider<VisitListenerProvider> visitListenerProviders,
      ObjectProvider<TransactionListenerProvider> transactionListenerProviders,
      ObjectProvider<ExecutorProvider> executorProvider) {
    DefaultConfiguration configuration = new DefaultConfiguration();
    configuration.set(properties.determineSqlDialect(dataSource));
    configuration.set(connectionProvider);
    transactionProvider.ifAvailable(configuration::set);
    recordMapperProvider.ifAvailable(configuration::set);
    recordUnmapperProvider.ifAvailable(configuration::set);
    settings.ifAvailable(configuration::set);
    executorProvider.ifAvailable(configuration::set);
    configuration.set(
        (RecordListenerProvider[])
            recordListenerProviders
                .orderedStream()
                .toArray(
                    (x$0) -> {
                      return new RecordListenerProvider[x$0];
                    }));
    configuration.set(
        (ExecuteListenerProvider[])
            executeListenerProviders
                .orderedStream()
                .toArray(
                    (x$0) -> {
                      return new ExecuteListenerProvider[x$0];
                    }));
    configuration.set(
        (VisitListenerProvider[])
            visitListenerProviders
                .orderedStream()
                .toArray(
                    (x$0) -> {
                      return new VisitListenerProvider[x$0];
                    }));
    configuration.setTransactionListenerProvider(
        (TransactionListenerProvider[])
            transactionListenerProviders
                .orderedStream()
                .toArray(
                    (x$0) -> {
                      return new TransactionListenerProvider[x$0];
                    }));
    return configuration;
  }

  /*@Bean
  public TransactionAwareDataSourceProxy transactionAwareDataSource(DataSource dataSource) {
    return new TransactionAwareDataSourceProxy(dataSource);
  }*/

  /*@Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
    return new DataSourceConnectionProvider(transactionAwareDataSource(dataSource));
  }*/
  /*
  @Bean
  public ExceptionTranslator exceptionTransformer() {
    return new ExceptionTranslator();
  }

  @Bean
  public DefaultDSLContext jooq(DefaultConfiguration configuration) {
    return new DefaultDSLContext(configuration);
  }

  @Bean
  public DefaultConfiguration configuration(DataSource dataSource) {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.set(connectionProvider(dataSource));
    jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

    String sqlDialectName = environment.getRequiredProperty("jooq.sql.dialect");
    SQLDialect dialect = SQLDialect.valueOf(sqlDialectName);
    jooqConfiguration.set(dialect);

    return jooqConfiguration;
  }*/
}
