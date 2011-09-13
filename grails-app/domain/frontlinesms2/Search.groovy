package frontlinesms2

class Search {
	String name
	String searchString
	String contactString
	List owners
	String activityId
	def activity
	Group group
	String status
	boolean inArchive
	
	static constraints = {
		name(blank: false, nullable: false, maxSize: 255)
		searchString(blank: true, maxSize: 255)
		contactString(blank: true, nullable: true, maxSize: 255)
		activityId(nullable: true, blank: true, maxSize: 255)
		owners(nullable: true)
		group(nullable: true)
		status(nullable: true)
		inArchive(nullable: true)
	}
}
