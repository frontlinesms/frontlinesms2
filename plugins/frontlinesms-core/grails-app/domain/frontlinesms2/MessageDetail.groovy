package frontlinesms2

class MessageDetail {
	static belongsTo = [message: TextMessage]
	enum OwnerType { ACTIVITY, STEP }

	OwnerType ownerType
	Long ownerId
	String value
	
	static constraints = {
	}
	
}
