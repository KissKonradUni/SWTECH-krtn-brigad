package hu.krtn.brigad.engine.window;

import imgui.ImGui;

import java.util.ArrayList;

public class ImGuiLayer {

    private static ImGuiLayer INSTANCE;

    private final ArrayList<Runnable> renderables = new ArrayList<>();

    private ImGuiLayer() {

    }

    public static ImGuiLayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImGuiLayer();
        }
        return INSTANCE;
    }

    private boolean showText = false;

    public void render() {
        renderables.forEach(Runnable::run);
    }

    public void registerRenderable(Runnable renderable) {
        renderables.add(renderable);
    }

    public void unregisterRenderable(Runnable renderable) {
        renderables.remove(renderable);
    }

}
