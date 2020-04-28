package gitlet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class CommitTest {

    @Test
    public void initTest() {
        Commit obj1 = new Commit("Test Commit");

        assertEquals(obj1.getMessage(), "Test Commit");
        assertNull(obj1.getParents());
        assertTrue(obj1.getBlobs().isEmpty());

        Commit obj2 = new Commit("MergeCommitTest", new String[]{"a", "1"});
        assertEquals(obj2.getMessage(), "MergeCommitTest");
        assertArrayEquals(obj2.getParents(), new String[]{"a", "1"});
        assertTrue(obj2.getBlobs().isEmpty());
    }

    @Test
    public void blobsTest() {
        Commit obj = new Commit("MergeCommitTest", new String[]{"a", "1"});
        assertTrue(obj.getBlobs().isEmpty());

        HashMap<String, String> blobs = new HashMap<>();
        blobs.put("key1", "file1");
        blobs.put("key2", "file2");
        obj.setBlobs(blobs);

        assertEquals(obj.getBlobs().get("key1"), "file1");
        assertEquals(obj.getBlobs().get("key2"), "file2");
        assertNull(obj.getBlobs().get("key3"));
    }

    @Test
    public void findSplitPointTest() {
        List<String> currParents = new ArrayList<>(),
                givenParents = new ArrayList<>();

        currParents.add("b4");
        currParents.add("a1");
        currParents.add("b5");
        currParents.add("a1");
        currParents.add("b1");
        currParents.add("a2");
        currParents.add("b2");
        currParents.add("a3");
        currParents.add("b3");
        currParents.add("a4");

        assertEquals(Commit.findSplitPoint(currParents, givenParents), "");

        givenParents.add("a5");
        assertEquals(Commit.findSplitPoint(currParents, givenParents), "");

        givenParents.add("b1");
        assertEquals(Commit.findSplitPoint(currParents, givenParents), "b1");

        givenParents.add("b5");
        assertEquals(Commit.findSplitPoint(currParents, givenParents), "b5");
    }
}
