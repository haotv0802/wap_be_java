package config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.config.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Configuration
@ComponentScan({"crwlr"})
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringConfig extends WebMvcConfigurerAdapter {

  private Logger log = LogManager.getLogger(getClass());

  private static final long sessionTimeoutInSec = 1800L;

  public SpringConfig() {
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**");
  }

  @Bean
  public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    jsonConverter.setObjectMapper(objectMapper);
    return jsonConverter;
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(customJackson2HttpMessageConverter());
  }

  @Bean(name = "dataSource")
  public DataSource dataSource() throws SQLException {
    final String databaseUrl = "jdbc:mysql://localhost:3306/crawler_db";
    final String usr = "root";
    final String pass = "hoanhhao";

    log.debug("databaseUrl=={}", databaseUrl);

    MysqlDataSource ds = new MysqlDataSource();
    ds.setURL(databaseUrl);
    ds.setUser(usr);
    ds.setPassword(pass);

    return ds;
  }

  @Bean(name = "txManager")
  public PlatformTransactionManager txManager() throws SQLException {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean(name = "transactionTemplate")
  public TransactionTemplate transactionTemplate() throws SQLException {
    return new TransactionTemplate(txManager());
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    super.addInterceptors(registry);
  }

  @Configuration
  @EnableHazelcastHttpSession(maxInactiveIntervalInSeconds = (int) sessionTimeoutInSec, sessionMapName = "spring:session:sessions")
  protected static class SessionConfig {
    @Bean
    public HazelcastInstance embeddedHazelcast() {
      Config cfg = new Config();
      cfg.setProperty("hazelcast.logging.type", "slf4j");
      final NetworkConfig netConfig = cfg.getNetworkConfig();
      netConfig.getJoin().getTcpIpConfig().setEnabled(false);
      netConfig.getJoin().getMulticastConfig().setEnabled(false);

      return Hazelcast.newHazelcastInstance(cfg);
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
      return new HeaderHttpSessionStrategy();
    }

  }

  @Bean(name = "jdbcTemplate")
  public JdbcTemplate jdbcTemplate()
      throws SQLException {
    return new JdbcTemplate(dataSource());
  }

  @Bean(name = "namedTemplate")
  public NamedParameterJdbcTemplate namedTemplate()
      throws SQLException {
    return new NamedParameterJdbcTemplate(dataSource());
  }


}