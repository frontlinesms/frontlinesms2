<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<div id="manual-address">
		<label id="manual-label" class="bold" for="address"><g:message code="quickmessage.phonenumber.label"/> </label>
		<g:textField id="address" name="address" onkeyup="validateAddressEntry();"/>
		<g:link url="#" class="btn add-address" onclick="addAddressHandler();"><g:message code="quickmessage.phonenumber.add"/></g:link>
	</div>
	<div id="recipients-list">
		<ul>
			<g:each in="${groupList}" var="entry">
				<li class="group">
					<g:checkBox name="groups" value="${entry.key}" onclick="selectMembers('${entry.key}', '${entry.value.name}', ${entry.value.addresses as JSON})" checked="${false}"/>
					${entry.value.name}(${entry.value.addresses.size()})
				</li>
			</g:each>
			<g:each in="${nonExistingRecipients}" var="address">
				<li>
					<g:checkBox name="addresses" value="${address}" checked="${true}"/>
					${address}
				</li>
			</g:each>
		</ul>
		<ul id="contacts">
			<g:each in="${contactList}" var="contact">
				<li class="contact">
					<g:checkBox name="addresses" value="${contact.mobile}" checked="${recipients.contains(contact.mobile)}"/>
					${contact.name ?: contact.mobile}
				</li>
				<li class="contact">
					<g:if test="${recipients.contains(contact.email)}">
						<input type="checkbox" name="addresses" value="${contact.email}" checked>
						${contact.name ?: contact.email} (<g:message code="contact.email.label"/>)
					</g:if>
				</li>
			</g:each>
		</ul>
	</div>
	<div id="recipients-selected"><span id="recipient-count">0</span> <g:message code="quickmessage.selected.recipients"/></div>
</div>

<r:script disposition="head">
	var groupAndMembers = {}
	function selectMembers(groupIdString, groupName, allContacts) {
		groupAndMembers[groupIdString] = allContacts
		$.each(allContacts, function(index, contact) {
			setValueForCheckBox(contact, isCheckboxSelected(groupIdString));
		});
		
		$.each(getSelectedGroupElements('groups'), function(index, groupInputElement) {
			if(groupInputElement.value != groupName) {
				$.each(groupAndMembers[groupInputElement.value], function(index, member) {
					setValueForCheckBox(member, true);
				});
			}
		});
		updateMessageCount();
	}

	$(".contact input[type='checkbox']").bind('click', function() {
		if (!($(this).is(":checked"))) {
			var contactNumber = this.value
			$.each(groupAndMembers, function(key, value) {
				var partOfGroup = jQuery.inArray(contactNumber, groupAndMembers[key]) > -1 ? true : false 
				if (partOfGroup)
					findInputWithValue(key).attr('checked', false);
			});
		}
		updateMessageCount();
	});

	function setValueForCheckBox(value, checked) {
		var checkBox = $('#contacts input[value=' + "'" + value + "'" + ']');
		checkBox.attr('checked', checked);
		checkBox.change();
	}

	function updateMessageCount() {
		var addressCount = getSelectedGroupElements("addresses").size();
		$.each(["#recipient-count", "#contacts-count"],
			function(index, id) {
				if($(id)) $(id).html(addressCount);
			}
		);
		
		var messageStats = $("#send-message-stats").text();
		noOfMessages = messageStats.substring(messageStats.indexOf("(")+1, messageStats.indexOf(" S"));
		noOfMessages = noOfMessages == 0 ? 1 : noOfMessages;
		messageCount = addressCount * parseInt(noOfMessages);
		$("#messages-count").html(messageCount);
	}

	function validateAddressEntry() {
		var address = $('#address').val();
		var containsLetters = jQuery.grep(address, function(a) {
			return a.match(/[^\+?\d+]/) != null;
		}).join('');
		$("#address").removeClass('error');
		$("#manual-address").find('#address-error').remove();
		if(containsLetters != '' && containsLetters != null) {
			$("#address").addClass('error');
			$("#manual-address").append("<div id='address-error' class='error-message'><g:message code='fmessage.number.error'/></div>");
			return false;
		} else {
			return true;
		}
	}

	 function addAddressHandler() {
		var address = $('#address').val();
		if(address == '') {
			return true;
		} else {
			var sanitizedAddress = address.replace(/\D/g, '');
			if(address[0] == '+') sanitizedAddress = '+' + sanitizedAddress;
			var checkbox = $("li.manual").find(":checkbox[value=" + sanitizedAddress + "]").val();
			if(checkbox !== address) {
				$("#contacts").prepend("<li class='manual contact'><input contacts='true' type='checkbox' checked='true' name='addresses' value=" + sanitizedAddress + ">" + sanitizedAddress + "</input></li>")
				updateMessageCount();
			}
			$('#address').val("");
			$("#address").removeClass('error');
			$("#manual-address").find('#address-error').remove();
			return true;
		}
		return false;
	}
</r:script>
