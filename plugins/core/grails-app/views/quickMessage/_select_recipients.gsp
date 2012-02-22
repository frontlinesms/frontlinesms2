<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<div id="manual-address">
		<label id="label" class="bold" for="address">Add phone number: </label>
		<g:textField id="address" name="address" onkeyup="validateAddressEntry();"/>
		<g:link url="#" class="btn add-address" onclick="addAddressHandler();">Add</g:link>
	</div>
	<div id="recipients-list">
		<ul id="groups">
			<g:each in="${groupList}" var="entry">
				<li class="group">
					<input type="checkbox" name="groups" value="${entry.key}" onclick='selectMembers("${entry.key}", ${entry.value as JSON})'>
					${entry.key}(${entry.value.size()}) <br/>
				</li>
			</g:each>
			<g:each in="${nonExistingRecipients}" var="address">
				<li>
					<input type="checkbox" name="addresses" value="${address}" checked>${address} <br/>
				</li>
			</g:each>
		</ul>
		<ul id="contacts">
			<g:each in="${contactList}" var="contact">
				<li class="contact">
					<input type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if>>
					${contact.name ?: contact.primaryMobile} <g:if test="${recipients.contains(contact.secondaryMobile) || recipients.contains(contact.email)}">(Primary)</g:if><br/>
				</li>
				<li class="contact">
					<g:if test="${recipients.contains(contact.secondaryMobile)}">
						<input type="checkbox" name="addresses" value="${contact.secondaryMobile}" checked>
						${contact.name ?: contact.secondaryMobile} (Secondary)<br/>
					</g:if>
				</li>
				<li class="contact">
					<g:if test="${recipients.contains(contact.email)}">
						<input type="checkbox" name="addresses" value="${contact.email}" checked>
						${contact.name ?: contact.email} (Email)<br/>
					</g:if>
				</li>
			</g:each>
		</ul>
	</div>
	<div id="recipients-selected"><span id="recipient-count">0</span> recipients selected</div>
</div>

<script>
	var groupAndMembers = {}
	function selectMembers(groupName, allContacts) {
		groupAndMembers[groupName] = allContacts
		$.each(allContacts, function(index, contact) {
			setValueForCheckBox(contact, isCheckboxSelected(groupName));
		});
		
		$.each(getSelectedGroupElements('groups'), function(index, groupInputElement) {
			if(groupInputElement.value != groupName) {
				$.each(groupAndMembers[groupInputElement.value], function(index, member) {
					setValueForCheckBox(member, true)
				});
			}
		});
		updateCount()
	}

	$(".contact input[type='checkbox']").live('click', function() {
		if (!($(this).is(":checked"))) {
			var contactNumber = this.value
			$.each(groupAndMembers, function(key, value) {
				var partOfGroup = jQuery.inArray(contactNumber, groupAndMembers[key]) > -1 ? true : false 
				if (partOfGroup)
					findInputWithValue(key).attr('checked', false);
			});
		}
		updateCount()
	});

	function setValueForCheckBox(value, checked) {
		var checkBox = $('#contacts input[value=' + "'" + value + "'" + ']');
		checkBox.attr('checked', checked);
		checkBox.change()
	}

	function updateCount() {
		var addressCount = getSelectedGroupElements("addresses").size();
		$.each(["#recipient-count", "#contacts-count"],
			function(index, id) {
				if($(id)) $(id).html(addressCount);
			}
		);
		
		var messageStats = $("#send-message-stats").text()
		noOfMessages = messageStats.substring(messageStats.indexOf("(")+1, messageStats.indexOf(" S"));
		noOfMessages = noOfMessages == 0 ? 1 : noOfMessages
		messageCount = addressCount * parseInt(noOfMessages)
		$("#messages-count").html(messageCount)
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
			$("#manual-address").append("<div id='address-error' class='error-message'>Phone number cannot contain non-numbers</div>");
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
				updateCount();
			}
			$('#address').val("")
			$("#address").removeClass('error');
			$("#manual-address").find('#address-error').remove();
			return true;
		}
		return false;
	}
</script>




