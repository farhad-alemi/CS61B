import java.util.Arrays;

/** Disjoint sets of contiguous integers that allows (a) finding whether
 *  two integers are in the same set and (b) unioning two sets together.  
 *  At any given time, for a structure partitioning the integers 1 to N, 
 *  into sets, each set is represented by a unique member of that
 *  set, called its representative.
 *  @author Farhad Alemi
 */
public class UnionFind {

    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        parents = new int[N + 1];
        sizes = new int[N + 1];

        for (int i = 0; i < parents.length; ++i) {
            parents[i] = i;
        }
        Arrays.fill(sizes, 1);
        sizes[0] = -1;
        parents[0] = -1;
    }

    /** Return the representative of the set currently containing V.
     *  Assumes V is contained in one of the sets. */
    public int find(int v) {
        if (v <= 0 || v >= parents.length) {
            return -1;
        } else if (parents[v] == v) {
            return v;
        } else {
            int parent = find(parents[v]);
            if (parent != parents[v]) {
                parents[v] = parent;
            }
            return parent;
        }
    }

    /** Return true iff U and V are in the same set. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single set, returning its representative. */
    public int union(int u, int v) {
        int sizeU, sizeV, parentU = find(u), parentV = find(v);

        if (samePartition(u, v)) {
            return parentU;
        } else {
            sizeU = sizes[parentU];
            sizeV = sizes[parentV];

            if (sizeU > sizeV) {
                parents[parentV] = parentU;
                sizes[parentU] += sizeV;
                return parentU;
            } else {
                parents[parentU] = parentV;
                sizes[parentV] += sizeU;
                return parentV;
            }
        }
    }

    /** Parents array. */
    int[] parents;

    /** Sizes array. */
    int[] sizes;
}
