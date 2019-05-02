package com.github.braully.web;

import com.github.braully.app.url;
import com.github.braully.util.UtilIO;
import com.github.braully.web.jsf.ViewScope;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import nz.net.ultraq.thymeleaf.LayoutDialect;
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
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Path;

@EnableWebMvc
@Configuration
@EnableTransactionManagement
@RewriteConfiguration
public class SpringWebConfig implements
        WebMvcConfigurer, ServletContextInitializer, ConfigurationProvider<ServletContext> {

    public static final String WORKSPACE_DEV_DIRECTORY = UtilIO.homeUserDir() + File.separator + "workspace";

    static {
        try {
            new File(WORKSPACE_DEV_DIRECTORY).mkdirs();
        } catch (Exception e) {

        }
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
    public static final String DEFAULT_PROP_APP_FORM = "template.app.content.form";
    public static final String DEFAULT_PROP_APP_FILTER = "template.app.content.filter";
    public static final String DEFAULT_PROP_APP_LIST = "template.app.content.list";

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
        "/resources/", "classpath:/resources/", "classpath:/META-INF/resources/",
        "/web/", "classpath:/web/"};

    private static final String[] CLASSPATH_ASSETS_LOCATIONS = {
        "/assets/", "/resources/assets/", "classpath:/META-INF/resources/assets/",
        "classpath:/resources/assets/", "classpath:/web/assets/"};

    private static final String[] CLASSPATH_PKG_LOCATIONS = {
        "/static/assets/", "/static/bower_components/", "/static/node_modules/",
        "classpath:/web/assets/", "classpath:/web/node_modules/",
        "classpath:/web/bower_components/"};

    /* Option 5 - OpenEntityManagerInViewInterceptor instead of a filter (recommended):
     * https://stackoverflow.com/questions/33056952/openentitymanagerinviewfilter-annotation-config
     * TODO: Performance degraded openEntityManagerInView, improv this.
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new OpenEntityManagerInViewFilter());
        registrationBean.setName("openEntityManagerInViewFilter");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registrationBean;
    }


    /* Redirect and map rules Config */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /* block the servlets? */
        registry.addResourceHandler("/resources/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);

        registry.addResourceHandler("/assets/**")
                .addResourceLocations(CLASSPATH_ASSETS_LOCATIONS);

        registry.addResourceHandler("/pkg/**")
                .addResourceLocations(CLASSPATH_PKG_LOCATIONS);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /* Exemplo: registry.addRedirectViewController("/home", "/index");  */
        url.redirect.forEach((k, v) -> registry.addRedirectViewController(k, v));
    }

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

    /* JSF */
    @Bean
    public ServletRegistrationBean facesServlet() {
        ServletRegistrationBean servletRegistrationBean
                = new ServletRegistrationBean(new FacesServlet(), "*.jsf", "*.xhtml", "/jsf/*");
        servletRegistrationBean.setName("FacesServlet");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

//    /* Git:
//    http://m3y3r.de/post/2014-07-09-git-server-with-jetty-and-jgit/ 
//    https://github.com/onexus/onexus/tree/develop/org.onexus.resource.manager/src/main/java/org/onexus/resource/manager/internal/ws/git
//    https://github.com/centic9/jgit-cookbook/tree/master/httpserver
//    https://github.com/centic9/jgit-cookbook/blob/master/httpserver/src/main/java/org/dstadler/jgit/server/Main.java
//     */
//    @Bean
//    public ServletRegistrationBean gitServlet() {
//        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new org.eclipse.jgit.http.server.GitServlet(), "/app/git/*");
//        servletRegistrationBean.addInitParameter("base-path", WORKSPACE_DEV_DIRECTORY);
//        servletRegistrationBean.addInitParameter("export-all", "false");
//        return servletRegistrationBean;
//    }

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

    @Bean
    public ViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setTemplateEngine(thymeleafTemplateEngine());
        viewResolver.setViewNames(new String[]{"*.thf", "/thf/*"});
        viewResolver.setOrder(0);
        return viewResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        //templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        templateEngine.setTemplateResolver(thymeleafSpringTemplateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        /* 
         * https://www.thymeleaf.org/doc/articles/layouts.html 
         */
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver thymeleafSpringTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("");
        templateResolver.setSuffix("");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public ITemplateResolver thymeleafTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/thf/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    @Bean
    public GroovyMarkupConfigurer groovyMarkupConfigurer() {
        GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
        configurer.setResourceLoaderPath("/grv/");
        return configurer;
    }

    @Bean
    public GroovyMarkupViewResolver GroovyViewResolver() {
        GroovyMarkupViewResolver viewResolver = new GroovyMarkupViewResolver();
        viewResolver.setSuffix(".html");
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Bean
    public ViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setCache(true);
        viewResolver.setPrefix("/frmk/");
        viewResolver.setSuffix(".ftl");
        viewResolver.setViewNames(new String[]{"/frmk/*", "*.ftl"});
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() throws IOException, TemplateException {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
        factory.setTemplateLoaderPath("classpath:/frmk/");
        // Folder containing FreeMarker templates.
        // config.setTemplateLoaderPath("classpath:/templates");
        config.setConfiguration(factory.createConfiguration());
        return config;
    }

    /* Spring REST Config */
    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {
            @Override
            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
                config.setBasePath("/app");
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
            //TODO: Automate Production or Development parameter
            servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
            servletContext.setInitParameter("facelets.DEVELOPMENT", "true");
            servletContext.setInitParameter("javax.faces.FACELETS_REFRESH_PERIOD", "1");
            servletContext.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
            /* */
            servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
            servletContext.setInitParameter("primefaces.THEME", "bootstrap");
            servletContext.setInitParameter("primefaces.UPLOADER", "auto");
        };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new RequestContextListener());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
