package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongMapImplTest {
    private LongMap longMap;

    @Before
    public void setUp() {
        longMap = new LongMapImpl();
    }

    @Test
    public void whenPutValues_shouldCorrectGetValues() {
        longMap.put(1483L, "Spider");
        assertEquals("Spider", longMap.get(1483L));
    }

    @Test
    public void whenCallKeys_shouldGetArrays() {
        longMap.put(1827398712L, "NZVI");
        longMap.put(189273L, "comparus");
        longMap.put(3788L, "deviro");
        long[] longArray = {1827398712L, 189273L, 3788L};
        assertEquals(longMap.keys().length, longArray.length);
        for (long key : longArray) {
            assertTrue(longMap.containsKey(key));
        }
    }

    @Test
    public void whenCallValues_shouldGetArrays() {
        longMap.put(1827398712L, "NZVI");
        longMap.put(189273L, "comparus");
        longMap.put(3788L, "deviro");
        String[] valueArray = {"NZVI", "comparus", "deviro"};
        assertEquals(longMap.values().length, valueArray.length);
        for (String value : valueArray) {
            assertTrue(longMap.containsValue(value));
        }
    }

    @Test
    public void whenInputMoreValues_shouldReturnCorrectSize() {
        for (long i = 1; i < 100234000; i++) {
            longMap.put(i, "lol" + i);
        }
        assertEquals(100233999, longMap.size());
    }

    @Test
    public void whenCallRemove_shouldDeleteEntryByKey() {
        longMap.put(1827398712L, "NZVI");
        longMap.remove(1827398712L);
        assertTrue(!longMap.containsKey(1827398712L));
    }

    @Test
    public void whenCallClear_shouldClearAllEntry() {
        longMap.put(1827398712L, Integer.MAX_VALUE);
        longMap.put(189273L, "comparus");
        longMap.put(3788L, "deviro");
        longMap.clear();
        assertTrue(!longMap.containsKey(189273L));
        assertEquals(0, longMap.size());
    }

    @Test
    public void whenCreateIntegerMaxValueCapacity_shouldCorrectCreate() {
        longMap = new LongMapImpl(Integer.MAX_VALUE);
        assertNotNull(longMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCallGetWithIncorrectKey_shouldGetException() {
        longMap.get(421L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCallRemoveWithIncorrectKey_shouldGetException() {
        longMap.get(421L);
    }

}
