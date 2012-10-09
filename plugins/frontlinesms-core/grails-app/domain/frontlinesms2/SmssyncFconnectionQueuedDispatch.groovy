package frontlinesms2

import org.apache.commons.lang.builder.HashCodeBuilder

class SmssyncFconnectionQueuedDispatch implements Serializable {
	static mapping = {
		id composite: ['connection', 'dispatch']
		version false
		table 'smssync_dispatch'
	}

	SmssyncFconnection connection
	Dispatch dispatch

	boolean equals(that) {
		that instanceof SmssyncFconnectionQueuedDispatch &&
				that.connection.id == this.connection.id &&
				that.dispatch.id == this.dispatch.id
	}

	int hashCode() {
		return new HashCodeBuilder().append(connection.id).append(dispatch.id).toHashCode()
	}

	static SmssyncFconnectionQueuedDispatch create(SmssyncFconnection connection, Dispatch dispatch, boolean flush=false) {
		new SmssyncFconnectionQueuedDispatch(connection:connection, dispatch: dispatch).save(flush: flush, insert: true)
	}

	static void delete(SmssyncFconnection c, List<Long> dispatchIds) {
		executeUpdate("DELETE FROM SmssyncFconnectionQueuedDispatch WHERE connection=:connection AND dispatch.id in :dispatchIds", [connection: c, dispatchIds: dispatchIds])
	}

	static getDispatches(connectionInstance) {
		GroupMembership.executeQuery("SELECT qd.dispatch FROM SmssyncFconnectionQueuedDispatch qd WHERE qd.connection=:connection", [connection: connectionInstance])
	}
}
