/**
 * Copyright 2015. ABN Software. All Rights reserved.<br>
 * <br>
 * Homepage .... http://www.ABNsoft.info<br>
 * <br>
 * Project ..... AbnLogbackExternal<br>
 * <br>
 * Author ...... AnNik<br>
 * E-Mail ...... ABN.DEV@mail.ru<br>
 * Created ..... 05 марта 2015 г.<br>
 * <br>
 */
package info.abnsoft.java.servlet.exception;

/**
 * @author annik
 *
 */
public class NoFoundServletContext extends RuntimeException {

    private static final String NOT_FOUND_SERVLET_CONTEXT = "Not found ServletContext";

    private static final long serialVersionUID = 1L;

    /**
     * Contructor.
     */
    public NoFoundServletContext() {

        super( NOT_FOUND_SERVLET_CONTEXT );
    }

    /**
     * Contructor.
     * 
     * @param message
     */
    public NoFoundServletContext( String message ) {

        super( NOT_FOUND_SERVLET_CONTEXT + message );
    }

    /**
     * Contructor.
     * 
     * @param throwable
     */
    public NoFoundServletContext( Throwable throwable ) {

        super( NOT_FOUND_SERVLET_CONTEXT, throwable );
    }

    /**
     * Contructor.
     * 
     * @param message
     * @param thtowable
     */
    public NoFoundServletContext( String message, Throwable thtowable ) {

        super( NOT_FOUND_SERVLET_CONTEXT + message, thtowable );
    }

}
