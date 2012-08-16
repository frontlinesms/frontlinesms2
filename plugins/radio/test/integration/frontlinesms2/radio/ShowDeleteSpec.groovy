package frontlinesms2.radio

import frontlinesms2.*
import grails.plugin.spock.IntegrationSpec

class ShowDeleteSpec extends IntegrationSpec {
	def trashService

	def 'Restoring a show with a separately deleted activity should leave nothing in the trash'() {
		given:
			def a = Announcement.build()
			def s = RadioShow.build()
			s.addToActivities(a).save(failOnError:true)
		when:
			trashService.sendToTrash(a)
			trashService.sendToTrash(s)
		then:
			Trash.count() == 2
		when:
			trashService.restore(s)
		then:
			!Trash.findAll()
	}
}

