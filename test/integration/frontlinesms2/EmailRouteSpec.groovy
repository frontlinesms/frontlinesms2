package frontlinesms2

import routing.CamelIntegrationSpec

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 16/03/11
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
abstract class EmailRouteSpec extends CamelIntegrationSpec {
	protected Address[] emailAddress(String... strings) {
		def addresses = []
		for(s in strings) addresses << new InternetAddress(s)
		return addresses as Address[]
	}
}
