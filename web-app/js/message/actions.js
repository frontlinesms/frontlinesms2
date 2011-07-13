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

function checkAllMessages(){
	if($(':checkbox')[0].checked){
		$(':checkbox').each(function(){
			this.checked = true;
		});
		changeMessageDetailsPane($(':checkbox').size()-1);
	} else {
		$(':checkbox').each(function(){
			this.checked = false;
		});
		showDefaultMessageDetailsPane();
	}
	
}

var messageDetails;
function appendMessageDetails(){
	var count = countCheckedMessages();
	if(count>1){
		changeMessageDetailsPane(count);
	} else {
		showDefaultMessageDetailsPane();
	}
}

function countCheckedMessages(){
	var count = 0;
	$(':checkbox').each(function(){
			if(this.checked)
				count++;
		});
	//Check whether all messages are checked
	if(count == $(':checkbox').size()-1 && !$(':checkbox')[0].checked){
			$(':checkbox')[0].checked = true;
		} else if($(':checkbox')[0].checked){
			$(':checkbox')[0].checked = false;
			count--;
		}
	return count;
}

function changeMessageDetailsPane(count){
	if(messageDetails == null){
		messageDetails = $('#message-details').html();
	}
	$('#message-details').empty();
	$('#message-details').append("<p> "+count+" messages selected</p>");
}

function showDefaultMessageDetailsPane(){
	if(messageDetails == null){
		return;
	}
	$('#message-details').empty();
	$('#message-details').html(messageDetails);
}
