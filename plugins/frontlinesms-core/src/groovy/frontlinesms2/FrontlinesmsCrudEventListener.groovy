package frontlinesms2

import org.grails.datastore.mapping.engine.event.*

class FrontlinesmsCrudEventListener extends AbstractPersistenceEventListener {
	def autoforwardService

	public FrontlinesmsCrudEventListener(datastore) {
		super(datastore)
	}

	@Override
	protected void onPersistenceEvent(final AbstractPersistenceEvent event) {
		println "## Captured CRUD Event ## ${event.eventType}"
	}

	boolean supportsEventType(Class sourceType){
		return true
	}
}
