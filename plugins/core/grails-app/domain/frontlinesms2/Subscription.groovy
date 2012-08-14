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
	String joinAutoreplyText
	String leaveAutoreplyText

	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = Subscription.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
			})
		joinAutoreplyText(nullable:true, blank:false)
		joinAutoreplyText(nullable:true, blank:false)
		keyword(nullable:true)
	}

	def addToMessages(def message) {}
	def processKeyword(Fmessage message, boolean exactMatch) {
		def action = getAction(message.text,exactMatch)
		def foundContact = Contact.findByMobile(message.src)
		if(action == Action.JOIN){
			 if(!foundContact){
			 	group.addToMembers(new Contact(name:"", mobile:message.src).save(failOnError:true));
			 }else{
			 	group.addToMembers(foundContact);
			 }
		}else if(action == Action.LEAVE){
			foundContact?.removeFromGroup(group)
		}else if(action == Action.TOGGLE){
			if(foundContact){
				if(foundContact.isMemberOf(group)){
					foundContact.removeFromGroup(group)
				}else{
					group.addToMembers(foundContact);
				}
			}else{
				group.addToMembers(new Contact(name:"", mobile:message.src).save(failOnError:true));			
			}
		}
	}

	Action getAction(String messageText, boolean exactMatch) {
		def words =  messageText.trim().toUpperCase().split(/\s+/)
		if(words.size() == 1){
			if(words[0].contains(keyword.value)){
				if(hasAtLeastOneAlias(joinAliases,words[0])){
					return Action.JOIN
				}else if(hasAtLeastOneAlias(leaveAliases,words[0])){
					return Action.LEAVE
				}else if (exactMatch){
					return Action.TOGGLE
				}else if(!exactMatch){
					return null;
				}
			}
		}
		else if (words.size() > 1 && exactMatch){
			if(joinAliases.toUpperCase().split(",").contains(words[1].trim())){
				return Action.JOIN
		 	}else if(leaveAliases.toUpperCase().split(",").contains(words[1].trim())){
		 		return Action.LEAVE
		 	}else{
		 		return Action.TOGGLE
		 	}
		}
	}

	def hasAtLeastOneAlias(aliases,message){
		aliases.toUpperCase().split(",").contains(message.substring(keyword.value.length()))	
	}
}


