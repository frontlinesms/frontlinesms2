<ul id="group-recipients" class="recipient-list">
	<g:each in="${groupList}" status="i" var="g">
		<li>${g.name}</li>
	</g:each>
</ul>
<ul id="contact-recipients" class="recipient-list">
	<g:each in="${contactList}" status="i" var="c">
		<li>${c}</li>
	</g:each>
</ul>
<ul id="address-recipients" class="recipient-list">
	<g:each in="${addressList}" status="i" var="a">
		<li>${a}</li>
	</g:each>
</ul>
