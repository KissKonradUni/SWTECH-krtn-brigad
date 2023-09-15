package hu.krtn.brigad.engine.window;

import hu.krtn.brigad.engine.logic.LogicManager;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * The main window of the game.
 */
public class Window {

    /**
     * A separate thread that runs the game logic.
     */
    private static class LogicThread extends Thread {

        /**
         * The rate at which the logic thread runs per second.
         */
        private final float tickRate;
        /**
         * The time between the last two logic thread runs.
         * Ideally, this should be equal to 1.0f / tickRate.
         * If the logic thread is running slower than the tick rate,
         * this value will be greater than 1.0f / tickRate.
         */
        private float       deltaTime;

        /**
         * Whether the logic thread should run or not.
         */
        private boolean shouldRun = true;

        /**
         * Creates a new logic thread with the given tick rate.
         * @param tickRate The rate at which the logic thread runs per second.
         */
        public LogicThread(float tickRate) {
            this.tickRate = tickRate;
        }

        /**
         * The main loop of the logic thread.
         */
        @Override
        public void run() {
            float runStart ;
            float runEnd   ;
            float runTime  ;
            float sleepTime;

            while (shouldRun) {
                // Start measuring the time it takes to run the logic thread
                runStart = (float) glfwGetTime();

                LogicManager.getInstance().update(deltaTime);

                // End measuring the time it takes to run the logic thread
                runEnd  = (float) glfwGetTime();
                runTime = runEnd - runStart;

                // Calculate the time the logic thread should sleep
                sleepTime = (1000.0f / tickRate) - runTime * 1000.0f;

                try {
                    // If the logic thread is running faster than the tick rate,
                    // sleep for the remaining time.
                    if (sleepTime > 0)
                        //noinspection BusyWait
                        Thread.sleep((long) sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Calculate the time between the last two logic thread runs
                deltaTime = sleepTime / 1000.0f + runTime;
            }
        }

        /**
         * Stops the logic thread.
         */
        public void stopRunning() {
            shouldRun = false;
        }

        /**
         * Returns the rate at which the logic thread runs per second.
         * @return The set tick rate.
         */
        public float getTickRate() {
            return tickRate;
        }

        /**
         * Returns the time between the last two logic thread runs.
         * @return The fixed delta time.
         */
        public float getDeltaTime() {
            return deltaTime;
        }

    }

    /**
     * The handle of the GLFW window.
     */
    private long windowHandle;

    /**
     * The logic thread of the game.
     */
    private LogicThread logicThread;

    private int width;
    private int height;
    private String title;
    private float tickRate;
    private boolean vsync;
    private boolean fullscreen;

    /**
     * Creates a new window with the given width, height and title.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param title The title of the window.
     * @param tickRate The rate at which the logic thread runs per second.
     *                 This value should be greater than 0.
     * @param vsync Whether the window should use vsync or not.
     * @param fullscreen Whether the window should be fullscreen or not.
     */
    public Window(int width, int height, String title, float tickRate, boolean vsync, boolean fullscreen) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.tickRate = tickRate;
        this.vsync = vsync;
        this.fullscreen = fullscreen;
    }

    /**
     * Runs the game.
     */
    public void run() {
        init();

        logicThread = new LogicThread(tickRate);
        logicThread.start();

        loop();

        logicThread.stopRunning();
    }

    /**
     * Initializes the window.
     */
    private void init() {
        // Put the error messages on the standard error stream
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        // Set the window to fullscreen if needed
        windowHandle = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        if (windowHandle == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // TODO: Implement a real input system
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        // Center the window on the primary monitor
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                windowHandle,
                (videoMode.width() - pWidth.get(0)) / 2,
                (videoMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(windowHandle);
        // Enable vsync if needed
        glfwSwapInterval(vsync ? 1 : 0);

        glfwShowWindow(windowHandle);
    }

    /**
     * The main rendering loop of the game.
     */
    private void loop() {
        // Create the OpenGL context
        GL.createCapabilities();

        // Set the clear color to red
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        float lastFrameTime    = 0.0f;
        float currentFrameTime = 0.0f;
        float deltaTime        = 0.0f;

        while (!glfwWindowShouldClose(windowHandle)) {
            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Swap the front and back buffers
            glfwSwapBuffers(windowHandle);

            // Render the game
            LogicManager.getInstance().render(deltaTime);

            // Calculate the time between the last two frames
            currentFrameTime = (float) glfwGetTime();
            deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            // Set the window title
            glfwSetWindowTitle(
                    windowHandle,
                    title +
                        " | FPS: " + String.format("%06.2f", 1.0f / deltaTime) +
                        " | UPS: " + String.format("%04.1f", 1.0f / logicThread.getDeltaTime()) +
                        "/" + logicThread.getTickRate()
            );

            // Poll for window events
            glfwPollEvents();
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}