package frontlinesms2

class Search {
	String searchString
	String contactString
	List owners
	String activityId
	Group group
	String status
	
	static constraints = {
		searchString(blank: true, maxSize: 255)
		contactString(blank: true, nullable: true, maxSize: 255)
		activityId(nullable: true, blank: true, maxSize: 5)
		owners(nullable: true)
		group(nullable: true)
		status(nullable: true)
	}
}
