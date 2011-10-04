<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<div>
		<label class="header" for="address">Add phone number</label>
		<g:textField id="address" name="address"/>
		<g:link url="#" class="add-address" onclick="addAddressHandler();">Add</g:link> <!-- FIXME this should be a button, surely? -->
	</div>
	
	<div id="recipients-list">
		<ol>
			<div id="groups">
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
			</div>
			<div id="contacts">
				<g:each in="${contactList}" var="contact">
					<li class="contact">
						<input contacts="true" class="" type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if>>
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
			</div>
		</ol>
	</div>
	<span id="recipient-count">0</span> recipients selected
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

	$("input[contacts='true']").live('click', function() {
		if (!($(this).is(":checked"))) {
			var contactNumber = this.value
			$.each(groupAndMembers, function(key, value) {
				if (jQuery.inArray(contactNumber, groupAndMembers[key] > -1))
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
		var count = getSelectedGroupElements("addresses").size();
		$.each(["#recipient-count", "#contacts-count", "#messages-count"],
			function(index, id) {
				if($(id)) $(id).html(count);
			}
		);
	}

	 function addAddressHandler() {
		var address = $('#address').val();
		$("#contacts").prepend("<div class='manual'><input type='checkbox' checked='true' name='addresses' value=" + address + ">" + address + "</input></div>")
		updateCount();
	}
</script>




