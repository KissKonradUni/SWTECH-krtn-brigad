package hu.krtn.brigad.engine.window;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;

/**
 * A simple logger class.
 */
public class Logger {

    private static final String[] colors = {
        "\u001B[30m", // black
        "\u001B[31m", // red
        "\u001B[32m", // green
        "\u001B[33m", // yellow
        "\u001B[34m", // blue
        "\u001B[35m", // purple
        "\u001B[36m", // cyan
        "\u001B[37m", // white
        "\u001B[39m", // default
        "\u001B[90m", // bright black
        "\u001B[91m", // bright red
        "\u001B[92m", // bright green
        "\u001B[93m", // bright yellow
        "\u001B[94m", // bright blue
        "\u001B[95m", // bright purple
        "\u001B[96m", // bright cyan
        "\u001B[97m", // bright white
    };
    private static final String reset = "\u001B[0m";

    /**
     * Returns the current timestamp in the format of HH:mm:ss.
     * @return The current timestamp.
     */
    private static String getTimestamp() {
        return LocalDateTime.now().format(
            new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter()
        );
    }

    /**
     * Logs a message to the console.
     * @param message The message to log.
     */
    public static void log(String message) {
        System.out.printf(
            "[%s%s%s][%sINFO%s] %s%n", colors[3], getTimestamp(), reset, colors[15], reset, message
        );
    }

    /**
     * Logs a warning to the console.
     * @param message The message to log.
     */
    public static void warn(String message) {
        System.out.printf(
            "[%s%s%s][%sWARN%s] %s%n", colors[3], getTimestamp(), reset, colors[12], reset, message
        );
    }

    /**
     * Logs an error to the console.
     * @param message The message to log.
     */
    public static void error(String message) {
        message += "\n    " + Arrays.toString(Arrays.stream(Thread.currentThread().getStackTrace()).skip(2).toArray()).replace(",", "\n    ");
        System.out.printf(
            "[%s%s%s][%sERROR%s] %s%n", colors[3], getTimestamp(), reset, colors[10], reset, message
        );
    }

}
