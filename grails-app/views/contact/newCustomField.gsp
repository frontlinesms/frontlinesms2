<div id="custom-field-popup">
  <g:form name="custom-field-details" id="custom-field-details">
	  <div class="dialog">
		  <table>
			  <tbody>
				  <tr class="prop">
					  <td valign="top" class="name">
						  <label for="name">Name</label>
					  </td>
					  <td valign="top">
						  <g:textField id="custom-field-name" name="custom-field-name" value="" />
					  </td>
				  </tr>
				  <tr class="prop">
					  <td valign="top" class="value">
						  <label for="value">Value</label>
					  </td>
					  <td valign="top">
						  <g:textField id="custom-field-value" name="custom-field-value" value="" />
					  </td>
				  </tr>
			  </tbody>
		  </table>
	  </div>
	  <div class="buttons">
		  <g:submitButton name="save-custom-field" value="Create" />
		  <g:link id="cancel-create-field" class="cancel">Cancel</g:link>
	  </div>
	</g:form>
  	<script>
		$('#custom-field-details').submit(function() {
		  var name = $('#custom-field-name').val();
		  var value = $('#custom-field-value').val();
		  if(!name.length || !value.length) {
			  if(!$('#invalid').length) {
				  $('#custom-field-popup').prepend("<p id='invalid'>invalid details</p>");
			  }
			  return false;
		  }
		  alert("We think we checked");
		  addCustomField(name, value);

		  alert("We think we added a new field");
		  $("#custom-field-popup").dialog('close');
		  return false;
		});
		
		$('#cancel-create-field').click(function() {
		  $("#custom-field-popup").dialog('close');
		  return false;
		});
	</script>
</div>
