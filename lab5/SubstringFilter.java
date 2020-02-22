import java.rmi.activation.ActivationGroup_Stub;

/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen, Farhad Alemi
 */
public class SubstringFilter extends TableFilter {

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        _colName = colName;
        _subStr = subStr;
    }

    @Override
    protected boolean keep() {
        return candidateNext().getValue(headerList().indexOf(_colName))
                .contains(_subStr);
    }

    /** Column Name */
    String _colName;

    /** Substring */
    String _subStr;
}
