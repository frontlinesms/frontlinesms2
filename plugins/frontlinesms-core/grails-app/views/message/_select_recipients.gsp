<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<g:hiddenField name="mobileNumbers" value=""/>
	<div id="manual-address">
		<label id="manual-label" class="bold" for="address"><g:message code="quickmessage.phonenumber.label" /></label>
		<g:textField id="address" name="address" onkeyup="validateAddressEntry();"/>
		<g:link url="#" class="btn add-address" onclick="addAddressHandler();" >
			<g:message code="quickmessage.phonenumber.add"/>
		</g:link>
	</div>
	<div id="recipients-list">
		<ul id="groups">
			<g:each in="${groupList}" var="entry" status='i'>
				<li class="group">
					<g:checkBox id="groups-${i}" name="groups" value="${entry.key}" onclick="selectMembers(this,'${entry.key}', '${entry.value.name}', ${entry.value.addresses as JSON})" checked="${false}" groupMembers="${entry.value.addresses as JSON}"/>
					<label for="groups-${i}">${entry.value.name} (${entry.value.addresses.size()})</label>
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
			<g:each in="${contactList}" var="contact" status="i">
				<li class="contact" f-name="${contact.name}" f-number="${contact.mobile}">
					<g:checkBox id="addresses-${i}" name="addresses" value="${contact.mobile}" onclick="setContact(this,'${contact.mobile}')" checked="${recipients?.contains(contact.mobile)}"/>
					<label for="addresses-${i}">${contact.name ?: contact.mobile}</label>
					<span class="matched-search-result" id="matched-search-result-${i}">
						<g:message code="contact.mobile.label"/> : ${contact.mobile}
					</span>
				</li>
				<g:if test="${recipients?.contains(contact.email)}">
					<li class="contact">
						<g:checkBox name="addresses" value="${contact.email}" checked="true"/>
						${contact.name ?: contact.email} (<g:message code="contact.email.label"/>)
					</li>
				</g:if>
			</g:each>
		</ul>
	</div>
	<div class="controls">
		<div id="search">
			<g:textField id="searchbox" class='search' name="address" onkeyup="searchForContacts();"/>
		</div>
		<div id="recipients-selected"><span id="recipient-count">0</span> <g:message code="quickmessage.selected.recipients"/></div>
	</div>
</div>

<r:script disposition="head">
	var groupAndMembers = {}
	function selectMembers(element, groupIdString, groupName, allContacts) {
		var contactMobileNumbers = {};
		$.each($('#mobileNumbers').val().split(','), function(index, value) { 
  			if(!(value in contactMobileNumbers) && (value != '')){
  				contactMobileNumbers[value] = true;
  			}
		});

		if($(element).attr('checked')){
			console.log('adding');
			$.each(allContacts, function(index, value) { 
	  			if(!(value in contactMobileNumbers)){
	  				contactMobileNumbers[value] = true;
	  			}
			});
		} else {
			console.log('Unchecked');
			$.each(allContacts, function(index, value) {
	  			if(value in contactMobileNumbers){
	  				if(! inCheckedGroup(value)){
	  					delete contactMobileNumbers[value];
	  				}
	  			}
			});
		}

		var mobileNumbers = $.map(contactMobileNumbers, function(index, value){
			return value;
		});

		$('#mobileNumbers').val(($.makeArray(mobileNumbers).join(',')));
		console.log($('#mobileNumbers').val());

		updateRecipientCount();
	}

	function inCheckedGroup(value){
		var checkedGroups = $('li.group input:checked');
		var t = false;
	  	$.each(checkedGroups, function(index, element){
	  		if($.inArray(value, $.makeArray($(element).attr('groupMembers')))){
	  			t =  true;
	  		}
	  	});
	  	return t;
	}

	function setContact(element,contactNumber) {
		var contactMobileNumbers = {};
		$.each($('#mobileNumbers').val().split(','), function(index, value) { 
  			if(!(value in contactMobileNumbers) && (value != '')){
  				contactMobileNumbers[value] = true;
  			}
		});

		if($(element).attr('checked')){
			console.log('adding');
  			if(!(contactNumber in contactMobileNumbers)){
  				contactMobileNumbers[contactNumber] = true;
  			}
		} else {
			console.log('Unchecked');
  			if(contactNumber in contactMobileNumbers){
  				if(! inCheckedGroup(contactNumber)){
  					delete contactMobileNumbers[contactNumber];
  				}
  			}
		}

		var mobileNumbers = $.map(contactMobileNumbers, function(index, value){
			return value;
		});

		$('#mobileNumbers').val(($.makeArray(mobileNumbers).join(',')));
		console.log($('#mobileNumbers').val());

		updateRecipientCount();
	}

	function setValueForCheckBox(value, checked) {
		var checkBox = $('#contacts input[value=' + "'" + value + "'" + ']');
		checkBox.attr('checked', checked);
		checkBox.change();
	}

	function updateRecipientCount() {
		var mobileNumbersArray;
		if($('#mobileNumbers').val() != ""){
			mobileNumbersArray = $('#mobileNumbers').val().split(',');
		}
		var contactCount = mobileNumbersArray? mobileNumbersArray.length:0 ;
		$("#recipient-count").html(contactCount);
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
				$("#contacts").prepend("<li class='manual contact' f-name='' f-number='" + sanitizedAddress + "'><input contacts='true' type='checkbox' onclick='setContact(this," + sanitizedAddress + ")' checked='true' name='addresses' value='" + sanitizedAddress + "'>" + sanitizedAddress + "</input></li>")
				$("li.manual.contact[f-number='"+sanitizedAddress+"'] input").trigger('click');
				$("li.manual.contact[f-number='"+sanitizedAddress+"'] input").attr('checked','checked');
				updateRecipientCount();
			}
			$('#address').val("");
			$("#address").removeClass('error');
			$("#manual-address").find('#address-error').remove();
			return true;
		}
		return false;
	}

	function searchForContacts() {
		var search = $('#searchbox').val().toLowerCase();
		if (search == "" )
		{
			$('li.contact').show();
			$('ul#groups').show();
			$('.matched-search-result').hide();
		}
		else
		{
			$('ul#groups').hide();
			$('.ui-tabs-panel #recipients-list ul#contacts li.contact').each(function () {
				if($(this).attr('f-name').toLowerCase().indexOf(search.toLowerCase()) == -1 
					&& $(this).attr('f-number').toLowerCase().indexOf(search.toLowerCase()) == -1)
				{
					$(this).hide();
					$(this).find('.matched-search-result').hide();
				}
				else
				{
					$(this).show();
					$(this).find('.matched-search-result').show();
				}
			});
		}
	}
</r:script>
