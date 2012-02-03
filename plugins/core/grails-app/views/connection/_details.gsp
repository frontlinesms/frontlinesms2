<div id="tabs-2">
	<g:if test="${fconnectionInstance instanceof frontlinesms2.SmslibFconnection || action == 'save'}">
		<div id='smslib-form'>
			<h2>Phone/Modem</h2>
			<div class="input field">
				<label for="name">Name</label>
				<g:textField name="name" value="${fconnectionInstance?.name}" />
			</div>
		
			<div class="field">
				<label for="port">Port</label>
				<g:textField name="port" value="${fconnectionInstance?.port}" />
			</div>
		
			<div class="field">
				<label for="baud">Baud rate</label>
				<g:textField name="baud" value="${fconnectionInstance?.baud}" />
			</div>
		
			<div class="field">
				<label for="pin">PIN</label>
				<g:passwordField name="pin" value="${fconnectionInstance?.pin}" />
			</div>
		</div>
	</g:if>
	<g:if test="${fconnectionInstance instanceof frontlinesms2.EmailFconnection || action == 'save'}">
		<div id='email-form'>
			<h2>Email</h2>
			<div class="field">
				<label for="name">Name</label>
				<g:textField name="email-name" value="${fconnectionInstance?.name}" />
			</div>
		
			<div class="field">
				<label for="receiveProtocol">Protocol</label>
				<g:select from="${frontlinesms2.EmailReceiveProtocol.values()}"
						value="${fconnectionInstance?.receiveProtocol}"
						name="receiveProtocol"
						noSelection="${['null': '- Select -']}"/>
			</div>
		
			<div class="field">
				<label for="serverName">Server name</label>
				<g:textField name="serverName" value="${fconnectionInstance?.serverName}" />
			</div>
		
			<div class="field">
				<label for="serverPort">Server port</label>
				<g:textField name="serverPort" value="${fconnectionInstance?.serverPort}" />
			</div>
		
			<div class="field">
				<label for="username">Username</label>
				<g:textField name="username" value="${fconnectionInstance?.username}" />
			</div>
		
			<div class="field">
				<label for="password">Password</label>
				<g:textField name="password" value="${fconnectionInstance?.password}" />
			</div>
		</div>
	</g:if>
</div>
