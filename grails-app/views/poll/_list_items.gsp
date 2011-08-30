<g:if test="${polls.size() > 0}">
<table id="list-items">
	<thead>
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th>Date</th>
		<th>Messages</th>
	</tr>
	</thead>
	<tbody>
		<g:each in="${polls}" var="poll">
			<tr>
				<td>
					<g:link controller="message" action="poll" params="[archived: true]" id="${poll.id}">
						${poll.title}
					</g:link>
				</td>
				<td>
					<g:link controller="message" action="poll" params="[archived: true]" id="${poll.id}">
						Poll
					</g:link>
				</td>
				<td>
					<g:link controller="message" action="poll" params="[archived: true]" id="${poll.id}">
						<g:formatDate format="dd-MMM-yyyy hh:mm" date="${poll.dateCreated}"/>
					</g:link>
				</td>
				<td>
					<g:link controller="message" action="poll" params="[archived: true]" id="${poll.id}">
						${poll.countMessages()}
					</g:link>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
</g:if>
<g:else>
	No archived activities
</g:else>
