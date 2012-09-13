package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class ArchiveISpec extends IntegrationSpec {
	def folderController, pollController, archiveController

	def setup() {
		pollController = new PollController()
		folderController = new FolderController()
		archiveController = new ArchiveController()
	}
}

