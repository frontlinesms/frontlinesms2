databaseChangeLog = {

	changeSet(author: "geoffrey (generated)", id: "changelog") {
		// TODO add changes and preconditions here
	}
	//this is the beta 1 changelog and should not be renamed
	include file: 'changelog-1.0.groovy'
	include file: 'changelog-0.b2.groovy'

	include file: 'changelog-0.0.groovy'
}
