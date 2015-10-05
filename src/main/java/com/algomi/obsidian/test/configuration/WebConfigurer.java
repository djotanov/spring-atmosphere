package com.algomi.obsidian.test.configuration;

import com.algomi.obsidian.test.controller.AtmosphereService;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.ContainerInitializer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;

/**
 * Configures an {@link AtmosphereServlet} and announces it to the
 * {@link ServletContext}.
 */
@Configuration
public class WebConfigurer {

    @Bean
    public EmbeddedAtmosphereInitializer atmosphereInitializer() {
        return new EmbeddedAtmosphereInitializer();
    }

    @Bean
    public ServletRegistrationBean atmosphereServlet() {
        // Dispatcher servlet is mapped to '/home' to allow the AtmosphereServlet
        // to be mapped to '/chat'
        ServletRegistrationBean registration = new ServletRegistrationBean(
                new AtmosphereServlet(), "/async/*");
        registration.addInitParameter(ApplicationConfig.CLIENT_HEARTBEAT_INTERVAL_IN_SECONDS, "10");
        registration.addInitParameter(ApplicationConfig.ANNOTATION_PACKAGE, AtmosphereService.class.getPackage().getName());
        registration.addInitParameter(ApplicationConfig.BROADCASTER_CACHE, UUIDBroadcasterCache.class.getName());
        registration.addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true");
        registration.addInitParameter(ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE, "10");
        registration.addInitParameter(ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE, "10");
        registration.setLoadOnStartup(0);
        registration.setAsyncSupported(true);
        // Need to occur before the EmbeddedAtmosphereInitializer
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    private static class EmbeddedAtmosphereInitializer extends ContainerInitializer implements ServletContextInitializer {
        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            onStartup(Collections.<Class<?>>emptySet(), servletContext);
            servletContext.addListener(new org.atmosphere.cpr.SessionSupport());
        }
    }
}
