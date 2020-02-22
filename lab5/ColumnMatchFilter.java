/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen, Farhad Alemi
 */
public class ColumnMatchFilter extends TableFilter {

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        _colName1 = colName1;
        _colName2 = colName2;
    }

    @Override
    protected boolean keep() {
        String firstEntry = candidateNext().getValue(headerList().indexOf(_colName1));
        String secondEntry = candidateNext().getValue(headerList().indexOf(_colName2));
        return firstEntry.equals(secondEntry);
    }

    /** First Column Name */
    String _colName1;

    /** Second Column Name */
    String _colName2;
}
