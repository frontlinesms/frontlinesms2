package frontlinesms2

import org.apache.commons.lang.builder.HashCodeBuilder

class QueuedDispatch implements Serializable {
	static mapping = {
		id composite: ['connectionId', 'dispatchId']
		version false
	}

	long connectionId
	long dispatchId

	boolean equals(that) {
		that instanceof QueuedDispatch &&
				that.connectionId == this.connectionId &&
				that.dispatchId == this.dispatchId
	}

	int hashCode() {
		return new HashCodeBuilder().append(connectionId).append(dispatchId).toHashCode()
	}

	static QueuedDispatch create(SmssyncFconnection connection, Dispatch dispatch, boolean flush=false) {
		new QueuedDispatch(connectionId:connection.id,
				dispatchId:dispatch.id)
			.save(flush:flush, insert:true)
	}

	static void delete(SmssyncFconnection c, dispatches) {
		if(dispatches) {
			executeUpdate "DELETE FROM QueuedDispatch WHERE connectionId=:connectionId AND dispatchId in :dispatchIds",
					[connectionId:c.id, dispatchIds:dispatches*.id]
		}
	}

	static getDispatches(connection) {
		// TODO should do this in a single query
		def dispatchIds = Dispatch.executeQuery("SELECT q.dispatchId FROM QueuedDispatch q WHERE q.connectionId=:connectionId",
				[connectionId:connection.id])
		Dispatch.getAll(dispatchIds) - null
	}
}
