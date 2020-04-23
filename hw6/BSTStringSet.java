import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author Farhad Alemi
 */
public class BSTStringSet implements StringSet, Iterable<String>, SortedStringSet {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        putHelper(_root, s);
    }

    /** Helper method used for put operation. */
    private void putHelper(Node node, String s) {
        if (node == null) {
            _root = new Node(s);
        } else if (s.compareTo(node.s) < 0) {
            if (node.left == null) {
                node.left = new Node(s);
            } else {
                putHelper(node.left, s);
            }
        } else if (s.compareTo(node.s) > 0) {
            if (node.right == null) {
                node.right = new Node(s);
            } else {
                putHelper(node.right, s);
            }
        }
    }

    @Override
    public boolean contains(String s) {
        return containsHelper(_root, s);
    }

    /** Helper method used for contains operation. */
    private boolean containsHelper(Node node, String s) {
        if (node == null) {
            return false;
        } else if (s.compareTo(node.s) == 0) {
            return true;
        } else if (s.compareTo(node.s) < 0) {
            return containsHelper(node.left, s);
        } else {
            return containsHelper(node.right, s);
        }
    }

    @Override
    public List<String> asList() {
        BSTIterator iter = new BSTIterator(_root);
        LinkedList<String> lst = new LinkedList<>();
        while(iter.hasNext()) {
            lst.add(iter.next());
        }
        return lst;
    }

    /** Getter method used for testing purposes only. */
    Node getRoot() {
        return _root;
    }

    /** Represents a single Node of the tree. */
    static class Node {
        /** String stored in this Node. */
        private String s;

        /** Left child of this Node. */
        private Node left;

        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }

        /** Getter method used for testing purposes only. */
        String getString() {
            return s;
        }
        /** Getter method used for testing purposes only. */
        Node getRight() {
            return right;
        }

        /** Getter method used for testing purposes only. */
        Node getLeft() {
            return left;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        protected Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    /** Represents the bounded operator.*/
    private static class RangedBSTIterator extends BSTIterator {

        /** A new iterator over the labels in NODE.
         *  @param node the node for which iterator is created.
         */
        RangedBSTIterator(Node node, String low, String high) {
            super(node);
            _low = low;
            _high = high;
            calibrate();
        }

        /** Removes element lower than _LOW. */
        private void calibrate() {
            if (_toDo.empty()) {
                return;
            }
            String s = _toDo.peek().s;
            while (s.compareTo(_low) < 0) {
                super.next();
                s = _toDo.peek().s;
            }
        }

        @Override
        public boolean hasNext() {
            return super.hasNext() && _toDo.peek().s.compareTo(_high) < 0;
        }

        @Override
        public String next() {
            String next = super.next();
            /*while (next.compareTo(_low) < 0) {
                next = super.next();
            }*/
            if (next.compareTo(_high) >= 0) {
                _toDo.clear();
                return super.next();
            }
            return next;
        }

        /** Lower boundary - Inclusive. */
        String _low;
        /** Lower boundary - Exclusive. */
        String _high;
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    @Override
    public Iterator<String> iterator(String low, String high) {
        return new RangedBSTIterator(_root, low, high);
    }

    /** Root node of the tree. */
    private Node _root;
}
