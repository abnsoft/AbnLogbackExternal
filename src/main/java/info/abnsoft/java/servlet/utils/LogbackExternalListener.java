package info.abnsoft.java.servlet.utils;

import info.abnsoft.java.servlet.exception.NoFoundServletContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
//import java.util.logging.LogManager;
import org.springframework.web.util.ServletContextPropertyUtils;
import org.springframework.web.util.WebUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

//import ch.qos.logback.classic.LoggerContext;

/**
 */
public class LogbackExternalListener implements ServletContextListener {

    /**
     * 
     */
    public static final String LOGBACK_CONFIG_FILE_NAME = "LogbackConfigFilename";

    public static final String LOGBACK_CONFIG_FILE_NAME_DEFAULT = "logback.xml";

    private static final Logger LOG = LoggerFactory.getLogger( LogbackExternalListener.class );

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized( ServletContextEvent sce ) {

        LOG.debug( this.getClass().getName() + " ~ Context initialized" );

        ServletContext defaultServletContext = sce.getServletContext();

        if ( defaultServletContext == null ) {
            throw new NoFoundServletContext();
        }
        if ( defaultServletContext.getInitParameter( LOGBACK_CONFIG_FILE_NAME ) == null ) {
            LOG.warn( "There is defined " + this.getClass().getName()
                    + ", but not found <context-param><param-name>" + LOGBACK_CONFIG_FILE_NAME
                    + "</param-name>... (in web.xml). Will try to use default logback.xml." );

        } else {

            String logbackConfigurationFile = "";

            String contextParamLogback = defaultServletContext.getInitParameter( LOGBACK_CONFIG_FILE_NAME );
            LOG.debug( "Found param-name=`{}` , value=`{}`", LOGBACK_CONFIG_FILE_NAME, contextParamLogback );

            if ( contextParamLogback.matches( ".*(\\$\\{.*\\}).*" ) ) {
                String contextName =
                        contextParamLogback.substring( contextParamLogback.indexOf( "${" ),
                                contextParamLogback.indexOf( "}" ) + 1 );
                contextName = contextName.length() < 3 ? "   " : contextName;
                LOG.warn(
                        "It seems there is not defined valid name VM arguments for param-name=`{}`! You have to"
                                + " use in VM arguments of Application server or JVM  … -D{}=/path/to/dir … ",
                        contextName, contextName.substring( 2, contextName.length() - 1 ) );
            }

            try {
                logbackConfigurationFile =
                        ServletContextPropertyUtils.resolvePlaceholders( contextParamLogback,
                                defaultServletContext );

                // Leave a URL (e.g. "classpath:" or "file:") as-is.
                if ( !ResourceUtils.isUrl( logbackConfigurationFile ) ) {
                    logbackConfigurationFile =
                            WebUtils.getRealPath( defaultServletContext, logbackConfigurationFile );
                }

                defaultServletContext.log( "Initializing Logback from [" + logbackConfigurationFile + "]" );

                InputStream configResource =
                        defaultServletContext.getResourceAsStream( logbackConfigurationFile );

                initLogback( ResourceUtils.getFile( logbackConfigurationFile ) );

            } catch (FileNotFoundException e) {
                LOG.error( "Logback configuration file `{}` not found.", contextParamLogback );
            }

        }

    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed( ServletContextEvent sce ) {

        LOG.debug( "contex DESTROYED" );
        ( (LoggerContext) LoggerFactory.getILoggerFactory() ).stop();

    }

    /**
     * Initialize Logback with given file,
     * 
     * @param configPath
     */
    public static void initLogback( File configResource ) {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( context );
            configurator.doConfigure( configResource );

        } catch (JoranException e) {
            throw new RuntimeException( "Cannot initialize logback with given file `"
                    + configResource.toString() + "` ", e );
        }
    }

    public static void initLogback( String configPath ) {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( context );
            configurator.doConfigure( configPath );

        } catch (JoranException e) {
            throw new RuntimeException( "Cannot initialize logback with given file `" + configPath + "` ", e );
        }
    }

}
