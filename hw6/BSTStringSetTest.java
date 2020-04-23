import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test of a BST-based String Set.
 * @author Farhad Alemi
 */
public class BSTStringSetTest {

    @Test
    public void testPut() {
        BSTStringSet obj = new BSTStringSet();
        obj.put("c");
        obj.put("a");
        obj.put("b");
        obj.put("c");
        obj.put("d");
        obj.put("e");

        assertEquals(obj.getRoot().getString(), "c");
        assertEquals(obj.getRoot().getLeft().getString(), "a");
        assertEquals(obj.getRoot().getLeft().getRight().getString(), "b");
        assertEquals(obj.getRoot().getRight().getString(), "d");
        assertEquals(obj.getRoot().getRight().getRight().getString(),
                "e");
    }

    @Test
    public void testContains() {
        BSTStringSet obj = new BSTStringSet();
        obj.put("c");
        obj.put("b");
        obj.put("c");
        obj.put("e");

        assertFalse(obj.contains("a"));
        assertTrue(obj.contains("b"));
        assertTrue(obj.contains("c"));
        assertFalse(obj.contains("d"));
        assertTrue(obj.contains("e"));
    }

    @Test
    public void testAsList() {
        BSTStringSet obj = new BSTStringSet();
        obj.put("Qc");
        obj.put("adf");
        obj.put("dfsb");
        obj.put("dsfc");
        obj.put("dee");
        obj.put("ewre");
        obj.put("Ads");

        List<String> lst = new LinkedList<>();
        lst.add("Ads");
        lst.add("Qc");
        lst.add("adf");
        lst.add("dee");
        lst.add("dfsb");
        lst.add("dsfc");
        lst.add("ewre");
        assertEquals(lst, obj.asList());
    }

    @Test
    public void testIterator() {
        BSTStringSet obj = new BSTStringSet();
        obj.put("c");
        obj.put("a");
        obj.put("b");
        obj.put("d");
        obj.put("e");

        Iterator<String> iter1 = obj.iterator("A", "B");
        assertFalse(iter1.hasNext());

        Iterator<String> iter2 = obj.iterator("a", "e");
        assertEquals(iter2.next(), "a");
        assertEquals(iter2.next(), "b");
        assertEquals(iter2.next(), "c");
        assertEquals(iter2.next(), "d");
        assertFalse(iter2.hasNext());

        Iterator<String> iter3 = obj.iterator("b", "c");
        assertEquals(iter3.next(), "b");
        assertFalse(iter3.hasNext());
    }
}
