/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {

    public GreaterThanFilter(Table input, String colName, String ref) {
        super(input);
        _colName = colName;
        _ref = ref;
    }

    @Override
    protected boolean keep() {

        return candidateNext().getValue(headerList().indexOf(_colName)).compareTo(_ref) > 0;
    }

    /** Column Name */
    String _colName;

    /** Reference String */
    String _ref;
}
