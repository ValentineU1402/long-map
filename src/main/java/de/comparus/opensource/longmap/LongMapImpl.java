package de.comparus.opensource.longmap;

import java.util.*;

public class LongMapImpl<V> implements LongMap<V> {
    private static final float LOAD_FACTORY = 0.75F;
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private Entry<V>[] table;
    private int capacity;
    private int size;

    public LongMapImpl() {
        this.capacity = DEFAULT_INITIAL_CAPACITY;
        this.size = 0;
        this.table = new Entry[capacity];
    }

    public LongMapImpl(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    capacity);
        if (capacity > MAXIMUM_CAPACITY) {
            this.capacity = MAXIMUM_CAPACITY / 1990800;
        } else {
            this.capacity = capacity;
        }
        this.size = 0;
        this.table = new Entry[this.capacity];
    }

    public V put(long key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, capacity);
        if (size > capacity * LOAD_FACTORY) {
            resize();
        }
        if (key != 0) {
            if (table[index] == null) {
                addEntry(key, value, hash, index);
            } else if (table[index].getHash() == hash && table[index].getKey() == key) {
                table[index].setValue(value);
            } else {
                addNextEntry(key, value, hash, index);
            }
        } else {
            putForZeroKey(key, value);
        }
        return value;
    }

    public V get(long key) {
        int index = indexFor(hash(key), capacity);
        if (table[index] != null) {
            Entry<V> result = table[index];
            return result.hasNext() ? iterateEntry(result, key).getValue() : result.getValue();
        }
        throw new IllegalArgumentException("Key not found");
    }

    public V remove(long key) {
        if (containsKey(key)) {
            V values = get(key);
            int index = indexFor(hash(key), capacity);
            table[index] = table[index].hasNext() ? iterateEntry(table[index], key) : null;
            size--;
            return values;
        }
        throw new IllegalArgumentException("Key not found");
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        int index = indexFor(hash(key), capacity);
        if (table[index] != null)
            return iterateEntry(table[index], key).getKey() == key;

        return false;
    }

    public boolean containsValue(V value) {
        Optional<Entry<V>> result = Arrays.stream(table)
                .filter(Objects::nonNull)
                .filter(entry -> entry.getValue() == value).findFirst();
        return result.isPresent();
    }

    public long[] keys() {
        long[] keys = Arrays.stream(table)
                .filter(Objects::nonNull)
                .mapToLong(Entry::getKey).toArray();
        return keys;
    }

    public V[] values() {
        V[] values = (V[]) Arrays.stream(table).filter(Objects::nonNull)
                .map(Entry::getValue).toArray();
        return values;
    }

    public long size() {
        return size;
    }

    public void clear() {
        table = new Entry[capacity];
        size = 0;
    }

    private Entry<V> iterateEntry(Entry<V> entry, long key) {
        while (entry.hasNext()) {
            if (entry.getKey() == key) {
                entry = entry.next;
            }
        }
        return entry;
    }

    private void resize() {
        if (table.length >= MAXIMUM_CAPACITY) {
            return;
        }
        int newLength = (capacity * 2);
        Entry<V>[] newTable = new Entry[newLength];
        transfer(newTable);
        table = newTable;
    }

    private void transfer(Entry<V>[] newEntry) {
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                int index = indexFor(table[i].hash, table.length);
                newEntry[index] = table[i];
            }
        }
    }

    private void addNextEntry(long key, V value, int hash, int index) {
        this.table[index].setNext(new Entry<>(key, value, hash));
        size++;
    }

    private void putForZeroKey(long key, V value) {
        addEntry(key, value, 0, 0);
    }

    private void addEntry(long key, V value, int hash, int index) {
        this.table[index] = new Entry<>(key, value, hash);
        size++;
    }

    private int indexFor(int hash, int size) {
        return hash & (size - 1);
    }

    private int hash(long key) {
        int h = Long.hashCode(key);
        return (key == 0) ? 0 : h ^ (h >>> 16);
    }

    private class Entry<V> implements Iterator<Entry<V>> {
        private long key;
        private V value;
        private int hash;
        private Entry<V> next;

        public Entry(long key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }

        public long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public int getHash() {
            return hash;
        }

        public void setNext(Entry<V> next) {
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Entry<V> next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            return next;
        }
    }
}
