<div id="multiple-message-details">
	<p>1 2 3 a lot</p>
	<div class="actions">
		<ol class="buttons">
			<g:if test="${messageSection != 'pending'}">
				<li class='static_btn'><a id='btn_reply_all'>Reply All</a></li>
			</g:if>
			<g:if test="${!params['archived'] && messageSection != 'poll'}">
				<li class='static_btn'><a id='btn_archive_all'>Archive All</a></li>
			</g:if>
			<li class="static_btn"><a id='btn_delete_all'>Delete All</a></li>
		</ol>
		<g:if test="${!params['archived']}">
			<g:render template="/message/action_list"/>
		</g:if>
	</div>
</div>