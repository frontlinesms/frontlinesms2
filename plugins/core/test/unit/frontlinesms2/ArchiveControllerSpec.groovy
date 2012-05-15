package frontlinesms2

import spock.lang.*
import groovy.lang.MetaClass;

@TestFor(ArchiveController)
@Mock([Folder, Fmessage])
class ArchiveControllerSpec extends Specification {
        def "deleted folders do not appear in the archive section"() {
                given:
                        def folder = new Folder(name:'rain', archived:true).save()
                        assert folder.archived
                when:
                        controller.folderList()
                then:
                        model.folderInstanceList == [folder]
                when:
                        folder.deleted = true
			folder.save()
                        controller.folderList()
                then:
                        !model.folderInstanceList
        }
}

