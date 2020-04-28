package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Utility class specifically built for Gitlet.
 *
 * @author Farhad Alemi.
 */
public class GitletUtils {

    /** File separator. */
    static final String SEPARATOR = Gitlet.SEPARATOR;

    /**
     * Returns the File obj for the branch named branchName, if i exists.
     *
     * @param branchName The branch name.
     * @return The branch File obj.
     */
    static File getBranch(String branchName) {
        File givenBranch = null;
        try {
            givenBranch = new File(Gitlet.headsFolder().getCanonicalPath()
                    + SEPARATOR + branchName);
            if (givenBranch.exists()) {
                return givenBranch;
            } else {
                Main.softFail("A branch with that name does not exist.");
            }
        } catch (IOException e) {
            Main.softFail("File IO Failed.");
        }
        return givenBranch;
    }

    /**
     * Returns  true if branchName is the current branch.
     *
     * @param branchName A branch name.
     * @return True if branchName is the name of current branch.
     */
    static boolean isCurrBranch(String branchName) {
        return getBranch(branchName).getAbsolutePath()
                .equals(getBranch(getCurrBranchName()).getAbsolutePath());
    }

    /**
     * Returns the name for the current branch.
     *
     * @return Current branch name.
     */
    static String getCurrBranchName() {
        return new File(getHeadPtr()).getName();
    }

    /**
     * Copies commit, along with associated blobs to toGitlet directory.
     *
     * @param fromGitlet Source DIR.
     * @param toGitlet Destination DIR.
     * @param commitHash Hash of the commit to be copied.
     */
    static void copyCommit(File fromGitlet, File toGitlet, String commitHash) {
        File fromCommit, toCommit;
        Commit commit;

        try {
            fromCommit = new File(fromGitlet.getCanonicalPath() + SEPARATOR
                    + ".gitlet/commits" + SEPARATOR + commitHash);
            toCommit = new File(toGitlet.getCanonicalPath() + SEPARATOR
                    + "commits" + SEPARATOR + commitHash);
            commit = Commit.retrieveCommit(commitHash);
            assertTrue(commit != null && fromCommit.exists());

            Files.copy(fromCommit.toPath(), toCommit.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            if (commit.getBlobs() != null) {
                for (String blobName : commit.getBlobs().keySet()) {
                    File fromBlob = new File(fromGitlet.getCanonicalPath()
                            + SEPARATOR + ".gitlet/blobs" + SEPARATOR + commit
                            .getBlobs().get(blobName) + "-" + blobName
                            .substring(Utils.UID_LENGTH + 1));
                    File toBlob = new File(toGitlet.getCanonicalPath()
                            + SEPARATOR + "blobs" + SEPARATOR + commit
                            .getBlobs().get(blobName) + "-" + blobName
                            .substring(Utils.UID_LENGTH + 1));

                    assertTrue(fromBlob.exists());
                    Files.copy(fromBlob.toPath(), toBlob.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            Main.softFail("File copy failed.");
        }
    }

    /**
     * Returns the current head pointer.
     *
     * @return Current head-pointer full-path.*/
    static String getHeadPtr() {
        String temp = Utils.readContentsAsString(Gitlet.headPtr());
        temp = temp.substring(temp.lastIndexOf(SEPARATOR) + 1);
        File remoteBranch = new File(Gitlet.headsFolder() + SEPARATOR + temp);
        try {
            return remoteBranch.getCanonicalPath();
        } catch (IOException e) {
            Main.softFail("File IO Failed.");
        }
        return "";
    }

    /**
     * Returns the last commit hash as pointed to by HEAD_PTR.
     *
     * @return Last commit hash. */
    static String lastCommitHash() {
        return Utils.readContentsAsString(new File(getHeadPtr()));
    }

    /**
     *  Returns the pointed to by the head pointer.
     *
     *  @return Commit pointed to by the head pointer. */
    static Commit lastCommit() {
        return Commit.retrieveCommit(lastCommitHash());
    }

    /**
     * Deletes all the files inside INDEX_FOLDER.
     */
    static void clearStagingArea() {
        List<String> stagedFiles, removalFiles;

        stagedFiles = Utils.plainFilenamesIn(Gitlet.ADDITION_FOLDER);
        removalFiles = Utils.plainFilenamesIn(Gitlet.REMOVAL_FOLDER);

        assertTrue(stagedFiles != null && removalFiles != null);

        for (String fileName : stagedFiles) {
            clearStagingArea(fileName);
        }
        for (String fileName : removalFiles) {
            clearStagingArea(fileName);
        }
    }

    /**
     * Deletes specific file from staging area.
     *
     * @param fileNameSHA1 File name to be removed.
     */
    static void clearStagingArea(String fileNameSHA1) {
        File stagedBlob, removalBlob;
        stagedBlob = new File(Gitlet.ADDITION_FOLDER + SEPARATOR
                + fileNameSHA1);
        removalBlob = new File(Gitlet.REMOVAL_FOLDER + SEPARATOR
                + fileNameSHA1);

        if (stagedBlob.exists()) {
            stagedBlob.delete();
        }
        if (removalBlob.exists()) {
            removalBlob.delete();
        }
    }

    /**
     * Stores the commit inside COMMITS_FOLDER.
     *
     * @param newCommit Commit to be stored.
     */
    static void addCommit(Commit newCommit) {
        File branch;
        String commitSHA1;

        commitSHA1 = Utils.sha1((Object) Utils.serialize(newCommit));
        Utils.writeObject(new File(Gitlet.commitsFolder() + SEPARATOR
                        + commitSHA1), newCommit);

        branch = new File(getHeadPtr());
        Utils.writeContents(branch, commitSHA1);
    }

    /**
     * Prints branch names; indicates current branch with '*'.
     */
    static void printBranches() {
        List<String> branchNames;
        File currBranch, branch;

        currBranch = new File(Utils.readContentsAsString(Gitlet.headPtr()));
        branchNames = Utils.plainFilenamesIn(Gitlet.headsFolder());
        assertNotNull(branchNames);

        System.out.println("=== Branches ===");
        try {
            for (String branchName : branchNames) {
                branch = new File(Gitlet.headsFolder() + SEPARATOR
                        + branchName);
                if (branch.getCanonicalPath().equals(currBranch
                        .getCanonicalPath())) {
                    System.out.print("*");
                }
                if (branchName.startsWith("remote_")) {
                    int tempIndex = branchName.lastIndexOf("_");
                    System.out.println(branchName.substring(7, tempIndex) + "/"
                            + branchName.substring(tempIndex + 1));
                } else {
                    System.out.println(branchName);
                }
            }
        } catch (IOException e) {
            System.out.println("Branch path retrieval failed.");
        }
        System.out.println();
    }

    /**
     * Prints items from the given list.
     *
     * @param msg Header message.
     * @param filesList List of items to print.
     * @param preLength Filename truncation length.
     */
    static void printFilenamesIn(String msg, List<String> filesList,
                                  int preLength) {
        PriorityQueue<String> truncNames;
        assertTrue(filesList != null && msg != null);

        truncNames = new PriorityQueue<>();
        for (String fileName : filesList) {
            truncNames.add(fileName.substring(preLength));
        }

        System.out.println(msg);
        while (!truncNames.isEmpty()) {
            System.out.println(truncNames.poll());
        }
        System.out.println();
    }

    /**
     * Returns the SHA1 for files in CWD.
     *
     * @return SHA1 for all files in CWD: (Filename: FileSHA1).
     */
    static HashMap<String, String> cwdFilesSHA1() {
        File cwdFile;
        HashMap<String, String> cwdFilesSHA1;
        List<String> cwdFiles;

        cwdFiles = Utils.plainFilenamesIn(Gitlet.CWD);
        if (cwdFiles == null) {
            return null;
        }

        cwdFilesSHA1 = new HashMap<>();
        for (String fileName : cwdFiles) {
            cwdFile = new File(Gitlet.CWD + SEPARATOR + fileName);
            cwdFilesSHA1.put(fileName, Utils.sha1((Object) Utils.readContents
                    (cwdFile)));
        }
        return cwdFilesSHA1;
    }

    /**
     * Used for handling merge conflict.
     *
     * @param currBlob Current file blob.
     * @param givenBlob Given File blob.
     * @param fileName Original filename.
     */
    static void handleConflict(String currBlob, String givenBlob, String
            fileName) {
        String currBlobContent = (currBlob.equals("")) ? "" : Utils
                .readContentsAsString(new File(Gitlet.blobsFolder()
                        + SEPARATOR + currBlob + "-" + fileName));
        String givenBlobContent = (givenBlob.equals("")) ? "" : Utils
                .readContentsAsString(new File(Gitlet.blobsFolder()
                        + SEPARATOR + givenBlob + "-" + fileName));

        String newContents = "<<<<<<< HEAD\n" + currBlobContent + "=======\n"
                + givenBlobContent + ">>>>>>>\n";

        Utils.writeContents(new File(Gitlet.CWD + SEPARATOR + fileName),
                newContents);
    }
}
