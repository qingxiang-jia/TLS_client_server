import java.util.regex.Pattern;

/**
 * Validate the format of arguments.
 */
public class ArgsCheck
{
    /** regular expression taken from http://goo.gl/2IJ1B6 **/
    private static final Pattern DOMAIN_NAME = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");

    /** regular expression taken from http://goo.gl/gYeiGn **/
    private static final Pattern IP_ADDRESS = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");

    public static boolean isDomainName(String domainName)
    {
        return DOMAIN_NAME.matcher(domainName).find();
    }

    public static boolean isIPAddress(String ip)
    {
        return IP_ADDRESS.matcher(ip).find();
    }

    public static boolean isPositiveInteger(String integer)
    {
        boolean res = false;
        try {
            res = (Integer.parseInt(integer) > 0);
            return res;
        } catch (NumberFormatException e) {
            return res;
        }
    }

    // quick test
    public static void main(String args[])
    {
        System.out.println(ArgsCheck.isDomainName("cs.columbia.edu"));
        System.out.println(ArgsCheck.isDomainName("tokyo.clic.cs.columbia.edu"));
        System.out.println(ArgsCheck.isDomainName("tokyo.clic-lab.cs.columbia.edu"));
        System.out.println(ArgsCheck.isDomainName("d.c.v.a"));
        System.out.println(ArgsCheck.isDomainName("123.f.a.c"));

        System.out.println(ArgsCheck.isIPAddress("128.59.0.12"));
        System.out.println(ArgsCheck.isIPAddress("00.00.00.00"));
        System.out.println(ArgsCheck.isIPAddress("1231.123.1.1"));
        System.out.println(ArgsCheck.isIPAddress("1.2.3.1.3.4"));
        System.out.println(ArgsCheck.isIPAddress("259/231/231/21"));

        System.out.println(ArgsCheck.isPositiveInteger("-12"));
        System.out.println(ArgsCheck.isPositiveInteger("21"));
        System.out.println(ArgsCheck.isPositiveInteger("0"));
    }

}
