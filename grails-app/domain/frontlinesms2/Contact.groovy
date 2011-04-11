package frontlinesms2

class Contact {
	String name
	String address

    static constraints = {
		address(unique: true)
    }
}
