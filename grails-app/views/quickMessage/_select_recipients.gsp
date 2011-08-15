<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<div id="groups">
		<g:each in="${groupList}" var="entry">
			<div>
				<input type="checkbox" name="groups" value="${entry.key}" onclick='selectMembers("${entry.key}", ${entry.value as JSON})'>
				${entry.key}(${entry.value.size()}) <br/>
			</div>
		</g:each>
	</div>

	<div id="contacts">
		<g:each in="${nonExistingRecipients}" var="address">
			<div>
				<input type="checkbox" name="addresses" value="${address}" checked>${address} <br/>
			</div>
		</g:each>

		<g:each in="${contactList}" var="contact">
			<div>
				<input contacts="true" class="" type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if>>
				${contact.name ?: contact.primaryMobile} <g:if test="${recipients.contains(contact.secondaryMobile) || recipients.contains(contact.email)}">(Primary)</g:if><br/>
			</div>
			<div>
				<g:if test="${recipients.contains(contact.secondaryMobile)}">
					<input type="checkbox" name="addresses" value="${contact.secondaryMobile}" checked>
					${contact.name ?: contact.secondaryMobile} (Secondary)<br/>
				</g:if>
			</div>
			<div>
				<g:if test="${recipients.contains(contact.email)}">
					<input type="checkbox" name="addresses" value="${contact.email}" checked>
					${contact.name ?: contact.email} (Email)<br/>
				</g:if>
			</div>
		</g:each>
		<span id="recipient-count">0</span> recipients selected
	</div>
</div>

<script>
	var groupAndMembers = {}
	function selectMembers(groupName, allContacts) {
		groupAndMembers[groupName] = allContacts
		$.each(allContacts, function(index, value) {
			setValueForCheckBox(groupName, value, isCheckboxSelected(groupName))
		});
		updateCount()
	}

	$("input[contacts='true']").live('change', function() {
		if (!($(this).is(":checked"))) {
			var contactNumber = this.value
			$.each(groupAndMembers, function(key, value) {
				if (jQuery.inArray(contactNumber, groupAndMembers[key] > -1))
					findInputWithValue(key).attr('checked', false);
			});
		}
		updateCount()
	});

	function setValueForCheckBox(grpName, value, checked) {
		var checkBox = $('#contacts input[value=' + "'" + value + "'" + ']');
		checkBox.attr('checked', checked);
		checkBox.change()
	}

	function updateCount() {
		var count = getSelectedGroupElements("addresses").size();
		$("#recipient-count").html(count)
		var contactsCount = $("#contacts-count");
		contactsCount && contactsCount.html(count)
	}

	$('.add-address').live('click', function() {
		var address = $('#address').val();
		$("#contacts").prepend("<div><input type='checkbox' checked='true' name='addresses' value=" + address + ">" + address + "</input></div>")
		updateCount()
	});

</script>




