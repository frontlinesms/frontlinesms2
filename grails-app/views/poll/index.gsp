<%@ page contentType="text/html;charset=UTF-8" %>
<table>
	<thead>
	<tr>
		<th><g:checkBox></g:checkBox></th>
		<th>Name</th>
		<th>Type</th>
		<th>Date</th>
		<th>Messages</th>
	</tr>
	</thead>
	<tbody>
	<g:each in="${polls}" var="poll">
		<tr>
			<td><g:checkBox></g:checkBox></td>
			<td>${poll.title}</td>
			<td>Poll<td>
			<td>${poll.dateCreated}<td>
			<td>0<td>
		</tr>
	</g:each>
	</tbody>
</table>
