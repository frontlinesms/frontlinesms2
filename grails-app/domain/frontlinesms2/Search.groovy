package frontlinesms2

import groovy.time.*

class Search {
	String name
	String searchString
	String contactString
	List owners
	String activityId
	Group group
	String status
	Date startDate
	Date endDate
	String contactString
	List selectedCustomFields
	boolean inArchive
	
	static constraints = {
		name(blank: false, nullable: false, maxSize: 255)
		searchString(blank: true, maxSize: 255)
		contactString(blank: true, nullable: true, maxSize: 255)
		activityId(nullable: true, blank: true, maxSize: 255)
		owners(nullable: true)
		group(nullable: true)
		status(nullable: true)
		startDate(nullable: true)
		endDate(nullable: true)
		contactString(nullable:true)
		selectedCustomFields(nullable:true)
		inArchive(nullable: true)
	}
}
