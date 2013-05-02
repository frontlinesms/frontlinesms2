package frontlinesms2

import org.apache.commons.lang.builder.HashCodeBuilder

class SmssyncFconnectionQueuedDispatch implements Serializable {
	static mapping = {
		id composite: ['connectionId', 'dispatchId']
		version false
		table 'smssync_dispatch'
	}

	long connectionId
	long dispatchId

	boolean equals(that) {
		that instanceof SmssyncFconnectionQueuedDispatch &&
				that.connectionId == this.connectionId &&
				that.dispatchId == this.dispatchId
	}

	int hashCode() {
		return new HashCodeBuilder().append(connectionId).append(dispatchId).toHashCode()
	}

	static SmssyncFconnectionQueuedDispatch create(SmssyncFconnection connection, Dispatch dispatch, boolean flush=false) {
		new SmssyncFconnectionQueuedDispatch(connectionId:connection.id,
				dispatchId:dispatch.id)
			.save(flush:flush, insert:true)
	}

	static void delete(SmssyncFconnection c, dispatches) {
		if(dispatches) {
			executeUpdate "DELETE FROM SmssyncFconnectionQueuedDispatch WHERE connectionId=:connectionId AND dispatchId in :dispatchIds",
					[connectionId:c.id, dispatchIds:dispatches*.id]
		}
	}

	static getDispatches(connection) {
		// TODO should do this in a single query
		def dispatchIds = Dispatch.executeQuery("SELECT q.dispatchId FROM SmssyncFconnectionQueuedDispatch q WHERE q.connectionId=:connectionId",
				[connectionId:connection.id])
		Dispatch.getAll(dispatchIds) - null
	}
}
