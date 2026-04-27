package schoolhelpdesk.util;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    private static final Logger logger = Logger.getLogger(LoggerConfig.class.getName());
    
    static {
        // Configure simple logging to suppress MongoDB warnings
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.WARNING);
        
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.WARNING);
        handler.setFormatter(new SimpleFormatter());
        
        // Remove existing handlers and add our configured one
        for (java.util.logging.Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }
        rootLogger.addHandler(handler);
        
        // Suppress MongoDB driver warnings specifically
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
    }
    
    public static void info(String message) {
        logger.info(message);
    }
    
    public static void warning(String message) {
        logger.warning(message);
    }
    
    public static void error(String message) {
        logger.severe(message);
    }
    
    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
