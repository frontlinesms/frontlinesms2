package frontlinesms2

class Contact {
	String name
	String address

    static constraints = {
		name(blank: false)
		address(unique: true, blank: false)
    }
}
