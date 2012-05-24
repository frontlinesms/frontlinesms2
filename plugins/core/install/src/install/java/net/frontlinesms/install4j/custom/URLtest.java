import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLtest {
	public boolean test(String text) {
	    Pattern p2 = Pattern.compile("(((file|gopher|news|nntp|telnet|http|ftp|https|ftps|sftp)://)|(www\\.))+(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?");
	    Matcher m = p2.matcher(text);
	    boolean matchFound = m.matches();
	    if(matchFound == false){ javax.swing.JOptionPane.showMessageDialog(null, "Invalid Webaddress \nTry e.g. www.google.com"); }
	    return matchFound;
	}
}
