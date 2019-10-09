package com.github.braully.web;

import com.github.braully.app.SecurityService;
import com.github.braully.app.url;
import com.github.braully.web.jsf.ViewScope;

import java.util.HashMap;
import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Connector;
//import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.config.Direction;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@RewriteConfiguration
public class SpringWebConfig
        //extends WebSecurityConfigurerAdapter
        implements WebMvcConfigurer, ConfigurationProvider<ServletContext>,
        ServletContextInitializer {

    //@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new RequestContextListener());
    }

    //@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    public static final String DEFAULT_PROP_TEMPLATE_HTML_ATTRIBUTE_APPEND = "template.html.attribute";
    public static final String DEFAULT_PROP_TEMPLATE_HEAD_ATTRIBUTE_APPEND = "template.head.attribute";
    public static final String DEFAULT_PROP_TEMPLATE_BODY_ATTRIBUTE_APPEND = "template.body.attribute";
    public static final String DEFAULT_PROP_TEMPLATE_HTML_APPEND = "template.html.append";
    public static final String DEFAULT_PROP_TEMPLATE_HEAD_APPEND = "template.head.append";
    public static final String DEFAULT_PROP_TEMPLATE_BODY_APPEND = "template.body.append";
    public static final String DEFAULT_PROP_TEMPLATE_FOOT_APPEND = "template.foot.append";
    public static final String DEFAULT_PROP_APP_NAME = "template.app.name";
    public static final String DEFAULT_PROP_APP_TITTLE = "template.app.title";
    public static final String DEFAULT_PROP_APP_HEADER = "template.app.header";
    public static final String DEFAULT_PROP_APP_MENU = "template.app.menu";
    public static final String DEFAULT_PROP_APP_CONTET = "template.app.content";
    public static final String DEFAULT_PROP_APP_MSG = "template.app.content.msg";
    public static final String DEFAULT_PROP_APP_FORM = "template.app.content.form";
    public static final String DEFAULT_PROP_APP_FILTER = "template.app.content.filter";
    public static final String DEFAULT_PROP_APP_LIST = "template.app.content.list";

    static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
        "/", "/web/", "META-INF/resources/", "classpath:web/",
        "classpath:META-INF/resources/"
    };

    static final String[] CLASSPATH_PKG_LOCATIONS = {
        "node_modules/", "classpath:node_modules/"
    };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /* block the servlets? */
        registry.addResourceHandler("/*")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);

        registry.addResourceHandler("/pkg/**")
                .addResourceLocations(CLASSPATH_PKG_LOCATIONS);
    }

    /* Redirect and map rules Config */

 /*
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("urlRewriteFilterTuckey");
        registrationBean.setFilter(new UrlRewriteFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }*/
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public FilterRegistrationBean rewriteFilterOCP() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new RewriteFilter());
        registrationBean.setName("rewriteFilterOCP");
        registrationBean.addInitParameter("org.ocpsoft.rewrite.annotation.SCAN_LIB_DIRECTORY", "true");
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    //Redirect

    //@RewriteConfiguration
    //public static class SpringRewrite implements ConfigurationProvider<ServletContext> {
    @Override
    public org.ocpsoft.rewrite.config.Configuration getConfiguration(ServletContext context) {
        ConfigurationBuilder config = ConfigurationBuilder.begin();
        url.rewrite_forward.forEach((k, v) -> config.addRule().when(
                Direction.isInbound().and(Path.matches(k))).perform(Forward.to(v))
        );
        return config;
    }

    @Override
    public int priority() {
        return 10;

    }

    @Override
    public boolean handles(final Object payload) {
        return payload instanceof ServletContext;
    }
    //}


    /* Option 5 - OpenEntityManagerInViewInterceptor instead of a filter (recommended):
     * https://stackoverflow.com/questions/33056952/openentitymanagerinviewfilter-annotation-config
     * TODO: Performance degraded openEntityManagerInView, improv this.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /* Exemplo: registry.addRedirectViewController("/home", "/index");  */
        url.redirect.forEach((k, v) -> registry.addRedirectViewController(k, v));
    }

    //Open entity manager
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new OpenEntityManagerInViewFilter());
        registrationBean.setName("openEntityManagerInViewFilter");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registrationBean;
    }

    /* JSF */
    @Bean
    public ServletRegistrationBean facesServlet() {
        ServletRegistrationBean servletRegistrationBean
                = new ServletRegistrationBean(new FacesServlet(), "*.jsf", "*.xhtml", "/jsf/*");
        servletRegistrationBean.setName("FacesServlet");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    /*
    * https://www.baeldung.com/spring-mvc-content-negotiation-json-xml
    * https://spring.io/blog/2013/06/03/content-negotiation-using-views
    * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/ViewResolver.html
    * https://o7planning.org/en/11257/using-multiple-viewresolvers-in-spring-boot
    * https://github.com/hpym365/Springboot-Multi-ViewResolver/blob/master/src/main/java/com/senyint/config/MvcConfiguration.java
    * https://www.google.com/search?q=spring+boot+multi+viewresolver
     */
    @Bean
    public CustomScopeConfigurer viewScope() {
        CustomScopeConfigurer csc = new CustomScopeConfigurer();
        csc.setScopes(new HashMap<String, Object>() {
            {
                put("view", new ViewScope());
            }
        });
        return csc;
    }

    /* Spring REST Config */
    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {
            //@Override
            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
                config.setBasePath("/repo");
            }
        };
    }

    /*  Spring ViewResolvers lastest
     * https://stackoverflow.com/questions/30253953/spring-boot-mvc-how-to-support-multiple-viewresoler-eg-jsp-and-freemarker
     */
 /* Spring MVC JSTL & JSP: @Configuration public class JspViewResolverConfiguration */
    @Bean
    public ViewResolver internalResourceViewResolverJsp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/jsp/");
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setContentType("text/html");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(1000);
        return viewResolver;
    }

    /* Exra configurations */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
            servletContext.setInitParameter("javax.faces.PARTIAL_STATE_SAVING_METHOD", "true");
            servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Production");
            //TODO: Automate Production or Development parameter
            /*if (config.isDevlopment(env)) {
                servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
                servletContext.setInitParameter("facelets.DEVELOPMENT", "true");
            }*/

            servletContext.setInitParameter("javax.faces.FACELETS_REFRESH_PERIOD", "1");
            servletContext.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
            /* */
            servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
            servletContext.setInitParameter("primefaces.THEME", "bootstrap");
            servletContext.setInitParameter("primefaces.UPLOADER", "auto");
        };
    }

    //Security
    @Configuration
    @EnableWebSecurity
    public static class SpringWebSecurityConfig
            extends WebSecurityConfigurerAdapter {

        @Bean
        public UserDetailsService userDetailsService() {
            return new SecurityService();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService());
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider());
        }

        /*  https://stackoverflow.com/questions/44064346/spring-security-filter-authenticates-sucessfuly-but-sends-back-403-response */
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers(
                            "/pkg/**", "/assets/**", "/*resource/**", "/favicon.ico",
                            "/error**", "/error/**"
                    );
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests()//Login urls
                    .antMatchers(
                            "/login**", "/enter**",
                            "/logout**"
                    ).permitAll()
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/enter")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/index")
                    .permitAll()
                    .and().logout().deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true).permitAll();

            //Permit iframes from same origin;
            //https://stackoverflow.com/questions/28647136/how-to-disable-x-frame-options-response-header-in-spring-security
            http.headers()
                    .frameOptions()
                    .sameOrigin();

        }
    }

    //Tomcat conector
    @Component
    public static class SpringWebConnectorConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

        private final static String CONNECTOR_PROTOCOL = TomcatServletWebServerFactory.DEFAULT_PROTOCOL; //"org.apache.coyote.http11.Http11NioProtocol";
        private final static String CONNECTOR_SCHEME = "http";

        @Value("${http.port:8080}")
        Integer httpPort;

        @Value("${server.port:8443}")
        Integer serverPort;

        @Value("${security.require-ssl:false}")
        Boolean requireSsl;

        @Override
        public void customize(TomcatServletWebServerFactory factory) {
            factory.setPort(serverPort);
            if (!httpPort.equals(serverPort)) {
                factory.addAdditionalTomcatConnectors(getHttpConnector());
            }
        }

        //https://stackoverflow.com/questions/30896234/how-set-up-spring-boot-to-run-https-http-ports
        private Connector getHttpConnector() {
            Connector connector = new Connector(CONNECTOR_PROTOCOL);
            connector.setPort(httpPort);
            connector.setScheme(CONNECTOR_SCHEME);
            connector.setSecure(false);
            //If redirect, uncoment
            if (requireSsl != null && requireSsl) {
                connector.setRedirectPort(serverPort);
            }
            return connector;
        }
    }
}
