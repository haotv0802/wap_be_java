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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;
import wap.auth.LoggingEnhancingFilter;
import wap.auth.PasswordEncoderImpl;
import wap.auth.TokenAuthenticationService;
import wap.auth.encoders.DefaultPasswordEncoder;
import wap.auth.encoders.MD5HalfPasswordEncoder;
import wap.auth.encoders.MD5PasswordEncoder;
import wap.auth.encoders.SHA1PasswordEncoder;
import wap.auth.filters.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Configuration
@ComponentScan({"wap"})
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
    final String databaseUrl = "jdbc:mysql://localhost:3306/crawler_db_180104?useUnicode=true&characterEncoding=UTF-8";
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

  @Configuration
  @EnableWebSecurity
  @EnableGlobalMethodSecurity(prePostEnabled = true) // ENABLE @PreAuthorize & @PostAuthorize annotations
  // <security:global-method-security pre-post-annotations="enabled" />
  protected static class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    PlatformTransactionManager txManager;

    @Resource(name = "authService")
    private UserDetailsService userDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandlerImpl;

//    @Autowired
//    @Qualifier("authenticationProvider")
//    DaoAuthenticationProvider authenticationProvider;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//      auth.authenticationProvider(authenticationProvider);
//    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
      ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
      messageSource.setBasenames(
          "i18n.LoginResource",
          "i18n.admin_image",
          "i18n.admin_messages"
      );
      messageSource.setUseCodeAsDefaultMessage(true);
      return messageSource;
    }

    @Bean
    public AuthenticationFailureHandlerImpl customizedAuthenticationFailureHandler() {
      return new AuthenticationFailureHandlerImpl(messageSource());
    }

    @Bean
    public AuthenticationSuccessHandlerImpl customizedAuthenticationSuccessHandler() {
      return new AuthenticationSuccessHandlerImpl();
    }

    @Override
    protected UserDetailsService userDetailsService() {
      return userDetailsService;
    }

    @Bean
    public AccessDeniedHandlerImpl customizedAccessDeniedHandler() {
      return new AccessDeniedHandlerImpl(messageSource());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//          .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//          .maximumSessions(10)
//          .and()
          .and()
          .exceptionHandling()
          .authenticationEntryPoint(restAuthenticationEntryPoint)
          .accessDeniedHandler(accessDeniedHandlerImpl)
          .and()
          .csrf().disable()
          .authorizeRequests()
          //allow anonymous POSTs to login
          .antMatchers(HttpMethod.POST, "/svc/login").permitAll()
          .antMatchers(HttpMethod.GET, "/svc/messages").permitAll()
          //all other request need to be authenticated
          .antMatchers("/svc/**").authenticated()
          .and()
          .logout()
          .logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.POST.name()))
          .invalidateHttpSession(true)
          .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
          .and()
          // custom CORS filter as the mvc cors config doesn't play well, yet, with security
          .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
          // custom JSON based authentication by POST of {"userName":"<name>","userPass":"<password>"}
          .addFilterBefore(statelessLoginFilter(), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(statelessAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
          .addFilterAfter(loggingEnhancingFilter(), FilterSecurityInterceptor.class)
      ;
    }

    @Bean
    public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true); // you USUALLY want this
      config.addAllowedOrigin("*");
      config.addAllowedHeader("*");
      config.addAllowedMethod("GET");
      config.addAllowedMethod("HEAD");
      config.addAllowedMethod("POST");
      config.addAllowedMethod("DELETE");
      config.addAllowedMethod("PATCH");
      config.addAllowedMethod("PUT");

      config.addExposedHeader("Location");
      config.addExposedHeader("X-AUTH-TOKEN");
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
    }

    @Bean
    LoggingEnhancingFilter loggingEnhancingFilter() {
      return new LoggingEnhancingFilter();
    }

    @Bean
    StatelessLoginFilter statelessLoginFilter() throws Exception {
      return new StatelessLoginFilter(
          "/login"
          , authenticationManagerBean()
          , customizedAuthenticationFailureHandler()
          , customizedAuthenticationSuccessHandler());
    }

    @Bean
    StatelessAuthenticationFilter statelessAuthenticationFilter() {
      return new StatelessAuthenticationFilter(tokenAuthenticationService);
    }

    //TODO split mvc and security config
    @Bean
    public Object pwdEncoder() {
      PasswordEncoder passwordEncoder = null;
      // (SHA1) Used when connect to UDAL for testing e_transact
      // String passHashAlg = "SHA1";
      String passHashAlg = "DEFAULT";
      switch (passHashAlg) {
        case "MD5/0.5":
          passwordEncoder = new MD5HalfPasswordEncoder();
          break;
        case "MD5":
          passwordEncoder = new MD5PasswordEncoder();
          break;
        case "SHA1":
          passwordEncoder = new SHA1PasswordEncoder();
          break;
        case "DEFAULT":
          passwordEncoder = new DefaultPasswordEncoder();
          break;
        default:
          throw new IllegalArgumentException("Unknown password hash type");
      }

      return new PasswordEncoderImpl(passwordEncoder) {
      };
    }

    @Bean
    public Boolean hideUserNotFound() {
      return false;
    }

  }
}