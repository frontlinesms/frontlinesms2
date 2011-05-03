package frontlinesms2

class Group {
	static hasMany = [members: Contact]
	String name

	static mapping = { table 'grup' }

	static constraints = { name(unique: true, nullable: false, blank: false, maxSize: 255) }
}
