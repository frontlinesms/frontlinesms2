package frontlinesms2

import frontlinesms2.Fmessage
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

class Trash {
	Date dateCreated
	Long objectId
	String objectClass
	String displayName
	String displayText
	
	static constraints = {
		objectId unique:'objectClass'
		displayName(nullable: true)
		displayText(nullable: true)
	}
	
	def getObject() {
		// N.B. Class.forName will not work as expected here
		Thread.currentThread().contextClassLoader.loadClass(objectClass).get(objectId)
	}

	static def findByObject(def o) {
		findByObjectIdAndObjectClass(o.id, getClass(o))
	}

	static def deleteForAll(def objects) {
		objects.each { o ->
			findByObject(o)?.delete()
		}
	}

	static def create(def o, args=[:]) {
		args.objectId = o.id
		args.objectClass = getClass(o)
		return new Trash(args)
	}

	private static def getClass(def o) {
		return GrailsHibernateUtil.unwrapIfProxy(o).getClass().name
	}
}

