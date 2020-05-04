package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.assertNotNull;

/**
 * Represents the commits/snapshots similar to the real Git.
 *
 * @author Farhad Alemi
 */
public class Commit implements Serializable {

    /**
     * The two-parameter constructor which does the initialization.
     *
     * @param message The commit message.
     * @param parentCommits The parent commit(s).
     */
    Commit(String message, String[] parentCommits) {
        _message = message;
        _parentCommit = parentCommits;
        _timestamp = (_parentCommit != null) ? new Date(System
                .currentTimeMillis()) : new Date(0);
        _blobs = new HashMap<>();
    }

    /**
     * The constructor which has null has parentCommits.
     *
     * @param message The commit message.
     */
    Commit(String message) {
        this(message, null);
    }

    /**
     * Getter method that returns the commit message.
     *
     * @return The commit message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Getter method that returns the commit timestamp.
     *
     * @return The commit timestamp.
     */
    public Date getTimestamp() {
        return _timestamp;
    }

    /**
     * Getter method that returns the parent commits' Hash.
     *
     * @return The parent commits' Hash.
     */
    public String[] getParents() {
        return _parentCommit;
    }

    /**
     * Getter methods which returns the blobs tracked in the commit.
     *
     * @return blobs in the commit.
     */
    HashMap<String, String> getBlobs() {
        return _blobs;
    }

    /**
     * Setter methods which updates the blobs tracked in the commit.
     *
     * @param blobs blobs tracked by the commit.
     */
    void setBlobs(HashMap<String, String> blobs) {
        _blobs = blobs;
    }

    /**
     * Returns the commit which has the given hash.
     *
     * @param commitHash The commit hash.
     * @return The commit having commitHash as its hash.
     */
    public static Commit retrieveCommit(String commitHash) {
        commitHash = hashAutoComplete(commitHash);

        File commitFile = new File(Gitlet.commitsFolder() + Gitlet.SEPARATOR
                + commitHash);
        if (commitFile.exists()) {
            return Utils.readObject(commitFile, Commit.class);
        } else {
            Main.softFail("No commit with that id exists.");
            return new Commit("");
        }
    }


    /**
     * Prints the commit to the standard output.
     *
     * @param commit The commit which is to be printed.
     * @param commitHash The commit hash.
     */
    public static void printCommit(Commit commit, String commitHash) {
        if (commit == null) {
            return;
        }

        System.out.println("===");
        System.out.printf("commit %s\n", commitHash);

        if (commit.getParents() != null && commit.getParents().length > 1) {
            System.out.printf("Merge: %s %s\n", commit.getParents()[0]
                    .substring(0, 7), commit.getParents()[1].substring(0, 7));
        }
        System.out.println("Date: " + new SimpleDateFormat("EEE MMM d HH:mm:ss"
                + " yyyy Z").format(commit.getTimestamp()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /**
     * Returns a list of all the ancestors starting from the commit with the
     * given hash.
     *
     * @param commitHash The commit hash starting from which the ancestors are
     * found.
     * @return The list of ancestors.
     */
    public static List<String> getAncestors(String commitHash) {
        String[] currParent;
        List<String> ancestors;

        ancestors = new ArrayList<>();
        currParent = Objects.requireNonNull(retrieveCommit(commitHash))
                .getParents();

        while (currParent != null) {
            ancestors.add(currParent[0]);
            currParent = Objects.requireNonNull(retrieveCommit(currParent[0]))
                    .getParents();
        }
        return ancestors;
    }

    /**
     * Returns the first common (nearest in currParents) parent from the lists
     * of ancestors.
     *
     * @param currParents Ancestors in a commit.
     * @param givenParents Ancestors in a commit.
     */
    public static String findSplitPoint(List<String> currParents, List<String>
            givenParents) {
        String splitPoint = "";

        assertNotNull(currParents);
        assertNotNull(givenParents);

        for (String parent : currParents) {
            if (givenParents.contains(parent)) {
                splitPoint = parent;
                break;
            }
        }
        return splitPoint;
    }

    /**
     * Auto completes the commit hash, if possible.
     * @param commitHash Commit hash.
     * @return The, possibly, completed commit hash.
     */
    public static String hashAutoComplete(String commitHash) {
        if (commitHash.length() != Utils.UID_LENGTH) {
            boolean foundOnce = false;
            String tempHash = "";
            List<String> commitNames = Utils.plainFilenamesIn(Gitlet
                    .commitsFolder());

            for (String commitFile : Objects.requireNonNull(commitNames)) {
                if (commitFile.startsWith(commitHash)) {
                    if (foundOnce) {
                        Main.softFail("Commit ID is not unique.");
                    } else {
                        foundOnce = true;
                        tempHash = commitFile;
                    }
                }
            }
            commitHash = (foundOnce) ? tempHash : commitHash;
        }
        return commitHash;
    }

    /** Commit message. */
    private final String _message;

    /** Commit date. */
    private final Date _timestamp;

    /** Hash(es) for commit's parent(s). */
    private final String[] _parentCommit;

    /** A mapping of file names to respective blobs. Filename's SHA1 is the
     * key, and staged file's SHA1 is the respective value. */
    private HashMap<String, String> _blobs;
}
