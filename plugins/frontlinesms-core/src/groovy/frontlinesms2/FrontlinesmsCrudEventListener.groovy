package frontlinesms2

import org.grails.datastore.mapping.engine.event.*

class FrontlinesmsCrudEventListener extends AbstractPersistenceEventListener {
	public FrontlinesmsCrudEventListener(datastore) {
		super(datastore)
	}

	@Override
	protected void onPersistenceEvent(final AbstractPersistenceEvent event) {
		switch(event.eventType) {
		    case "PreDelete":
		    	println "PRE DELETE ${event.entityObject}"
			switch(event.entityObject.class){
				case Contact:
					autoforwardService.handleDeleteContact(event.entityObject)
				break
				case Group:
					autoforwardService.handleDeleteGroup(event.entityObject)
				break
				case SmartGroup:
					autoforwardService.handleDeleteSmartGroup(event.entityObject)
				break
			}
			break
		}
	}

	boolean supportsEventType(Class sourceType){
		return true
	}
}
