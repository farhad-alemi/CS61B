# Gitlet Design Document
**Name**: Farhad Alemi

## Classes and Data Structures
### Main
The main the driver class which runs the git simulator and passes the arguments
to Gitlet object.
##### *Methods*
* static void softFail(String msg): Halts the program execution after printing the error-specific message.

### Commit
Represents the commits/snapshots similar to real Git.
##### *Fields*
1. *String* message: The log message.
2. *Date* _timestamp: The commit date.
3. *String[]* parentCommit: Reference to the parent commit's hash.
4. *HashMap<String, String>* _blobs: A mapping of file names to blob references.

##### *Methods*
* Commit(String message, String[] parentCommits): The two-parameter constructor which does the initialization.
* Commit(String message): The constructor which has null has parentCommits.
* public String getMessage(): Getter method that returns the commit message.
* public Date getTimestamp(): Getter method that returns the commit timestamp.
* public String[] getParents(): Getter method that returns the parent commits' Hash.
* HashMap<String, String> getBlobs(): Getter methods which returns the blobs tracked in the commit.
* void setBlobs(HashMap<String, String> blobs): Setter methods which updates the blobs tracked in the commit.
* public static List<String> getAncestors(String commitHash):Returns a list of all the ancestors starting from the commit with the given hash.
* public static Commit retrieveCommit(String commitHash): Returns the commit which has the given hash.
* public static void printCommit(Commit commit, String commitHash): Prints the commit to the standard output.
* public static String findSplitPoint(List<String> currParents, List<String> givenParents): Returns the first common (nearest in currParents) parent from the lists of ancestors.
* public static String hashAutoComplete(String commitHash): Auto completes the commit hash, if possible.

### Gitlet
The Git simulator which invokes various methods based on various commands.
##### *Fields*
* Universal file separator for both Unix- and Windows-based machines: static final String SEPARATOR
* Current Working Directory: static final File CWD
* Main metadata folder: private static File _gitletFolder.
* The blobs folder used for storing file snapshots: private static File _blobsFolder.
* The commits folder used for storing commits: private static File _commitsFolder.
* The index folder used to as staging area: static final File INDEX_FOLDER.
* The addition folder contains files staged for addition: static final File ADDITION_FOLDER.
* The removal folder contains files staged for removal: static final File REMOVAL_FOLDER.
* The refs folder used to store various pointers: private static File _refsFolder.
* The folder which contains file(s) named after each branch: private static File _headsFolder
* The file which contains the full path for the active branch: private static File _headPtr.
* The initial branch name: static final String INIT_BRANCH.

##### *Methods*
1. *void* doInit: Creates a new Gitlet version-control system in the current
 directory. It initially starts with an initialized commit having "initial
 commit" as the commit message. The branch for this initial commit is named
 "master". The timestamp for this initial commit is 1/1/1970.
2. *void* doAdd: Sets up file for staging. Staging an already staged file
overwrites the previous entry and writes the new contents. The staging area has 
to be inside .gitlet directory.
3. *void* doCommit: Creates a snapshot of files. The commit object mimics the 
characteristics of a the git commit command. Commit only takes the snapshot of
files that have already been staged. By default, a commit is the same as its 
parent. Updates to the commits include files staged. Performing a commit clears 
the staging area.
4. *void* doRm: Un-stages the file if it is currently staged. It also stages the
files for removal if the file is tracked in the current commit. If the file 
still exists in the working directory, doRm removes it.
5. *void* doLog: Provides a log of the commits starting at current head. It goes
back until the initial commit ignoring any second parent (due to merge).
6. *void* doGlobalLog: Provides a log of all commits ever made. The order of the 
commits is not specified.
7. *void* doFind: Prints out the ids of all commits which have the given commit
 message on separate lines. Put the commit message in quotations if it is a 
 multi-word message.
8. *void* doStatus: Displays existing branches and denoting current branch.
9. *void* doCheckout args: Performs the git checkout operation according to
 supplied args.
10. *void* doBranch(*String* name): Creates a new branch with the given name and
 sets up the head node appropriately.
11. *void* doRmBranch(*String* name): Removes the branch with the given name.
12. *void* doReset: Performs the git reset command.
13. *void* doMerge: Merges the files to current branch from another branch.
14. /***isInitialized: Returns true iff there is a .gitlet directory within the CWD***/
15. /*** static boolean validateNumArgs(String[] args, int n) ***/

## Algorithms
##### The Blob class:
* The blobs are basically snapshots of files. Once a blob is added through commit, 
it is never deleted.
##### the Commit class:
* The commit class objects have hashes to contents of files. They also
include the log, and timestamp information.
##### The Gitlet class:
* The commit objects are stored as files inside "commits" folder.


## Persistence
Commits are stored using the serialization technique with and are identified using
 their SHA1 hashes.
Each commit is saved to a file insides "commits" and contains the information pertaining
 to its field.
The simulator checks for such files upon being launched.
