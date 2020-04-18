# Gitlet Design Document

**Name**: Farhad Alemi

## Classes and Data Structures
### Main
The main the driver class which runs the git simulator and passes the arguments
to Gitlet object.

### Blob
Represents the contents of files.
##### *Fields*

### Commit
Represents the commits/snapshots similar to real Git.
##### *Fields*
1. *String* log: The log message.
2. *String* commitDate: The commit date.
3. *String* hash: The commit hash.
4. *String* author: Commit author.
5. *Commit* parentCommit: Reference to a parent commit.
6. *Blob* blob: A mapping of file names to blob references.

### Gitlet
The Git simulator which invokes various methods based on various commands.
##### *Fields*
*Commit* branchPtr: The Branch pointer.
* LinkedList<Commit*> The list of all commits.
##### *Methods*
1. *void* doInit: Creates a new Gitlet version-control system in the current
 directory.
2. *void* doAdd: Sets up file for staging.
3. *void* doCommit: Creates a snapshot of files.
4. *void* doRm: Un-stages the file if it is currently staged.
5. *void* doLog: Provides a log of the commits starting at current head.
6. *void* doGlobalLog: Provides a log of all commits ever made.
7. *void* doFind: Prints out the ids of all commits which have the given commit
 message.
8. *void* doStatus: Displays existing branches and denoting current branch.
9. *void* doCheckout args: Performs the git checkout operation according to
 supplied args.
10. *void* doBranch(*String* name): Creates a new branch with the given name and
 sets up the head node appropriately.
11. *void* doRmBranch(*String* name): Removes the branch with the given name.
12. *void* doReset: Performs the git reset command.
13. *void* doMerge: Merges the files to current branch from another branch.

## Algorithms
##### The Blob class:
* The blob class represent the contents of the files.
It is in this class that IO operations are be performed.
##### the Commit class:
* The commit class objects have pointer(s) to contents of files. They also
include the log, author and hash information.
##### The Gitlet class:
* The commit objects are stored in a linked list in Gitlet class.


## Persistence
Commits are stored using the serialization technique with ":" used as the
 delimiter.
Each commit is saved to a .commit file and contains the information pertaining
 to its field.
The simulator checks for such files upon being launched.