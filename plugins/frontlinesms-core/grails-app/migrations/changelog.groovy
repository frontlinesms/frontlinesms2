databaseChangeLog = {
	// N.B. it is not safe to modify, remove or rename a changelog
	// after a public release.  Please do not do so!

	changeSet(author: "geoffrey (generated)", id: "changelog") {
		// TODO add changes and preconditions here
	}

	//this is the beta 1 changelog and should not be renamed
	include file: 'changelog-1.0.groovy'

	include file: 'changelog-0.b2.groovy'

	include file: 'changelog-0.0.groovy'

	include file: 'changelog-0.2.groovy'

	include file: 'changelog-0.3.groovy'

	include file: 'changelog-0.4.groovy'

	include file: 'changelog-1.0-rc2.groovy'

	include file: 'changelog-2.0.groovy'

	include file: 'changelog-3.0.groovy'

	include file: 'changelog-3.1.groovy'

	include file: 'changelog-4.0.groovy'
}

