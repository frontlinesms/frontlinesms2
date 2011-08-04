<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>


<div id="groups">
	<g:each in="${groupList}" var="entry">
		<div>
						<input type="checkbox"   name="groups" value= "${entry.key}" onclick='selectMembers("${entry.key}", ${entry.value as JSON})'>
		${entry.key}(${entry.value.size()})
</input>
</div>
	</g:each>
</div>

<div id="contacts">
	<g:each in="${nonExistingRecipients}" var="address">
		<div>
			<input type="checkbox" name="addresses" value="${address}" checked>${address}</input>
		</div>
	</g:each>

	<g:each in="${contactList}" var="contact">
		<div>
		<input contacts="true" class= "" type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if>>
		${contact.name ?: contact.primaryMobile} <g:if test="${recipients.contains(contact.secondaryMobile) || recipients.contains(contact.email)}">(Primary)</g:if>
		</input>
  </div>
  <div>
		<g:if test="${recipients.contains(contact.secondaryMobile)}">
			<input type="checkbox" name="addresses" value="${contact.secondaryMobile}" checked>
			${contact.name ?: contact.secondaryMobile} (Secondary)
		</input>
		</g:if>
		</div>
  <div>
		<g:if test="${recipients.contains(contact.email)}">
			<input type="checkbox" name="addresses" value="${contact.email}" checked>
			${contact.name ?: contact.email} (Email)
		</input>
		</g:if>
		</div>
	</g:each>

(<span id="count">0</span> recipients selected)
</div>


<g:link url="#" class="back">Back</g:link>
<g:link url="#" class="next">Next</g:link>






<script>
	var groupAndMembers = {}
	function selectMembers(groupName, allContacts) {
		groupAndMembers[groupName] = allContacts
		$.each(allContacts, function(index, value) {
			setValueForCheckBox(groupName, value, isCheckboxSelected(groupName))
		});
		updateCount()
	}

	$("input[contacts='true']").live('click', function() {
		if (!($(this).is(":checked"))) {
			var contactNumber = this.value
			$.each(groupAndMembers, function(key, value) {
				if(jQuery.inArray(contactNumber,  groupAndMembers[key] > -1))
					findInputWithValue(key).attr('checked', false);
			});
		}
		updateCount()
	});

	function setValueForCheckBox(grpName, value, checked) {
		var checkBox = $('#contacts input[value=' + "'" + value + "'" + ']');
		checkBox.attr('checked', checked);
	}

	function updateCount() {
		$("#count").html(getSelectedGroupElements("addresses").size())
	}

</script>




