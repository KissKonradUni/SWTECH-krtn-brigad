package hu.krtn.brigad.test.tetrahedron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.EntityManager;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.ecs.component.StaticModelRendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.logic.Query;
import hu.krtn.brigad.engine.rendering.Texture;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.window.Logger;
import hu.krtn.brigad.engine.window.Window;
import org.joml.*;

import java.io.File;
import java.lang.Math;
import java.util.ArrayList;

public class TetrahedronProject {

    private static final Vector3f[] tetrahedronVertices = new Vector3f[] {
            new Vector3f( 0.0f       ,  1.05878000f,   0.0f),
            new Vector3f(-0.86602557f, -0.35292527f,   0.5f),
            new Vector3f( 0.86602557f, -0.35292527f,   0.5f),
            new Vector3f( 0.0f       , -0.35292527f,  -1.0f),
    };

    public static void main(String[] args) {
        Window window = Window.initInstance(1920, 1080, "Test Game", 5.0f, true, false, 4);
        window.init();

        ResourceManager.StaticModelData[] tetrahedron = ResourceManager.getInstance().loadStaticModel("./resources/models/extra/tetrahedron.gltf");

        EntityFactory
                .create("Camera")
                .addComponent(new TransformComponent(new Vector3f(4.0f, 0.0f, 4.0f), new Vector3f(0.0f, 45.0f, 0.0f), new Vector3f(1.0f)))
                .addComponent(new CameraComponent(50.0f, 0.1f, 1000.0f))
                .buildAndRegister();

        EntityFactory
                .create("Light")
                .addComponent(new TransformComponent(new Vector3f(2.0f, 3.0f, 3.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
                .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(1.0f, 1.0f, 1.0f)))
                .buildAndRegister();

        EntityFactory
                .create("HitMarker")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(0.05f)))
                .addComponent(
                        new StaticModelRendererComponent(
                                ResourceManager.getInstance().loadStaticModel("./resources/models/extra/cube.gltf")[0],
                                new Texture(new Texture.ByteColor(new Vector4f(0.5f, 0.5f, 1.0f, 0.5f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ).setAlphaBlending(true))
                .buildAndRegister();

        for (Vector3f v : tetrahedronVertices) {
            EntityFactory
                    .create("CornerMarker")
                    .addComponent(new TransformComponent(v, new Vector3f(0.0f), new Vector3f(0.025f)))
                    .addComponent(
                            new StaticModelRendererComponent(
                                    ResourceManager.getInstance().loadStaticModel("./resources/models/extra/cube.gltf")[0],
                                    new Texture(new Texture.ByteColor(new Vector4f(1.5f, 0.5f, 0.0f, 0.5f))),
                                    ResourceManager.getInstance().loadShader(
                                            "./resources/shaders/vertex/pbr.glsl",
                                            "./resources/shaders/fragment/pbr.glsl"
                                    )
                            ).setAlphaBlending(true))
                    .buildAndRegister();
        }

        EntityFactory
                .create("Tetrahedron")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
                .addComponent(
                        new StaticModelRendererComponent(
                                tetrahedron[0],
                                new Texture(new Texture.ByteColor(new Vector4f(1.0f, 0.5f, 0.5f, 0.5f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ).setAlphaBlending(true))
                .buildAndRegister();

        EntityFactory
                .create("Cube")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
                .addComponent(
                        new StaticModelRendererComponent(
                                ResourceManager.getInstance().loadStaticModel("./resources/models/extra/cube.gltf")[0],
                                new Texture(new Texture.ByteColor(new Vector4f(0.5f, 1.0f, 0.5f, 0.5f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ).setAlphaBlending(true))
                .buildAndRegister();

        LogicManager.getInstance().registerLogic(new Logic(new Query("Cube")) {
            static class recordedStep {
                final Vector3f rotation;
                final float scale;

                public recordedStep(Vector3f rotation, float scale) {
                    this.rotation = rotation;
                    this.scale = scale;
                }
            }
            private final ArrayList<recordedStep> recordedSteps = new ArrayList<>();
            private int currentStep = 0;
            private TransformComponent hitMarkerTransformComponent;

            @Override
            protected void update(Entity[] queryTargets, float fixedDeltaTime) {
                if (hitMarkerTransformComponent == null)
                    hitMarkerTransformComponent = (TransformComponent) EntityManager.getInstance().getEntitiesByName("HitMarker")[0].getComponent(TransformComponent.class);

                TransformComponent transformComponent = (TransformComponent) queryTargets[0].getComponent(TransformComponent.class);

                int xStep = currentStep % 9;
                xStep *= 10;
                int yStep = (currentStep / 9) % 18;
                yStep *= 10;
                int zStep = (currentStep / 9 / 18) % 36;
                zStep *= 10;

                transformComponent.setRotation(new Vector3f(xStep, yStep, zStep));
                window.setTitle("x: " + xStep + " y: " + yStep + " z: " + zStep);

                // find the closest side of the cube to a tetrahedron vertex
                // and scale the cube to that size
                float minDistance = Float.MAX_VALUE;
                Vector3f hitMarkerPosition = new Vector3f();
                int closestSideIndex = 0;

                for (int i = 0; i < 6; i++) {
                    // find the center of the side
                    Vector3f sideCenter = switch (i) {
                        case 0 -> new Vector3f( 0.0f,  0.0f,  1.0f);
                        case 1 -> new Vector3f( 0.0f,  0.0f, -1.0f);
                        case 2 -> new Vector3f( 0.0f,  1.0f,  0.0f);
                        case 3 -> new Vector3f( 0.0f, -1.0f,  0.0f);
                        case 4 -> new Vector3f( 1.0f,  0.0f,  0.0f);
                        case 5 -> new Vector3f(-1.0f,  0.0f,  0.0f);
                        default -> new Vector3f();
                    };
                    sideCenter.rotate(new Quaternionf().fromAxisAngleDeg(1.0f, 0.0f, 0.0f, xStep));
                    sideCenter.rotate(new Quaternionf().fromAxisAngleDeg(0.0f, 1.0f, 0.0f, yStep));
                    sideCenter.rotate(new Quaternionf().fromAxisAngleDeg(0.0f, 0.0f, 1.0f, zStep));

                    // find the closest vertex of the tetrahedron to the side
                    for (Vector3f tetrahedronVertex : TetrahedronProject.tetrahedronVertices) {
                        float distanceToSide = sideCenter.distance(tetrahedronVertex);
                        if (distanceToSide < minDistance) {
                            minDistance = distanceToSide;

                            // hit marker position
                            hitMarkerPosition = new Vector3f(tetrahedronVertex);

                            // closest side index
                            closestSideIndex = i;
                        }
                    }
                }

                // scale the cube to the closest side
                // transformComponent.setScale(new Vector3f(hitMarkerPosition.length()));

                // this was incorrect, we need to transform into the cube's space,
                // then scale, then transform back and measure the distance
                // to the center of the cube
                Matrix3f cubeTransformMatrix = new Matrix3f().identity();
                cubeTransformMatrix.rotate(new Quaternionf().fromAxisAngleDeg(1.0f, 0.0f, 0.0f, xStep));
                cubeTransformMatrix.rotate(new Quaternionf().fromAxisAngleDeg(0.0f, 1.0f, 0.0f, yStep));
                cubeTransformMatrix.rotate(new Quaternionf().fromAxisAngleDeg(0.0f, 0.0f, 1.0f, zStep));

                Vector3f hitMarkerPosition3f = new Vector3f(hitMarkerPosition).mul(cubeTransformMatrix);

                // this only works upwards,
                // we need to select the closest side's axis
                // float scale = Math.abs(hitMarkerPosition4f.y);

                // this works
                float scale = Math.abs(switch (closestSideIndex) {
                    case 0, 1 -> hitMarkerPosition3f.z;
                    case 2, 3 -> hitMarkerPosition3f.y;
                    case 4, 5 -> hitMarkerPosition3f.x;
                    default -> 0.0f;
                });

                // scale the cube to the closest side
                transformComponent.setScale(new Vector3f(scale));

                // move the hit marker to the closest vertex
                hitMarkerTransformComponent.setPosition(hitMarkerPosition);

                currentStep += 1;

                recordedSteps.add(new recordedStep(new Vector3f(transformComponent.getRotation()), hitMarkerPosition.length()));

                if (currentStep == 9 * 18 * 36) {
                    Logger.log("Finished recording steps");
                    Logger.log("Steps: " + recordedSteps.size());

                    // save as json
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(recordedSteps);

                    // save as file using java nio
                    File file = new File("./recordedSteps.json");
                    try {
                        java.nio.file.Files.writeString(file.toPath(), json);
                    } catch (Exception e) {
                        Logger.error("Failed to save recorded steps to file");
                    }

                    this.setEnabled(false);
                }
            }

            @Override
            protected void render(Entity[] queryTargets, float deltaTime) {

            }
        });

        window.run();
    }

}
