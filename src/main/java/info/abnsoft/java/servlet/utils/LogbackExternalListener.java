package info.abnsoft.java.servlet.utils;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
//import java.util.logging.LogManager;
import org.springframework.web.util.ServletContextPropertyUtils;
import org.springframework.web.util.WebUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.sun.tools.javac.util.Assert;

//import ch.qos.logback.classic.LoggerContext;

/**
 * This is extends of {@link ServletContextListener}. <br />
 * Using this listener allows you to use in web application Logback config file in any place of filesystem.
 * You should not place now logback.xml either only inside of web app or classpath. <br />
 * <br />
 * The main goal of this library is changing the logging on the fly without restarting and changing any files
 * in web applicat<br />
 * <br />
 * ion. It is based on:
 * <ul>
 * <li>Servlets 2.5,
 * <li>spring-web 4.1.5.RELEASE,
 * <li>logback-classic <br />
 * </ul>
 * <br />
 * You have to define 2 things : <br />
 * <br />
 * 1. In JVM arguments (jvm or Tomcat vm arguments) define some arguments. I.e. <br />
 * -DmyConfigDir="d:/my/config/logback/" . <br />
 * Where "d:/my/config/logback/" is folder where you will put your config file. <br />
 * <br />
 * 2. In web.xml file create new context-param :
 * 
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;LogbackConfigFilename&lt;/param-name&gt;
 *     &lt;param-value&gt;file:${myConfigDir}/logback.xml&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * 
 * 
 * 
 * @author annik
 */
public class LogbackExternalListener implements ServletContextListener {

    /**
     * 
     */
    public static final String LOGBACK_CONFIG_FILE_NAME = "LogbackConfigFilename";

    public static final String LOGBACK_CONFIG_FILE_NAME_DEFAULT = "logback.xml";

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized( ServletContextEvent sce ) {

        Assert.checkNonNull( sce );

        ServletContext defaultServletContext = sce.getServletContext();
        defaultServletContext.log( this.getClass().getName() + " ~ Context initialized" );

        if ( defaultServletContext.getInitParameter( LOGBACK_CONFIG_FILE_NAME ) == null ) {
            defaultServletContext.log( "There is defined " + this.getClass().getName()
                    + ", but not found <context-param><param-name>" + LOGBACK_CONFIG_FILE_NAME
                    + "</param-name>... (in web.xml). Will try to use default logback.xml." );

        } else {

            String logbackConfigurationFile = "";

            String contextParamLogback = defaultServletContext.getInitParameter( LOGBACK_CONFIG_FILE_NAME );
            defaultServletContext.log( "Found param-name=`{" + LOGBACK_CONFIG_FILE_NAME + "}` , value=`{"
                    + contextParamLogback + "}`" );

            if ( contextParamLogback.matches( ".*(\\$\\{.*\\}).*" ) ) {
                String contextName =
                        contextParamLogback.substring( contextParamLogback.indexOf( "${" ),
                                contextParamLogback.indexOf( "}" ) + 1 );
                contextName = contextName.length() < 3 ? "   " : contextName;
                defaultServletContext
                        .log( "It seems there is not defined valid name VM arguments for param-name=`{"
                                + contextName + "}`! You have to"
                                + " use in VM arguments of Application server or JVM  … -D{"
                                + contextName.substring( 2, contextName.length() - 1 ) + "}=/path/to/dir … " );
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

                initLogback( ResourceUtils.getFile( logbackConfigurationFile ) );

            } catch (FileNotFoundException e) {
                defaultServletContext.log( "Logback configuration file `{" + contextParamLogback
                        + "}` not found." );
            }

        }

    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed( ServletContextEvent sce ) {

        sce.getServletContext().log( "contex DESTROYED" );

        ( (LoggerContext) LoggerFactory.getILoggerFactory() ).stop();

    }

    /**
     * Initialize Logback with given {@link File}
     * 
     * @param configPath
     */
    public static void initLogback( File configResource ) {

        Assert.checkNonNull( configResource );

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( context );
            configurator.doConfigure( configResource );

        } catch (JoranException e) {
            throw new RuntimeException( "Cannot initialize logback with given file `"
                    + configResource.getAbsolutePath() + "` ", e );
        }
    }

    /**
     * Initialize Logback with given file path.
     * 
     * @param configPath
     */
    public static void initLogback( String configPath ) {

        initLogback( new File( configPath ) );
    }

}
