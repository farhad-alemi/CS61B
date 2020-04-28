package gitlet;

/**
 * Driver class for Gitlet, the tiny version-control system.
 * @author Farhad Alemi
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS.
     *
     * @param args Contains input commands in <COMMAND> <OPERAND> form.
     */
    public static void main(String... args) {
        if (args.length == 0) {
            softFail("Please enter a command.");
        }
        Gitlet gitlet = new Gitlet();
        switch (args[0]) {
        case "init":
            gitlet.doInit(args);
            break;
        case "add":
            gitlet.doAdd(args);
            break;
        case "commit":
            gitlet.doCommit(args, null);
            break;
        case "rm":
            gitlet.doRm(args);
            break;
        case "log":
            gitlet.doLog(args);
            break;
        case "global-log":
            gitlet.doGlobalLog(args);
            break;
        case "find":
            gitlet.doFind(args);
            break;
        case "status":
            gitlet.doStatus(args);
            break;
        case "checkout":
            gitlet.doCheckout(args);
            break;
        case "branch":
            gitlet.doBranch(args);
            break;
        case "rm-branch":
            gitlet.doRmBranch(args);
            break;
        case "reset":
            gitlet.doReset(args);
            break;
        case "merge":
            gitlet.doMerge(args);
            break;
        case "add-remote":
            gitlet.doAddRemote(args); break;
        case "rm-remote":
            gitlet.doRmRemote(args); break;
        case "push":
            gitlet.doPush(args); break;
        case "fetch":
            gitlet.doFetch(args); break;
        case "pull":
            gitlet.doPull(args); break;
        default:
            System.out.println("No command with that name exists.");
        }
    }

    /**
     * Halts the program execution after printing the error-specific message.
     *
     * @param msg Shutdown message.
     */
    static void softFail(String msg) {
        System.out.println(msg);
        System.exit(0);
    }
}
