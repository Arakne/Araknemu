package fr.quatrevieux.araknemu.game.admin;

/**
 * Logger for admin console
 *
 * The log message can be formatted using "{}" placeholder.
 *
 * To log a simple success message :
 * logger.success("Command successfully executed");
 *
 * Log an error with an argument :
 * logger.error("The argument {} is not valid", arg);
 *
 * Passing multiple arguments
 * logger.info("Hello {}, my name is {} and I'm {} y-o", "John", "Alan", 36);
 */
public interface AdminLogger {
    /**
     * Log to admin console
     *
     * @param type The log type
     * @param message The log message
     * @param arguments The message arguments
     */
    public void log(LogType type, String message, Object... arguments);

    /**
     * Log an information message (white)
     *
     * @param message Message to log
     * @param arguments Message arguments
     */
    default public void info(String message, Object... arguments) {
        log(LogType.DEFAULT, message, arguments);
    }

    /**
     * Log an error message (red)
     *
     * @param message Message to log
     * @param arguments Message arguments
     */
    default public void error(String message, Object... arguments) {
        log(LogType.ERROR, message, arguments);
    }

    /**
     * Log a success message (green)
     *
     * @param message Message to log
     * @param arguments Message arguments
     */
    default public void success(String message, Object... arguments) {
        log(LogType.SUCCESS, message, arguments);
    }
}
