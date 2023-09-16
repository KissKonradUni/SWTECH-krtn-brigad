package hu.krtn.brigad.engine.io;

import java.util.HashMap;

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
