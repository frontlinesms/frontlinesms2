<div id="tabs-1">
	<p>Contacts can be added and removed from groups automatically when FrontlineSMS receives a message that includes a special keyword</p>
	<div>
		<g:select name="id" from="${groups.collect{it.name}}" keys="${groups.collect{it.id}}" noSelection="['':'Select group...']"/>

		<div>
			<h2>Join Keyword</h2>
			<g:checkBox name="keyword" value="subscriptionKey" id="join_group_checkbox" checked='false'/> Join the group using a keyword
			<g:textField name="subscriptionKey" class="check-bound-text-area" checkbox_id="join_group_checkbox"/>
		</div>                                               

		<div>
			<h2>Leave Keyword</h2>
			<g:checkBox name="keyword" value="unsubscriptionKey" id="leave_group_checkbox" checked='false'/> Leave the group using a keyword
			<g:textField name="unsubscriptionKey" class="check-bound-text-area" checkbox_id="leave_group_checkbox"/>
		</div>
	</div>
</div>      
