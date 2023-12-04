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
        Window window = Window.initInstance(1920, 1080, "Test Game", 800.0f, true, false, 4);
        window.init();

        ResourceManager.StaticModelData[] tetrahedron = ResourceManager.getInstance().loadStaticModel("./resources/models/extra/tetrahedron.gltf");

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
                .create("Piggyback")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("Tetrahedron")[0]))
                .buildAndRegister();

        EntityFactory
                .create("Light")
                .addComponent(new TransformComponent(new Vector3f(2.0f, 3.0f, 3.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("Piggyback")[0]))
                .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(1.0f, 1.0f, 1.0f)))
                .buildAndRegister();

        EntityFactory
                .create("Camera")
                .addComponent(new TransformComponent(new Vector3f(4.0f, 0.0f, 4.0f), new Vector3f(0.0f, 45.0f, 0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("Piggyback")[0]))
                .addComponent(new CameraComponent(50.0f, 0.1f, 1000.0f))
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

                Vector3f closestVertexPosition = new Vector3f();
                Vector3f closestSideHitMarkerPosition = new Vector3f();
                float closestSideHitMarkerDistance = Float.MAX_VALUE;
                for (int cubeSide = 0; cubeSide < 6; cubeSide++ ) {
                    Vector3f sideLocalPos = switch (cubeSide) {
                        case 0 -> new Vector3f(1.0f, 0.0f, 0.0f);
                        case 1 -> new Vector3f(-1.0f, 0.0f, 0.0f);
                        case 2 -> new Vector3f(0.0f, 1.0f, 0.0f);
                        case 3 -> new Vector3f(0.0f, -1.0f, 0.0f);
                        case 4 -> new Vector3f(0.0f, 0.0f, 1.0f);
                        case 5 -> new Vector3f(0.0f, 0.0f, -1.0f);
                        default -> new Vector3f();
                    };
                    Matrix3f rotationMatrix = new Matrix3f().identity();
                    rotationMatrix.rotateXYZ((float) Math.toRadians(xStep), (float) Math.toRadians(yStep), (float) Math.toRadians(zStep));
                    Vector3f sideWorldPos = rotationMatrix.transform(sideLocalPos);

                    // intersect every tetrahedron vertex with the cube side
                    for (int tetrahedronVertex = 0; tetrahedronVertex < 4; tetrahedronVertex++) {
                        Vector3f directionVector = tetrahedronVertices[tetrahedronVertex].normalize();
                        Vector3f origin = new Vector3f(0.0f);

                        // DON'T ROTATE THE DIRECTION VECTOR
                        // check for intersection between the ray and the plane
                        float denominator = directionVector.dot(sideWorldPos);
                        if (denominator == 0) continue;

                        float t = (sideWorldPos.dot(origin) + 1) / denominator;
                        if (t < 0) continue;

                        Vector3f hitMarkerPosition = new Vector3f(directionVector).mul(t);
                        hitMarkerPosition.add(origin);

                        Vector3f distanceFromVertexToHitMarker = new Vector3f(hitMarkerPosition).sub(tetrahedronVertices[tetrahedronVertex]);
                        float distanceFromVertexToHitMarkerLength = distanceFromVertexToHitMarker.length();

                        if (distanceFromVertexToHitMarkerLength < closestSideHitMarkerDistance) {
                            closestSideHitMarkerDistance = distanceFromVertexToHitMarkerLength;
                            closestSideHitMarkerPosition = hitMarkerPosition;
                            closestVertexPosition = tetrahedronVertices[tetrahedronVertex];
                        }
                    }
                }

                // scale the hit marker
                Vector3f fromVertexToHitMarker = new Vector3f(closestSideHitMarkerPosition).sub(closestVertexPosition);
                float distanceFromVertexToHitMarker = fromVertexToHitMarker.length();
                float vertexPosLength = closestVertexPosition.length();
                float scale = (distanceFromVertexToHitMarker + vertexPosLength) / vertexPosLength;

                transformComponent.setScale(new Vector3f(scale));

                // move the hit marker to the closest vertex
                hitMarkerTransformComponent.setPosition(closestVertexPosition);

                window.setTitle("x: " + xStep + " y: " + yStep + " z: " + zStep + " scale: " + scale);
                currentStep += 1;

                recordedSteps.add(new recordedStep(new Vector3f(transformComponent.getRotation()), scale));

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

        LogicManager.getInstance().registerLogic(new Logic(new Query("Piggyback")) {
            @Override
            protected void update(Entity[] queryTargets, float fixedDeltaTime) {

            }

            @Override
            protected void render(Entity[] queryTargets, float deltaTime) {
                TransformComponent transformComponent = (TransformComponent) queryTargets[0].getComponent(TransformComponent.class);
                transformComponent.setRotation(new Vector3f(0.0f, transformComponent.getRotation().y + 30.0f * deltaTime, 0.0f));
            }
        });

        window.run();
    }

}
