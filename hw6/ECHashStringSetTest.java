import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author Farhad Alemi
 */
public class ECHashStringSetTest  {

    @Test
    public void testPutContains() {
        ECHashStringSet obj = new ECHashStringSet();
        obj.put("c");
        obj.put("a");
        obj.put("B");
        obj.put("c");
        obj.put("d");
        obj.put("e");

        assertFalse(obj.contains("b"));
        assertFalse(obj.contains("aa"));
        assertTrue(obj.contains("c"));
        assertTrue(obj.contains("a"));
        assertTrue(obj.contains("B"));
        assertTrue(obj.contains("d"));
        assertTrue(obj.contains("e"));
    }

    @Test
    public void testAsList() {
        ECHashStringSet obj = new ECHashStringSet();
        List<String> objLst;
        obj.put("Qc");
        obj.put("adf");
        obj.put("dfsb");
        obj.put("dsfc");
        obj.put("dee");
        obj.put("ewre");
        obj.put("Ads");

        objLst = obj.asList();

        List<String> lst = new LinkedList<>();
        lst.add("Ads");
        lst.add("Qc");
        lst.add("adf");
        lst.add("dee");
        lst.add("dfsb");
        lst.add("dsfc");
        lst.add("ewre");

        for (String str : lst) {
            assertTrue(objLst.contains(str));
        }
    }
}
