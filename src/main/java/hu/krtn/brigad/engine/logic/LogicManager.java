package hu.krtn.brigad.engine.logic;

import hu.krtn.brigad.engine.ecs.EntityManager;

import java.util.ArrayList;

/**
 * The LogicManager class is responsible for calling the registered
 * {@link Logic} objects' update and render methods.
 *
 * @see Logic
 */
public class LogicManager {

    private final ArrayList<Logic> logics;

    private static LogicManager INSTANCE;

    private LogicManager() {
        logics = new ArrayList<>();
    }

    public static LogicManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogicManager();
        }
        return INSTANCE;
    }

    /**
     * Calls the update method of the registered {@link Logic} objects.
     * @param fixedDeltaTime The time elapsed since the last update.
     */
    public void update(float fixedDeltaTime) {
        for (Logic logic : logics) {
            logic.CallUpdate(fixedDeltaTime);
        }
        EntityManager.getInstance().setDirty(false);
    }

    /**
     * Calls the render method of the registered {@link Logic} objects.
     * @param deltaTime The time elapsed since the last render.
     */
    public void render(float deltaTime) {
        for (Logic logic : logics) {
            logic.CallRender(deltaTime);
        }
        EntityManager.getInstance().setDirty(false);
    }

    /**
     * Registers a {@link Logic} object.
     * @param logic The {@link Logic} object to register.
     */
    public void registerLogic(Logic logic) {
        logics.add(logic);
    }

    /**
     * Unregisters a {@link Logic} object.
     * @param logic The {@link Logic} object to unregister.
     */
    public void unregisterLogic(Logic logic) {
        logics.remove(logic);
    }

    /**
     * Unregisters all {@link Logic} objects.
     */
    public void reset() {
        logics.clear();
    }

    /**
     * Checks if a {@link Logic} object is registered.
     * @param classQuery The {@link Logic} object to check.
     * @return Whether the {@link Logic} object is registered or not.
     */
    public boolean isLogicPresent(Class<? extends Logic> classQuery) {
        boolean result = false;
        for (Logic logic : logics) {
            if (logic.getClass().equals(classQuery)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
