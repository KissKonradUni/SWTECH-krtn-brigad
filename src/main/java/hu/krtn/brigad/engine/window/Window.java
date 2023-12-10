package hu.krtn.brigad.engine.window;

import hu.krtn.brigad.engine.logic.LogicManager;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Locale;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * The main window of the game.
 */
public class Window {

    private static Window INSTANCE;

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
                    Logger.error(e.getMessage());
                }

                // Calculate the time between the last two logic thread runs
                deltaTime = sleepTime > 0 ? sleepTime / 1000.0f + runTime : runTime;
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
    private int msaa;

    // TODO: Move this to a separate class
    private float deltaTime;
    private float time;

    /**
     *  IMGUI
     *  */
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private String glslVersion = null;

    /**
     * Creates a new window with the given width, height and title.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param title The title of the window.
     * @param tickRate The rate at which the logic thread runs per second.
     *                 This value should be greater than 0.
     * @param vsync Whether the window should use vsync or not.
     * @param fullscreen Whether the window should be fullscreen or not.
     * @param msaa The number of samples to use for multisample antialiasing.
     */
    private Window(int width, int height, String title, float tickRate, boolean vsync, boolean fullscreen, int msaa) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.tickRate = tickRate;
        this.vsync = vsync;
        this.fullscreen = fullscreen;
        this.msaa = msaa;
    }

    /**
     * Initializes the window instance.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param title The title of the window.
     * @param tickRate The rate at which the logic thread runs per second.
     * @param vsync Whether the window should use vsync or not.
     * @param fullscreen Whether the window should be fullscreen or not.
     * @param msaa The number of samples to use for multisample antialiasing.
     * @return The initialized window instance.
     */
    public static Window initInstance(int width, int height, String title, float tickRate, boolean vsync, boolean fullscreen, int msaa) {
        if (INSTANCE == null) {
            INSTANCE = new Window(width, height, title, tickRate, vsync, fullscreen, msaa);
        } else {
            Logger.error("Window already initialized");
        }
        return INSTANCE;
    }

    /**
     * Returns the instance of the window.
     * @return The instance of the window.
     */
    public static Window getInstance() {
        return INSTANCE;
    }

    /**
     * Runs the game.
     */
    public void run() {
        logicThread = new LogicThread(tickRate);
        logicThread.start();
        Logger.log("Logic thread started");

        loop();
        Logger.log("Window closed");

        logicThread.stopRunning();
        Logger.log("Logic thread stopped");
    }

    /**
     * Initializes the window.
     */
    public void init() {
        // Put the error messages on the standard error stream
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, msaa);

        glslVersion = "#version 410 core";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        // Create the window
        // Set the window to fullscreen if needed
        windowHandle = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : 0, 0);
        if (windowHandle == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        Logger.log("Window created");

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

            if (videoMode == null) {
                Logger.error("Failed to get the video mode of the primary monitor");
                return;
            }

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

        // Create the OpenGL context
        GL.createCapabilities();
        Logger.log("OpenGL version: " + glGetString(GL_VERSION));
        Logger.log("OpenGL context created.");

        initImGui();
    }

    private void initImGui() {
        Logger.log("Initializing ImGui");

        ImGui.createContext();
        //ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init(glslVersion);

        float scale = (float) width / 1280;
        ImGui.getIO().setFontGlobalScale(scale);

        Logger.log("ImGui initialized");
    }

    /**
     * The main rendering loop of the game.
     */
    private void loop() {
        // Clear the color and depth buffers
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        float lastFrameTime    = 0.0f;
        float currentFrameTime = 0.0f;
              deltaTime        = 0.0f;

        // enable depth testing
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        if (msaa > 0)
            glEnable(GL_MULTISAMPLE);

        while (!glfwWindowShouldClose(windowHandle)) {
            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            // Render the game
            LogicManager.getInstance().render(deltaTime);

            ImGuiLayer.getInstance().render();

            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            endFrame(deltaTime);

            // Calculate the time between the last two frames
            currentFrameTime = (float) glfwGetTime();
            deltaTime = currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            time += deltaTime;

            // Set the window title
            //glfwSetWindowTitle(
            //        windowHandle,
            //        title +
            //            " | FPS: " + String.format(Locale.ENGLISH, "%06.2f", 1.0f / deltaTime) +
            //            " | UPS: " + String.format(Locale.ENGLISH, "%04.1f", 1.0f / logicThread.getDeltaTime()) +
            //            "/" + logicThread.getTickRate()
            //);

            // Swap the front and back buffers
            glfwSwapBuffers(windowHandle);

            // Poll for window events
            glfwPollEvents();
        }
    }

    private void endFrame(float deltaTime) {
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindow = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindow);
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

    public float getAspectRatio() {
        return (float) width / height;
    }

    public void destroy() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public float getTime() {
        return time;
    }

    public float getFixedDeltaTime() {
        return logicThread.getDeltaTime();
    }

    public float getTickRate() {
        return logicThread.getTickRate();
    }
}