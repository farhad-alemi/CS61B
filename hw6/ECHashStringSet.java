import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author Farhad Alemi
 */
class ECHashStringSet implements StringSet {
    /** Default array size. */
    private static final int DEFAULT_ARRAY_SIZE = 4;
    /** Default load factor limit. */
    private static final double LOAD_FACTOR_LIMIT = 0.5;
    /** The factor by which to resize the array. */
    private static final int MULTIPLICATION_FACTOR = 2;

    /** The default constructor which initializes the main array */
    ECHashStringSet() {
        resize(DEFAULT_ARRAY_SIZE);
        lstCount = 0;
    }

    /** Resize the hash table to NEWSIZE provided that the table has met the
     * load factor limit requirement. */
    private void resize(int newSize) {
        if (bins != null && loadFactor() <= LOAD_FACTOR_LIMIT) {
            return;
        }

        LinkedList<String>[] oldBins = bins;
        int oldBinsSize = (oldBins == null) ? 0 : bins.length;
        bins = (LinkedList<String>[]) new LinkedList<?>[newSize];
        for (int i = 0; i < bins.length; ++i) {
            bins[i] = new LinkedList<>();
        }
        lstCount = 0;
        for (int i = 0; i < oldBinsSize; ++i) {
            while (oldBins[i].size() != 0) {
                put(oldBins[i].pop());
            }
        }
    }

    /** Hash function for the set. */
    private int hashFunction(String s) {
        return (bins == null || bins.length == 0) ? -1 : (s.hashCode()
                & 0x7FFFFFFF) % bins.length;
    }

    @Override
    public void put(String s) {
        if (bins == null || bins.length == 0) {
            return;
        }
        if (loadFactor() > LOAD_FACTOR_LIMIT) {
            resize(bins.length * MULTIPLICATION_FACTOR);
        }
        if (bins[hashFunction(s)].size() == 0) {
            ++lstCount;
        }
        bins[hashFunction(s)].add(s);
    }

    /** Returns the load factor for the hash. */
    private double loadFactor() {
        return (bins == null) ? -1 : lstCount / bins.length;
    }

    @Override
    public boolean contains(String s) {
        return bins[hashFunction(s)].contains(s);
    }

    @Override
    public List<String> asList() {
        if (bins == null || bins.length == 0) {
            return null;
        }
        LinkedList<String> lst = new LinkedList<>(bins[0]);
        for (LinkedList<String> bin : bins) {
            lst.addAll(bin);
        }
        return lst;
    }

    /** The main hash array. */
    private LinkedList<String>[] bins;
    /** Number of non-empty linked lists. */
    double lstCount;
}
