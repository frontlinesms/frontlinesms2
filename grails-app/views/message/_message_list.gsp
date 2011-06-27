  <%@ page contentType="text/html;charset=UTF-8" %>
<script type="text/javascript">
	$(document).ready(function(){
		$("tr #star").click(function(){
			if(!$(this).hasClass('starred')) {
			  var object= $(this)
				$.ajax({
					type: "POST",
					url: "/frontlinesms2/message/starMessage",
					data: ({messageId: $(this).attr("messageId")}), 
					success: function(){ addStar(object); }
				});
				
				
			} else if($(this).hasClass('starred')) {
				 var object = $(this)
				 $.ajax({
					type: "POST",
					url: "/frontlinesms2/message/starMessage",
					data: ({messageId: $(this).attr("messageId")}), 
					success: function(){ removeStar(object); }
				});
				
			}
			
			function addStar(dom){
				dom.addClass('starred');
				dom.empty().append("Remove Star");
			}
	
			function removeStar(dom){
				dom.removeClass('starred');
				dom.empty().append("Add Star");
			}
		});
	
	});

	
	
</script>
<g:if test="${messageInstanceTotal > 0}">
	<table id="messages">
		<thead>
			<tr>
				<td></td>
			    <td><g:message code="fmessage.src.label" default="Name"/></td>
			    <td><g:message code="fmessage.text.label" default="Snippet"/></td>
			    <td><g:message code="fmessage.date.label" default="Date"/></td>
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList }" status="i" var="m">
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'}" id="message-${m.id}">
					<td id="star" class=" ${m.starred?'starred':""}" messageId="${m.id}">
					 ${m.starred?'Remove Star':'Add Star'}
					</td>
					<td>
						<g:if test="${ownerInstance}">
							<g:link action="${messageSection}" params="[messageId: m.id, ownerId: ownerInstance.id]">
								${m.displaySrc}
							</g:link>
						</g:if>
						<g:else>
							<g:link action="${messageSection}" params="[messageId: m.id]">
								${m.displaySrc}
							</g:link>
						</g:else>
					</td>
					<td>
						<g:if test="${ownerInstance}">
							<g:link action="${messageSection}" params="[messageId: m.id, ownerId: ownerInstance.id]">
								${m.displayText}
							</g:link>
						</g:if>
						<g:else>
							<g:link action="${messageSection}" params="[messageId: m.id]">
							  ${m.displayText}
							</g:link>
						</g:else>
					</td>
					<td>
						<g:if test="${ownerInstance}">
							<g:link action="${messageSection}" params="[messageId: m.id, ownerId:ownerInstance.id]">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
						</g:if>
						<g:else>
							<g:link  action="${messageSection}" params="[messageId: m.id]">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
						</g:else>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table> 
</g:if>
<g:else>
	<div id="messages">
		You have no messages saved
	</div>
</g:else>
