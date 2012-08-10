package frontlinesms2

class Subscription extends Activity{
//> CONSTANTS
	static String getShortName() { 'subscription' }

//> PROPERTIES
	enum Action { TOGGLE, JOIN, LEAVE }

	String name
	static hasOne = [keyword: Keyword]
	Group group
	Action defaultAction = Action.TOGGLE
	String joinAliases
	String leaveAliases

	def addToMessages(def message) {}
	def processKeyword(Fmessage message, boolean exactMatch) {}
	Action getAction(String messageText, boolean exactMatch) {}
}

