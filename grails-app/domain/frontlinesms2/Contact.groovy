package frontlinesms2

class Contact {
	String name
	String address

    static constraints = {
		name(blank: true, maxSize: 255)
		address(unique: true, nullable: true)
    }
}
