package storage;


import response.ResponseMessage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
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

    public ResponseMessage get(K key) {
        System.out.println("Get from Cache");
        return (ResponseMessage) this.map.get(key);
    }

    public void put(K key, V value) {
        System.out.println("Put inside Cache");
        this.map.put(key, value);
    }
}
