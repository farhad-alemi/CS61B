/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static String P1 = "((0?[1-9]{1})|(1[0-2]{1}))[/]((0*[1-9]{1})" +
            "|([1-2]{1}[0-9]{1})|(3[01]{1}))[/](19[0-9]{2}|[2-9][0-9]{3})";

    /** Pattern to match 61b notation for literal IntLists. */
    public static String P2 = "\\([\\d]+([,][ ]+[\\d]*)*\\)";

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static String P3 = "[^\\.\\-].*[^\\.\\-]";

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static String P4 = "[^\\d][^ ]*";

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    public static String P5 = "((0?0?[\\d])|(0?[\\d][\\d])|(1[\\d][\\d])" +
            "|(2[0-4][\\d])|(25[0-5]))([\\.]((0?0?[\\d])|(0?[\\d][\\d])" +
            "|(1[\\d][\\d])|(2[0-4][\\d])|(25[0-5]))){3}";

}
