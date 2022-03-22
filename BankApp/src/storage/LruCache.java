package storage;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public class LruCache<K, V> {
    private final Map<K, V> map;

    public LruCache(final int capacity) {
        this.map = Collections.synchronizedMap(new LinkedHashMap<K, V>(capacity, 0.75F, true) {
            protected boolean removeEldestEntry(Entry<K, V> eldest) {
                return this.size() > capacity;
            }
        });
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(this.map.get(key));
    }

    public void put(K key, V value) {
        this.map.put(key, value);
    }
}
