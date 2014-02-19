package frontlinesms2

import groovy.time.*

class MissedCall extends Interaction {
	def mobileNumberUtilService

	boolean inbound = true
	String outboundContactName = null

	static constraints = {
		inboundContactName nullable:true
		outboundContactName nullable:true
	}

	static namedQueries = {
		inbox { getOnlyStarred=false, archived=false ->
			and {
				eq("isDeleted", false)
				eq("archived", archived)
				if(getOnlyStarred)
					eq("starred", true)
			}
		}
		forReceivedStats { params ->
			def startDate = params.startDate.startOfDay
			def endDate = params.endDate.endOfDay
			and {
				eq('isDeleted', false)
				between("date", startDate, endDate)
			}
		}
	} << Interaction.namedQueries // Named Queries are not inherited

	def getDisplayName() {
		if(inboundContactName) return inboundContactName
		else if(id) return src.toPrettyPhoneNumber()
		else return Contact.findByMobile(src)?.name?: src.toPrettyPhoneNumber()
	}

	def getContactFlagCSSClasses() {
		def flagCssClass
		mobileNumberUtilService.getFlagCSSClasses(src)
	}

	static def countAllMissedCalls() {
		['inbox', 'deleted'].collectEntries { [it, MissedCall[it].count()] }
	}

	static def countUnreadMessages() {
		MissedCall.unread.count()
	}

	static def getMessageStats(params) {
		def asKey = { date -> date.format('dd/MM') }
		
		def dates = [:]
		(params.startDate..params.endDate).each { date ->
			dates[asKey(date)] = [sent:0, received:0]
		}
		MissedCall.forReceivedStats(params).list().each { m ->
			++dates[asKey(m.date)].received
		}
		
		dates
	}
}

