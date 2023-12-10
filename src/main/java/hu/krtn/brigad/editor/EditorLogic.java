package hu.krtn.brigad.editor;

import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityManager;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;
import hu.krtn.brigad.engine.window.ImGuiLayer;
import hu.krtn.brigad.engine.window.Logger;
import hu.krtn.brigad.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class EditorLogic extends Logic {
    public EditorLogic() {
        super(new Query("EditorDataHolder"));
        setEnabled(true);

        ImGuiLayer.getInstance().registerRenderable(this::renderIMGui);
    }

    private final int statusBarFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
    private final int topMenuFlags = statusBarFlags | ImGuiWindowFlags.MenuBar;
    private boolean firstFrame = true;

    private boolean showEntityList = false;
    private boolean showProperties = false;
    private boolean showAssets = false;
    private boolean showScripts = false;

    private void renderIMGui() {
        topMenu();
        statusBar();
        entityList();
        properties();

        firstFrame = false;
    }

    private void topMenu() {
        ImGui.begin("Editor",
            topMenuFlags
        );
        if (firstFrame) {
            ImGui.setWindowPos(0, 0);
            ImGui.setWindowSize(Window.getInstance().getWidth(), 68);
        }

        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save")) {
                Logger.log("Save");
            }
            if (ImGui.menuItem("Load")) {
                Logger.log("Load");
            }
            ImGui.endMenu();
        }

        ImGui.endMenuBar();

        if (ImGui.button("E ")) {
            showEntityList = !showEntityList;
        }
        ImGui.sameLine();
        if (ImGui.button("P ")) {
            showProperties = !showProperties;
        }
        ImGui.sameLine();
        if (ImGui.button("A ")) {
            showAssets = !showAssets;
        }
        ImGui.sameLine();
        if (ImGui.button("S ")) {
            showScripts = !showScripts;
        }
        // Move the next button to the right
        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - 155, 0);
        ImGui.sameLine();
        if (ImGui.button("Save")) {
            Logger.log("Save");
        }
        ImGui.sameLine();
        if (ImGui.button("Settings")) {
            Logger.log("Settings");
        }

        ImGui.end();
    }

    private final DecimalFormat df = new DecimalFormat("000.00", new DecimalFormatSymbols(Locale.US));

    private void statusBar() {
        ImGui.begin("Status Bar",
                statusBarFlags
        );
        // move to the bottom of the screen
        ImGui.setWindowPos(0, Window.getInstance().getHeight() - 32);
        ImGui.setWindowSize(Window.getInstance().getWidth(), 36);

        //noinspection StringBufferReplaceableByString
        StringBuilder statusText = new StringBuilder();
        statusText.append("Engine Version: ").append("0.1.0");  // TODO: get version from somewhere
        statusText.append(" | ");
        statusText.append("FPS: ").append(df.format(1.0f / Window.getInstance().getDeltaTime()));
        statusText.append(" | ");
        statusText.append("TPS: ").append(df.format(1.0f / Window.getInstance().getFixedDeltaTime()));
        statusText.append(" / ").append(Window.getInstance().getTickRate());

        ImGui.text(statusText.toString());

        ImGui.end();
    }

    private Entity selectedEntity = null;

    private void entityList() {
        if (!showEntityList) {
            return;
        }

        ImGui.begin("Entity List");
        ImGui.text("Selected: " + (selectedEntity == null ? "None" : selectedEntity.getName()));

        EntityManager.getInstance().getEntities().forEach(entity -> {
            if (ImGui.button(entity.getName(), 250.0f, 32.0f)) {
                selectedEntity = entity;
            }
        });

        ImGui.end();
    }

    private void properties() {
        if (!showProperties) {
            return;
        }

        ImGui.begin("Properties");
        ImGui.text("Selected: " + (selectedEntity == null ? "None" : selectedEntity.getName()));

        if (selectedEntity != null) {
            selectedEntity.getComponents().forEach(component -> {
                ImGui.separator();
                ImGui.text(component.getSimpleName());
                ImGui.separator();

                ExposedFields exposedFields = component.getExposedFields();

                if (exposedFields.getFields().isEmpty()) {
                    component.initExposedFields();
                }

                exposedFields.getFields().forEach((name, value) -> {
                    ImGui.text(name);
                    ImGui.sameLine(150.0f);
                    ImGui.pushItemWidth(-1);
                    switch (value.type.getSimpleName()) {
                        case "String":
                            ExposedFields.StringField stringField = (ExposedFields.StringField) value;

                            ImString imString = new ImString(stringField.getter.get());
                            if (ImGui.inputText(name, imString))
                                if (stringField.setter != null)
                                    stringField.setter.accept(imString.get());
                            break;
                        case "Float":
                            ExposedFields.FloatField floatField = (ExposedFields.FloatField) value;

                            ImFloat imFloat = new ImFloat(floatField.getter.get());
                            if (ImGui.inputFloat(name, imFloat))
                                if (floatField.setter != null)
                                    floatField.setter.accept(imFloat.get());
                            break;
                        case "Integer":
                            ExposedFields.IntegerField integerField = (ExposedFields.IntegerField) value;

                            ImInt imInt = new ImInt(integerField.getter.get());
                            if (ImGui.inputInt(name, imInt))
                                if (integerField.setter != null)
                                    integerField.setter.accept(imInt.get());
                            break;
                        case "Boolean":
                            ExposedFields.BooleanField booleanField = (ExposedFields.BooleanField) value;

                            ImBoolean imBoolean = new ImBoolean(booleanField.getter.get());
                            if (ImGui.checkbox(name, imBoolean))
                                if (booleanField.setter != null)
                                    booleanField.setter.accept(imBoolean.get());
                            break;
                        case "Vector3f":
                            ExposedFields.Vector3fField vector3fField = (ExposedFields.Vector3fField) value;

                            Vector3f vector3f = vector3fField.getter.get();
                            float[] floats = new float[]{vector3f.x, vector3f.y, vector3f.z};
                            if (ImGui.inputFloat3(name, floats))
                                if (vector3fField.setter != null)
                                    vector3fField.setter.accept(new Vector3f(floats));
                            break;
                        case "Vector4f":
                            ExposedFields.Vector4fField vector4fField = (ExposedFields.Vector4fField) value;

                            Vector4f vector4f = vector4fField.getter.get();
                            float[] floats2 = new float[]{vector4f.x, vector4f.y, vector4f.z, vector4f.w};
                            if (ImGui.inputFloat4(name, floats2))
                                if (vector4fField.setter != null)
                                    vector4fField.setter.accept(new Vector4f(floats2));
                            break;
                        case "EnumField":
                            ExposedFields.EnumField enumField = (ExposedFields.EnumField) value;

                            ImInt imInt2 = new ImInt(enumField.getter.get());
                            if (ImGui.combo(name, imInt2, enumField.values, enumField.values.length))
                                if (enumField.setter != null)
                                    enumField.setter.accept(imInt2.get());
                            break;
                        case "ColorField":
                            ExposedFields.ColorField colorField = (ExposedFields.ColorField) value;

                            Vector3f vector3f2 = colorField.getter.get();
                            float[] floats3 = new float[]{vector3f2.x, vector3f2.y, vector3f2.z};
                            if (ImGui.colorEdit3(name, floats3))
                                if (colorField.setter != null)
                                    colorField.setter.accept(new Vector3f(floats3));
                            break;
                        default:
                            ImGui.text("Unsupported type: " + value.type.getSimpleName());
                            break;
                    }
                    ImGui.popItemWidth();
                });

                ImGui.separator();
            });
        }

        ImGui.end();
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {

    }
}
