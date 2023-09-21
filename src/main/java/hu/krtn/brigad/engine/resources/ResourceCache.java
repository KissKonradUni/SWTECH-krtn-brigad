package hu.krtn.brigad.engine.resources;

import java.util.HashMap;

/**
 * A generic resource cache.
 * @param <T> The type of the resource.
 */
public class ResourceCache<T> {

    private final HashMap<String, T> resources;

    public ResourceCache() {
        resources = new HashMap<>();
    }

    public T get(String key) {
        return resources.get(key);
    }

    public void put(String key, T resource) {
        resources.put(key, resource);
    }

    public boolean has(String key) {
        return resources.containsKey(key);
    }

}
