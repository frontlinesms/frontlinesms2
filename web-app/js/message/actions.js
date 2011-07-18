function setStarStatus(object,data){
	if($("#"+object).hasClass("starred")) {
		$("#"+object).removeClass("starred");
	}
	
	$("#"+object).addClass(data);
	if(data != '') {
		$("#"+object).empty().append("Remove Star");
	} else {
		$("#"+object).empty().append("Add Star");
	}	
}

var messageDetails;
var messageIds = "";

function checkAllMessages(){
	if($(':checkbox')[0].checked){
		$(':checkbox').each(function(index){
			this.checked = true;
			if(index > 0){
				$(this).parent().parent().addClass('checked');
				addMessageIdToList(this.value);
			}
		});
		
		changeMessageCount($(':checkbox').size()-1);
	} else {
		$(':checkbox').each(function(index, element){
			this.checked = false;
			if(index > 0)			
				$(this).parent().parent().removeClass('checked');
		});
		emptyMessageList();
		showMessageDetails();
	}
	
}

function updateMessageDetails(id){
	var count = countCheckedMessages();

	if(count == 1 && $("#message-"+id).hasClass('checked') == $("#message-"+id).hasClass('selected')) {
		 //load the checked message
		 alert("Section:"+window.location.pathname+"/"+$('tr :checkbox[checked="true"]').val());
	}
	if(count > 1){
		changeMessageCount(count);
	} else {
		showMessageDetails();
	}
}

function countCheckedMessages(){
	var count = 0;
	$(':checkbox').each(function(index){
			if(this.checked){
				count++;
				addMessageIdToList(this.value);
			} else{
				removeMessageIdFromList(index>0 ? this.value: '');
			}
		});
	
	return validateCheckedMessageCount(count);
}

function validateCheckedMessageCount(count) {
	//Check whether all messages are checked
	if(count == $(':checkbox').size()-1 && !$(':checkbox')[0].checked){
			$(':checkbox')[0].checked = true;
		} else if($(':checkbox')[0].checked){
			$(':checkbox')[0].checked = false;
			count--;
		}
		return count;
}


function changeMessageCount(count){
	if(messageDetails == null){
		messageDetails = $('#message-details').html();
	}
	$('#message-details').empty();
	$('#message-details').append("<p> "+count+" messages selected</p>");
	setMessageActions();
}

function setMessageActions() {
	$('#message-details').append("<div class='buttons'></div>");
	$('#message-details div.buttons').load("/frontlinesms2/message/_multiple_message_buttons.gsp", {'checkedMessageIds': $('input:hidden[name=checkedMessageList]').val(), 'messageSection' : $('input:hidden[name=messageSection]').val()});
}

function showMessageDetails(){
	if(messageDetails == null){
		return;
	}
	$('#message-details').empty();
	$('#message-details').html(messageDetails);
}

function highlightRow(id){
	if($("#message-"+id).hasClass('checked')){
		$("#message-"+id).removeClass('checked')
	} else {
		$("#message-"+id).addClass('checked')
	}
}

function removeMessageIdFromList(id) {
	var f = $('input:hidden[name=checkedMessageList]');
	var oldList = f.val().replace(0 +',', ',');
	if(oldList.indexOf(id + ',') != -1) {
		var newList = oldList.replace(id +',', ',');
			f.val(newList);
	}
}

function addMessageIdToList(id) {
	var f = $('input:hidden[name=checkedMessageList]');
	var oldList = f.val();
	
	if(oldList.indexOf(id + ',') == -1) {
		var newList;
		newList = oldList + id + ',';
		f.val(newList);
	}
}

function emptyMessageList() {
	var f = $('input:hidden[name=checkedMessageList]');
	f.val("");
}
