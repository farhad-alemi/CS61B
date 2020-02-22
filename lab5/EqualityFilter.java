/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen, Farhad Alemi
 */
public class EqualityFilter extends TableFilter {

    public EqualityFilter(Table input, String colName, String match) {
        super(input);
        _colName = colName;
        _key = match;

    }

    @Override
    protected boolean keep() {
        return _key.equals(candidateNext().getValue(headerList()
                .indexOf(_colName)));
    }

    /** Column Name */
    String _colName;

    /** Key String value */
    String _key;
}
