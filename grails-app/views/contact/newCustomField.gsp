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
		<a class="create" href="#" onClick="createCustomField_submit()">Create</a>
		<a class="cancel" href="#" onClick="createCustomField_cancel()">Cancel</a>
	  </div>
	</g:form>
</div>
