<div id="message-details">
	<g:hiddenField name="checkedMessageList" id="checkedMessageList" value=","/>
	<div id="single-message">
		<div id='message-info'>
			<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}"/>
			<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
			<h2 id="contact-name">${messageInstance.displayName}
				<g:if test="${!messageInstance.contactExists}">
					<g:link class="button" id="add-contact" controller="contact" action="createContact" params='[primaryMobile: "${messageInstance.src ?: messageInstance.dst}"]'><img src='${resource(dir: 'images/icons', file: 'messagehistory.gif')}'/></g:link>
				</g:if>
			</h2>
			<p id="message-date"><g:formatDate date="${messageInstance.dateCreated}"/></p>
			<p id="message-body">${messageInstance.text}</p>
		</div>
		<div class="actions buttons">
			<ol class="buttons">
				<g:if test="${buttons != null}">
					${buttons}
				</g:if>
				<g:else>
					<g:render template="message_actions"></g:render>
				</g:else>
			</ol>
			<g:if test="${!params['archived']}">
				<g:render template="/message/action_list"/>
			</g:if>
			<div id="poll-actions">
				<g:if test="${messageInstance && messageSection == 'poll'}">
					<g:render template="categorize_response"/>
				</g:if>
			</div>
		</div>
	</div>
	<div id="multiple-messages" class='hide'>
		<div id='message-info'>
			<h2>${checkedMessageCount} messages selected</h2>
			<div class="actions">
				<ol class="buttons">
					<div id='other_btns'>
						<g:if test="${messageSection != 'pending'}">
							<li class='static_btn'>
								<a id='btn_reply_all'>Reply All</a>
							</li>
						</g:if>
						<g:if test="${!params['archived'] && messageSection != 'poll'}">
							<li class='static_btn'><a id='btn_archive_all'>Archive All</a></li>
						</g:if>
						<li class="static_btn"><a id='btn_delete_all'>Delete All</a></li>
					</div>
				</ol>
				<g:if test="${!params['archived']}">
					<g:render template="/message/action_list"/>
				</g:if>
			</div>
		</div>
	</div>
</div>
