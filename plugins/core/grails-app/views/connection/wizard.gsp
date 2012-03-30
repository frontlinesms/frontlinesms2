<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.Fconnection" %>
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
<g:javascript>
	url_root = "${request.contextPath}/";
	
	<g:if test="${fconnectionInstance}">
		var fconnectionType = "${fconnectionInstance.getClass().simpleName.toLowerCase() - 'fconnection'}";
	</g:if>
	<g:else>
		var fconnectionType = $("#type-list").find("input[checked=checked]").val();		
	</g:else>
	
	function setConnectionType(connectionType) {
		fconnectionType = connectionType;
		$("#type-list input[checked=checked]").attr('checked', '');
		$("#type-list ." + connectionType).attr('checked', 'checked');
		<g:each in="${Fconnection.implementations*.shortName}">
			$("#${it}-form").css('display', 'none');
		</g:each>
		$("#" + connectionType + "-form").css('display', 'inline');
	}
</g:javascript>
<g:javascript src="application.js"/>
<g:javascript src="mediumPopup.js"/>
<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div>Please fill in all required fields</div>
	<ol>
		<g:if test="${!fconnectionInstance}">
			<li><a href="#tabs-1">Choose type</a></li>
		</g:if>
		<li><a href="#tabs-2">Enter details</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ol>
	<g:form name="connectionForm" action="${action}" id='${fconnectionInstance?.id}'>
		<g:render template="type"/>
		<g:render template="details"/>
		<g:render template="confirm"/>
	</g:form>
</div>
<g:javascript>
function isFieldSet(fieldName) {
	var val = getFieldVal(fieldName);
	return val!=null && val.length>0;
}

function getFieldVal(fieldName) {
	var val = $('#' + fconnectionType + fieldName).val();
	return val;
}

function setConfirmVal(fieldName, val) {
	$("#" + fconnectionType + "-confirm #confirm-" + fieldName).text(val);
}

function setConfirmation(fieldName) {
	setConfirmVal(fieldName, getFieldVal(fieldName));
}

function setSecretConfirmation(fieldName) {
	val = isFieldSet(fieldName)? '****': 'None';
	setConfirmVal(fieldName, val);
}

function initializePopup() {
	<g:if test="${fconnectionInstance}">
		setConnectionType(fconnectionType);
	</g:if>
	<g:else>
		setConnectionType('smslib');
	</g:else>
	
	$("#tabs").bind("tabsshow", function(event, ui) {
		updateConfirmationMessage();
	});

	$("#tabs-2").contentWidget({
		validate: function() {
			var checked = $("#type-list").find("input[checked=checked]").val();
			var nullConstraintValidator = {};
			<g:each in="${Fconnection.implementations}">
				nullConstraintValidator.${it.shortName} = {
					isValid: function() {
						var fields = [];
						<g:each in="${it.configFields}" var="f">
							<g:if test='${!it.constraints."$f".nullable}'>
								fields.push("${f}");
							</g:if>
						</g:each>
						var valid = false;
						$.each (fields, function(index, value){
							valid = isFieldSet(value)
							console.log("is valid " + value + "? " + valid)
							return valid
						});
						return valid
						}
					}
				</g:each>
				return nullConstraintValidator[checked].isValid()	
			}
	});
}

function getFconnectionTypeAsHumanReadable() {
	<g:each in="${Fconnection.implementations}">
		if(fconnectionType == '${it.shortName}') return "<g:message code="${it.simpleName.toLowerCase()}.label"/>"
	</g:each>
}

function updateConfirmationMessage() {
	setConfirmVal('type', getFconnectionTypeAsHumanReadable());
	setConfirmation('name');
	
	var fconnection = {}
	//hide all the others
	<g:set var="connectionTypeList" value="${Fconnection.implementations*.shortName}"/>
	<g:each in="${Fconnection.implementations}">
		fconnection.${it.shortName} = {
			show: function() {
				<g:each in="${connectionTypeList - it.shortName}">
					$("#${it}-confirm").hide();
				</g:each>
				<g:each in="${it.configFields}" var="f">
					<g:if test="${f == it.passwords[0]}">
						setSecretConfirmation('${f}');
					</g:if>
					<g:else>
						setConfirmation('${f}');
					</g:else>
					
				</g:each>
				$("#${it.shortName}-confirm").show();
			}
		}
	</g:each>
	
	fconnection[fconnectionType].show()
	
}
</g:javascript>
