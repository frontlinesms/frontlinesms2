package frontlinesms2

class Subscription extends Activity{
	enum Action { TOGGLE, JOIN, LEAVE }

	def processKeyword(Fmessage message, boolean exactMatch) {}
	Action getAction(String messageText, boolean exactMatch) {}
}

