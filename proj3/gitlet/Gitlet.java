package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * The Git simulator which operates based on commands from the terminal.
 *
 * @author Farhad Alemi
 */
public class Gitlet {

    /** Universal file separator for both Unix- and Windows-based machines. */
    static final String SEPARATOR = System.getProperty("file.separator");

    /** Current Working Directory. */
    static final File CWD = new File(".");

    /** Main metadata folder. */
    private static File _gitletFolder = new File(CWD + SEPARATOR + ".gitlet");

    /** The blobs folder used for storing file snapshots. */
    private static File _blobsFolder = new File(_gitletFolder + SEPARATOR
            + "blobs");

    /** The commits folder used for storing commits. */
    private static File _commitsFolder = new File(_gitletFolder + SEPARATOR
            + "commits");

    /** The index folder used to as staging area. */
    static final File INDEX_FOLDER = new File(_gitletFolder + SEPARATOR
            + "index");

    /** The addition folder contains files staged for addition. */
    static final File ADDITION_FOLDER = new File(INDEX_FOLDER + SEPARATOR
            + "addition");

    /** The removal folder contains files staged for removal. */
    static final File REMOVAL_FOLDER = new File(INDEX_FOLDER + SEPARATOR
            + "removal");

    /** The refs folder used to store various pointers. */
    private static File _refsFolder = new File(_gitletFolder + SEPARATOR
            + "refs");

    /** The folder which contains file(s) named after each branch.
     * Inside each file is the latest commit's hash. */
    private static File _headsFolder = new File(_refsFolder + SEPARATOR
            + "heads");

    /** The file which contains the full path for the active branch. */
    private static File _headPtr = new File(_gitletFolder + SEPARATOR
            + "HEAD");

    /** The initial branch name. */
    static final String INIT_BRANCH = "master";

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * It initially starts with an initialized commit having "initial
     * commit" as the commit message. The branch for this initial commit is
     * named "master". The timestamp for this initial commit is the Unix Epoch.
     *
     * @param args init.
     */
    public void doInit(String[] args) {
        Commit initCommit;

        validateNumArgs(args, 1);
        if (gitletFolder().exists()) {
            Main.softFail("A Gitlet version-control system already exists"
                    + " in the current directory.");
        }

        headsFolder().mkdirs();
        blobsFolder().mkdir();
        commitsFolder().mkdir();
        ADDITION_FOLDER.mkdirs();
        REMOVAL_FOLDER.mkdir();

        try {
            Utils.writeContents(headPtr(), headsFolder().getCanonicalPath()
                    + SEPARATOR + INIT_BRANCH);
        } catch (IOException e) {
            Main.softFail("File IO Failed.");
        }
        initCommit = new Commit("initial commit");
        GitletUtils.addCommit(initCommit);
    }

    /**
     * Sets up file for staging. Staging an already staged file overwrites the
     * previous entry and writes the new contents. The staging area has to be
     * inside .gitlet directory.
     *
     * @param args add [filename].
     */
    public void doAdd(String[] args) {
        File addendFile, stagedFile;
        String addendNameSHA1, stagedFileSHA1;
        Collection<String> trackedFiles;

        validateInit();
        validateNumArgs(args, 2);

        addendFile = new File(CWD + SEPARATOR + args[1]);
        if (!addendFile.exists()) {
            Main.softFail("File does not exist.");
        }

        addendNameSHA1 = Utils.sha1(args[1]) + "-" + args[1];
        stagedFile = new File(ADDITION_FOLDER + SEPARATOR + addendNameSHA1);
        trackedFiles = GitletUtils.lastCommit().getBlobs().values();

        GitletUtils.clearStagingArea(addendNameSHA1);
        Utils.writeContents(stagedFile, (Object) Utils
                .readContents(addendFile));
        stagedFileSHA1 = Utils.sha1(Utils.sha1((Object) Utils.readContents
                (stagedFile)) + addendNameSHA1);
        if (trackedFiles.contains(stagedFileSHA1)) {
            stagedFile.delete();
        }
    }

    /**
     * Creates a snapshot of files. The Commit obj mimics the characteristics
     * of a the git commit command. Commit only takes the snapshot of files
     * that have already been staged. By default a commit is the same as its
     * parent. Updates to the commits include files staged. Performing a commit
     * clears the staging area.
     *
     * @param args commit [message].
     * @param parents Commit parents; null if not a merge commit.
     */
    public void doCommit(String[] args, String[] parents) {
        byte[] stagedFileContents;
        File stagedBlob, finalBlob;
        Commit parentCommit, newCommit;
        HashMap<String, String> blobs;
        List<String> stagedFiles, removalFiles;
        String stagedBlobSHA1;

        validateInit();
        validateNumArgs(args, 2);

        if (args[1].length() == 0) {
            Main.softFail("Please enter a commit message.");
        }

        parentCommit = GitletUtils.lastCommit();
        blobs = parentCommit.getBlobs();

        removalFiles = Utils.plainFilenamesIn(REMOVAL_FOLDER);
        stagedFiles = Utils.plainFilenamesIn(ADDITION_FOLDER);

        assertTrue(removalFiles != null && stagedFiles != null);

        if (removalFiles.size() == 0 && stagedFiles.size() == 0) {
            Main.softFail("No changes added to the commit.");
        }

        for (String rmFile : removalFiles) {
            blobs.remove(rmFile);
            GitletUtils.clearStagingArea(rmFile);
        }

        for (String file : stagedFiles) {
            stagedBlob = new File(ADDITION_FOLDER + SEPARATOR + file);

            stagedFileContents = Utils.readContents(stagedBlob);

            stagedBlobSHA1 = Utils.sha1(Utils.sha1((Object) stagedFileContents)
                    + file);
            blobs.put(file, stagedBlobSHA1);
            finalBlob = new File(blobsFolder() + SEPARATOR + stagedBlobSHA1
                    + file.substring(Utils.UID_LENGTH));

            Utils.writeContents(finalBlob, (Object) stagedFileContents);
            GitletUtils.clearStagingArea(file);
        }

        parents = (parents != null) ? parents : new String[]{GitletUtils
                .lastCommitHash()};
        newCommit = new Commit(args[1], parents);
        newCommit.setBlobs(blobs);
        GitletUtils.addCommit(newCommit);
    }

    /**
     * Un-stages the file if it is currently staged. It also stages the
     * files for removal if the file is tracked in the current commit. If the
     * file still exists in the working directory, doRm removes it.
     *
     * @param args rm [file to be removed/un-tracked upon next commit.]
     */
    public void doRm(String[] args) {
        File removalFile, stagingBlob, removalBlob;
        String fileNameSHA1;
        HashMap<String, String> trackedBlobs;

        validateInit();
        validateNumArgs(args, 2);

        removalFile = new File(CWD + SEPARATOR + args[1]);

        fileNameSHA1 = Utils.sha1(args[1]) + "-" + args[1];
        stagingBlob = new File(ADDITION_FOLDER + SEPARATOR + fileNameSHA1);
        removalBlob = new File(REMOVAL_FOLDER + SEPARATOR + fileNameSHA1);
        trackedBlobs = GitletUtils.lastCommit().getBlobs();

        if (!trackedBlobs.containsKey(fileNameSHA1) && !stagingBlob.exists()) {
            Main.softFail("No reason to remove the file.");
        } else {
            if (stagingBlob.exists()) {
                GitletUtils.clearStagingArea(fileNameSHA1);
            }
            if (trackedBlobs.containsKey(fileNameSHA1)) {
                Utils.writeContents(removalBlob, "");
                if (removalFile.exists()) {
                    Utils.restrictedDelete(removalFile);
                }
            }
        }
    }

    /**
     * Provides a log of the commits starting at current head. It goes
     * back until the initial commit ignoring any second parent (due to merge).
     *
     * @param args log.
     */
    public void doLog(String[] args) {
        Commit currCommit;
        String commitHash;
        String[] parentHash;

        validateInit();
        validateNumArgs(args, 1);

        commitHash = GitletUtils.lastCommitHash();

        do {
            currCommit = Commit.retrieveCommit(commitHash);
            Commit.printCommit(currCommit, commitHash);
            parentHash = (currCommit == null) ? null : currCommit.getParents();
            commitHash = (parentHash == null) ? null : parentHash[0];
        } while (commitHash != null);
    }

    /**
     * Provides a log of all commits ever made. The order of the
     * commits is not specified.
     *
     * @param  args global-log
     */
    void doGlobalLog(String[] args) {
        List<String> commitHashList;
        validateInit();
        validateNumArgs(args, 1);

        commitHashList = Utils.plainFilenamesIn(commitsFolder());
        assertNotNull(commitHashList);

        for (String commitHash : commitHashList) {
            Commit.printCommit(Commit.retrieveCommit(commitHash), commitHash);
        }
    }

    /**
     * Prints out the ids of all commits which have the given commit message
     * on separate lines. Put the commit message in quotations if it is a
     * multi-word message.
     *
     * @param args find [commit message].
     */
    public void doFind(String[] args) {
        List<String> commitHashList;
        boolean found;

        validateInit();
        validateNumArgs(args, 2);

        found = false;
        commitHashList = Utils.plainFilenamesIn(commitsFolder());
        assertNotNull(commitHashList);

        for (String commitHash : commitHashList) {
            if (Objects.requireNonNull(Commit.retrieveCommit(commitHash))
                    .getMessage().equals(args[1])) {
                System.out.println(commitHash);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Displays existing branches and denoting current branch.
     *
     * @param args status.
     */
    public void doStatus(String[] args) {
        File cwdFile, stagedFile;
        List<String> stagedFiles, removedFiles, cwdFiles, modNotStagedFiles,
                unTrackedFiles;
        String cwdTotalSHA1, cwdFileName, cwdFileNameSHA1;
        HashMap<String, String> cwdFilesSHA1, commitBlobs;
        validateInit(); validateNumArgs(args, 1);
        stagedFiles = Utils.plainFilenamesIn(ADDITION_FOLDER);
        removedFiles = Utils.plainFilenamesIn(REMOVAL_FOLDER);
        cwdFiles = Utils.plainFilenamesIn(CWD);
        cwdFilesSHA1 = GitletUtils.cwdFilesSHA1();
        commitBlobs = GitletUtils.lastCommit().getBlobs();
        assertTrue(stagedFiles != null && removedFiles != null && cwdFiles
                != null && cwdFilesSHA1 != null);
        modNotStagedFiles = new ArrayList<>();
        unTrackedFiles = new ArrayList<>();
        for (String fileName : stagedFiles) {
            cwdFileName = fileName.substring(Utils.UID_LENGTH + 1);
            cwdFile = new File(CWD + SEPARATOR + cwdFileName);
            stagedFile = new File(ADDITION_FOLDER + SEPARATOR + fileName);
            if (!cwdFile.exists()) {
                modNotStagedFiles.add(cwdFileName + " (deleted)");
            } else if (!cwdFilesSHA1.get(cwdFileName).equals(Utils.sha1
                    ((Object) Utils.readContents(stagedFile)))) {
                modNotStagedFiles.add(cwdFileName + " (modified)");
            }
        }
        for (String fileName : commitBlobs.keySet()) {
            cwdFileName = fileName.substring(Utils.UID_LENGTH + 1);
            cwdFile = new File(CWD + SEPARATOR + cwdFileName);
            if (!cwdFile.exists()) {
                if (!removedFiles.contains(fileName)) {
                    modNotStagedFiles.add(cwdFileName + " (deleted)");
                }
            } else {
                cwdTotalSHA1 = Utils.sha1(cwdFilesSHA1.get(cwdFileName)
                        + Utils.sha1(cwdFileName) + "-" + cwdFileName);
                if (!cwdTotalSHA1.equals(commitBlobs.get(fileName))) {
                    modNotStagedFiles.add(cwdFileName + " (modified)");
                }
            }
        }
        for (String fileName : cwdFiles) {
            cwdFileNameSHA1 = Utils.sha1(fileName) + "-" + fileName;
            if (!stagedFiles.contains(cwdFileNameSHA1) && !commitBlobs
                    .containsKey(cwdFileNameSHA1)) {
                unTrackedFiles.add(fileName);
            }
        }

        GitletUtils.printBranches();
        GitletUtils.printFilenamesIn("=== Staged Files ===", stagedFiles,
                Utils.UID_LENGTH + 1);
        GitletUtils.printFilenamesIn("=== Removed Files ===", removedFiles,
                Utils.UID_LENGTH + 1);
        GitletUtils.printFilenamesIn("=== Modifications Not Staged For Commit"
                + " ===", modNotStagedFiles, 0);
        GitletUtils.printFilenamesIn("=== Untracked Files ===",
                unTrackedFiles, 0);
    }

    /**
     * Performs the git checkout operation according to supplied args.
     *
     * @param args Has the following forms:
     * 1. checkout -- [file name].
     * 2. checkout [commit id] -- [file name].
     * 3. checkout [branch name].
     */
    public void doCheckout(String[] args) {
        validateInit();

        switch (args.length) {
        case 2:
            checkoutBranch(args);
            break;
        case 3:
            if (!args[1].equals("--")) {
                Main.softFail("Incorrect operands.");
            }
            checkoutFileFromCommit(new String[]{args[0], GitletUtils
                    .lastCommitHash(), args[1], args[2]});
            break;
        case 4:
            if (!args[2].equals("--")) {
                Main.softFail("Incorrect operands.");
            }
            checkoutFileFromCommit(args);
            break;
        default:
            Main.softFail("Incorrect operands.");
            break;
        }
    }

    /**
     * Creates a new branch with the given name and sets up the head node
     * appropriately.
     *
     * @param args branch [branch name].
     */
    public void doBranch(String[] args) {
        File branch;

        validateInit();
        validateNumArgs(args, 2);

        branch = new File(headsFolder() + SEPARATOR + args[1]);
        if (branch.exists()) {
            Main.softFail("A branch with that name already exists.");
        }
        Utils.writeContents(branch, GitletUtils.lastCommitHash());
    }

    /**
     * Removes the branch with the given name.
     *
     * @param args rm-branch [branch name].
     */
    public void doRmBranch(String[] args) {
        File branch;
        String branchPath = "";

        validateInit();
        validateNumArgs(args, 2);

        branch = new File(headsFolder() + SEPARATOR + args[1]);
        try {
            branchPath = branch.getCanonicalPath();
        } catch (IOException e) {
            Main.softFail("File IO Failed.");
        }
        if (!branch.exists()) {
            Main.softFail("A branch with that name does not exist.");
        } else if (GitletUtils.getHeadPtr().equals(branchPath)) {
            Main.softFail("Cannot remove the current branch.");
        } else {
            branch.delete();
        }
    }

    /**
     * Performs the git reset command.
     *
     * @param args reset [commit id].
     */
    public void doReset(String[] args) {
        validateInit();
        validateNumArgs(args, 2);

        args[1] = Commit.hashAutoComplete(args[1]);

        checkoutCommit(Commit.retrieveCommit(args[1]), args[1]);
        Utils.writeContents(new File(GitletUtils.getHeadPtr()), args[1]);
    }

    /**
     * Checks if merge preconditions hold.
     *
     * @param args merge [branch name]
     */
    private void mergePreConditions(String[] args) {
        validateInit();
        validateNumArgs(args, 2);

        if (!isEmptyStagingArea()) {
            Main.softFail("You have uncommitted changes.");
        } else if (GitletUtils.isCurrBranch(args[1])) {
            Main.softFail("Cannot merge a branch with itself.");
        }
    }

    /**
     * Merges the files to to the current branch from another branch.
     *
     * @param args merge [branch name].
     */
    public void doMerge(String[] args) {
        boolean hasConflict;
        Commit currCommit, givenCommit, splitPoint;
        HashMap<String, String> currBlob, givenBlob, splitPointBlob;
        List<String> currParents, givenParents;
        String currCommitHash, givenCommitHash;

        mergePreConditions(args);

        currCommitHash = Utils.readContentsAsString(GitletUtils
                .getBranch(GitletUtils.getCurrBranchName()));
        givenCommitHash = Utils.readContentsAsString(GitletUtils
                .getBranch(args[1]));
        currParents = Commit.getAncestors(currCommitHash);
        givenParents = Commit.getAncestors(givenCommitHash);

        if (currParents.contains(givenCommitHash)) {
            Main.softFail("Given branch is an ancestor of the current"
                    + " branch.");
        } else {
            if (givenParents.contains(currCommitHash)) {
                doCheckout(new String[]{"checkout", args[1]});
                Main.softFail("Current branch fast-forwarded.");
            }
        }

        currCommit = Commit.retrieveCommit(currCommitHash);
        givenCommit = Commit.retrieveCommit(givenCommitHash);
        splitPoint = Commit.retrieveCommit(Commit.findSplitPoint(currParents,
                givenParents));

        currBlob = Objects.requireNonNull(currCommit).getBlobs();
        givenBlob = Objects.requireNonNull(givenCommit).getBlobs();
        splitPointBlob = Objects.requireNonNull(splitPoint).getBlobs();

        currBlob = (currBlob != null) ? currBlob : new HashMap<>();
        givenBlob = (givenBlob != null) ? givenBlob : new HashMap<>();
        splitPointBlob = (splitPointBlob != null) ? splitPointBlob
                : new HashMap<>();

        hasConflict = mergeHelper(currBlob, givenBlob, splitPointBlob,
                givenCommitHash);

        doCommit(new String[]{"commit", "Merged " + args[1] + " into "
                + GitletUtils.getCurrBranchName() + "."}, new
                String[]{currCommitHash, givenCommitHash});

        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * A helper method which performs file merge.
     *
     * @param currBlob Current commit blob.
     * @param givenBlob Incoming commit blob.
     * @param splitPointBlob Split-point blob.
     * @param givenCommitHash Incoming commit hash.
     * @return True if there was a file conflict during merge.
     */
    private boolean mergeHelper(HashMap<String, String> currBlob,
            HashMap<String, String> givenBlob, HashMap<String, String>
            splitPointBlob, String givenCommitHash) {
        boolean hasConflict = false;
        for (String fileName : givenBlob.keySet()) {
            isOverWritingUnTracked(fileName, !splitPointBlob
                    .containsKey(fileName) && !currBlob.containsKey(fileName));
        }
        for (String fileName : givenBlob.keySet()) {
            String currFile = currBlob.get(fileName);
            String givenFile = givenBlob.get(fileName);
            String splitFile = splitPointBlob.get(fileName);
            String truncName = fileName.substring(Utils.UID_LENGTH + 1);
            if (splitPointBlob.containsKey(fileName)) {
                if (currBlob.containsKey(fileName)) {
                    if (!givenFile.equals(splitFile)) {
                        if (currFile.equals(splitFile)) {
                            checkoutFileFromCommit(new String[]{"checkout",
                                givenCommitHash, "--", truncName});
                        } else if (!currFile.equals(givenFile)) {
                            GitletUtils.handleConflict(currFile, givenFile,
                                    truncName);
                            hasConflict = true;
                        }
                        doAdd(new String[]{"add", truncName});
                    }
                } else if (!givenFile.equals(splitFile)) {
                    GitletUtils.handleConflict("", givenFile, truncName);
                    hasConflict = true;
                    doAdd(new String[]{"add", truncName});
                }
            } else if (!currBlob.containsKey(fileName)) {
                checkoutFileFromCommit(new String[]{"checkout",
                    givenCommitHash, "--", truncName});
                doAdd(new String[]{"add", truncName});
            }
        }
        for (String fileName : currBlob.keySet()) {
            String currFile = currBlob.get(fileName);
            String givenFile = givenBlob.get(fileName);
            String splitFile = splitPointBlob.get(fileName);
            String truncName = fileName.substring(Utils.UID_LENGTH + 1);
            if (splitPointBlob.containsKey(fileName)) {
                if (!givenBlob.containsKey(fileName)) {
                    if (currFile.equals(splitFile)) {
                        doRm(new String[]{"rm", truncName});
                    } else {
                        GitletUtils.handleConflict(currFile, "", truncName);
                        hasConflict = true;
                        doAdd(new String[]{"add", truncName});
                    }
                }
            } else if (givenBlob.containsKey(fileName) && !currFile
                    .equals(givenFile)) {
                GitletUtils.handleConflict(currFile, givenFile, truncName);
                hasConflict = true;
                doAdd(new String[]{"add", truncName});
            }
        }
        return hasConflict;
    }

    /**
     * Saves the given login information under the given remote name.
     *
     * @param args add-remote [remote name] [name of remote DIR]/.gitlet.
     */
    public void doAddRemote(String[] args) {
        validateInit();
        validateNumArgs(args, 3);

        File remoteDIR = new File(refsFolder() + SEPARATOR + args[1]);

        if (remoteDIR.exists()) {
            Main.softFail("A remote with that name already exists.");
        }

        Utils.writeContents(remoteDIR, args[2]);
    }

    /**
     * Remove information associated with the given remote name.
     *
     * @param args rm-remote [remote name].
     */
    public void doRmRemote(String[] args) {
        validateInit();
        validateNumArgs(args, 2);
        validateRemote(args[1]);

        File remoteDIR = new File(refsFolder() + SEPARATOR + args[1]);
        remoteDIR.delete();
    }

    /**
     * Helper method which validates remote preconditions.
     * @param args push|fetch [remote name] [remote branch name].
     */
    void preRemote(String[] args) {
        File remoteGitlet;

        validateInit();
        validateNumArgs(args, 3);
        validateRemote(args[1]);

        remoteGitlet = new File(Utils.readContentsAsString(new
                File(refsFolder() + SEPARATOR + args[1])));

        if (!remoteGitlet.exists()) {
            Main.softFail("Remote directory not found.");
        }
    }

    /**
     * Attempts to append the current branch's commits to the end of the given
     * branch at the given remote.
     *
     * @param args push [remote name] [remote branch name].
     */
    public void doPush(String[] args) {
        String remoteCommitHash, localCommitHash;
        File remoteGitlet, remoteBranch;
        List<String> localNewCommits;

        preRemote(args);
        remoteGitlet = new File(Utils.readContentsAsString(new
                File(refsFolder() + SEPARATOR + args[1])));

        localCommitHash = GitletUtils.lastCommitHash();
        switchGitlet(remoteGitlet.getPath());
        remoteBranch = new File(headsFolder() + SEPARATOR + args[2]);

        if (!remoteBranch.exists()) {
            remoteBranch = new File(GitletUtils.getHeadPtr());
        }
        remoteCommitHash = Utils.readContentsAsString(remoteBranch);

        switchGitlet(CWD + SEPARATOR + ".gitlet");
        localNewCommits = Commit.getAncestors(localCommitHash);

        if (remoteCommitHash.equals(localCommitHash)) {
            Main.softFail("Nothing to push.");
        } else if (!localNewCommits.contains(remoteCommitHash)) {
            Main.softFail("Please pull down remote changes before pushing.");
        } else {
            for (String commit : localNewCommits) {
                if (commit.equals(remoteCommitHash) && !Commit.retrieveCommit
                        (commit).getMessage().equals("initial commit")) {
                    break;
                }
                GitletUtils.copyCommit(CWD, remoteGitlet, commit);
            }
            GitletUtils.copyCommit(CWD, remoteGitlet, localCommitHash);
        }
        switchGitlet(remoteGitlet.getPath());
        Utils.writeContents(new File(headsFolder() + SEPARATOR + args[2]),
                localCommitHash);
        doReset(new String[]{"reset", localCommitHash});
    }

    /**
     * Brings down commits from the remote Gitlet repository into the local
     * Gitlet repository.
     *
     * @param args fetch [remote name] [remote branch name].
     */
    public void doFetch(String[] args) {
        File remoteGitlet, remoteBranch, remoteDIR;
        List<String> remoteNewCommits;
        String localCommitHash, remoteCommitHash;

        preRemote(args);
        remoteGitlet = new File(Utils.readContentsAsString(new
                File(refsFolder() + SEPARATOR + args[1])));

        localCommitHash = GitletUtils.lastCommitHash();
        switchGitlet(remoteGitlet.getPath());
        remoteBranch = new File(headsFolder() + SEPARATOR + args[2]);

        if (!remoteBranch.exists()) {
            Main.softFail("That remote does not have that branch.");
        }

        remoteCommitHash = Utils.readContentsAsString(remoteBranch);
        remoteNewCommits = Commit.getAncestors(remoteCommitHash);

        for (String commit : remoteNewCommits) {
            if (commit.equals(localCommitHash) && !Commit.retrieveCommit
                    (commit).getMessage().equals("initial commit")) {
                break;
            }
            GitletUtils.copyCommit(remoteGitlet.getParentFile(),
                    new File(CWD + SEPARATOR + ".gitlet"), commit);
        }
        GitletUtils.copyCommit(remoteGitlet.getParentFile(),
                new File(CWD + SEPARATOR + ".gitlet"), remoteCommitHash);

        switchGitlet(CWD + SEPARATOR + ".gitlet");
        remoteDIR = new File(headsFolder() + SEPARATOR + args[1]);
        remoteDIR.mkdir();
        Utils.writeContents(new File(remoteDIR + SEPARATOR + args[2]),
                remoteCommitHash);
        Utils.writeContents(new File(headsFolder() + SEPARATOR + "remote_"
                + args[1] + "_" + args[2]), remoteCommitHash);
    }

    /**
     * Fetches branch [remote name]/[remote branch name] as for the fetch
     * command, and then merges that fetch into the current branch.
     *
     * @param args pull [remote name] [remote branch name].
     */
    public void doPull(String[] args) {
        validateInit();
        validateNumArgs(args, 3);
        doFetch(new String[]{"fetch", args[1], args[2]});
        doMerge(new String[]{"merge", "remote_" + args[1] + "_" + args[2]});
    }
    /**
     * Switches the .gitlet directory by modifying the path for GITLET_FOLDER.
     *
     * @param gitletFolder new path for GITLET_FOLDER.
     */
    private void switchGitlet(String gitletFolder) {
        _gitletFolder = new File(gitletFolder);
        _blobsFolder = new File(gitletFolder() + SEPARATOR + "blobs");
        _commitsFolder = new File(gitletFolder() + SEPARATOR + "commits");
        _refsFolder = new File(gitletFolder() + SEPARATOR + "refs");
        _headsFolder = new File(refsFolder() + SEPARATOR + "heads");
        _headPtr = new File(gitletFolder() + SEPARATOR + "HEAD");
    }

    /**
     * Checks if a remote with that name exists.
     *
     * @param remoteName Remote name.
     */
    private void validateRemote(String remoteName) {
        File remoteDIR = new File(refsFolder() + SEPARATOR + remoteName);
        if (!remoteDIR.exists()) {
            Main.softFail("A remote with that name does not exist.");
        }
    }
    /**
     * Checks the number of arguments versus the expected number.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    private void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Main.softFail("Incorrect operands.");
        }
    }

    /**
     * Checks whether .gitlet DIR exists.
     */
    private void validateInit() {
        if (!gitletFolder().exists()) {
            Main.softFail("Not in an initialized Gitlet directory.");
        }
    }


    /**
     * A helper method for doCheckout [branch name].
     *
     * @param args checkout [branch name].
     */
    private void checkoutBranch(String[] args) {
        File branch;
        String inCommitStr, branchPath = "";

        branch = new File(headsFolder() + SEPARATOR + args[1]);
        try {
            branchPath = branch.getCanonicalPath();
        } catch (IOException e) {
            Main.softFail("File IO Failed.");
        }
        if (!branch.exists()) {
            Main.softFail("No such branch exists.");
        } else if (GitletUtils.getHeadPtr().equals(branchPath)) {
            Main.softFail("No need to checkout the current branch.");
        }
        inCommitStr = Utils.readContentsAsString(branch);
        checkoutCommit(Commit.retrieveCommit(inCommitStr), inCommitStr);
        Utils.writeContents(headPtr(), branchPath);
    }

    /**
     * Checks out a specific file from a commit; helper method for doCheckout.
     * @param args java gitlet.Main checkout [commit ID] -- [file name].
     */
    private void checkoutFileFromCommit(String[] args) {
        byte[] fileContents;

        fileContents = fileFromCommit(args[1], args[3]);
        Utils.writeContents(new File(CWD + SEPARATOR + args[3]),
                (Object) fileContents);
    }

    /**
     * Returns contents of a specific file from a specific commit.
     *
     * @param commitHash Commit hash.
     * @param fileName File name.
     * @return The contents of the file read.
     */
    private byte[] fileFromCommit(String commitHash, String fileName) {
        HashMap<String, String> commitBlobs;
        String suffix = "-" + fileName;

        Commit commit = Commit.retrieveCommit(commitHash);
        if (commit == null) {
            Main.softFail("No commit with that id exists.");
        } else {
            commitBlobs = commit.getBlobs();
            if (commitBlobs == null || !commit.getBlobs().containsKey(Utils
                    .sha1(fileName) + suffix)) {
                Main.softFail("File does not exist in that commit.");
            } else {
                return Utils.readContents(new File(blobsFolder() + SEPARATOR
                        + commitBlobs.get(Utils.sha1(fileName) + suffix)
                        + suffix));
            }
        }
        return new byte[]{};
    }

    /**
     * Checks out all files in a commit.
     * @param inCommit The commit from which to check out files.
     * @param inCommitStr Commit hash.
     */
    private void checkoutCommit(Commit inCommit, String inCommitStr) {
        HashMap<String, String> inBlobs, currBlobs;

        if (inCommit == null) {
            Main.softFail("No commit with that id exists.");
        }
        inBlobs = inCommit.getBlobs();
        currBlobs = GitletUtils.lastCommit().getBlobs();

        if (inBlobs != null) {
            for (String fileName : inBlobs.keySet()) {
                isOverWritingUnTracked(fileName, currBlobs == null
                        || !currBlobs.containsKey(fileName));
            }
        }
        if (currBlobs != null) {
            for (String fileName : currBlobs.keySet()) {
                File currFile = new File(CWD + SEPARATOR + fileName
                        .substring(Utils.UID_LENGTH + 1));
                if (currFile.exists()) {
                    currFile.delete();
                }
            }
        }
        GitletUtils.clearStagingArea();

        if (inBlobs != null) {
            for (String fileName : inBlobs.keySet()) {
                checkoutFileFromCommit(new String[] {"checkout", inCommitStr,
                    "--", fileName.substring(Utils.UID_LENGTH + 1)});
            }
        }
    }

    /**
     * Makes sure not un-tracked file is overwritten.
     *
     * @param fileName Filename for  incoming file.
     * @param cond The check used to when determine if a file is overwritten.
     */
    private void isOverWritingUnTracked(String fileName, boolean cond) {
        File incomingFile = new File(gitletFolder() + SEPARATOR + ".."
                + SEPARATOR + fileName.substring(Utils.UID_LENGTH + 1));
        if (incomingFile.exists() && cond) {
            Main.softFail("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
        }
    }

    /**
     * Returns true if the staging area is empty.
     *
     * @return True if staging area is empty.
     */
    private boolean isEmptyStagingArea() {
        return Objects.requireNonNull(Utils.plainFilenamesIn(ADDITION_FOLDER))
                .size() == 0 && Objects.requireNonNull(Utils.plainFilenamesIn
                (REMOVAL_FOLDER)).size() == 0;
    }

    /**
     * Returns the gitlet directory.
     * @return .gitlet directory.
     */
    static File gitletFolder() {
        return _gitletFolder;
    }
    /**
     * Returns the blobs directory.
     * @return blobs directory.
     */
    static File blobsFolder() {
        return _blobsFolder;
    }
    /**
     * Returns the commits directory.
     * @return commits directory.
     */
    static File commitsFolder() {
        return _commitsFolder;
    }
    /**
     * Returns the refs directory.
     * @return refs directory.
     */
    static File refsFolder() {
        return _refsFolder;
    }
    /**
     * Returns the heads directory.
     * @return heads directory.
     */
    static File headsFolder() {
        return _headsFolder;
    }
    /**
     * Returns the head pointer.
     * @return Head pointer.
     */
    static File headPtr() {
        return _headPtr;
    }
}
