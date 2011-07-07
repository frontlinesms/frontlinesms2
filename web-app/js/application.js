var Ajax;
if (Ajax && (Ajax != null)) {
	Ajax.Responders.register({
	  onCreate: function() {
        if($('spinner') && Ajax.activeRequestCount>0)
          Effect.Appear('spinner',{duration:0.5,queue:'end'});
	  },
	  onComplete: function() {
        if($('spinner') && Ajax.activeRequestCount==0)
          Effect.Fade('spinner',{duration:0.5,queue:'end'});
	  }
	});
}

function launchWizard(title, html) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			title: title,
			width: 600,
			close: function() { $(this).remove(); }
		}
	);
	$("#tabs").tabs();
}

function isEmpty(selector) {
	return !$(selector).val();
}

function moveToTabBy(index) {
	var tabWidget = $('#tabs').tabs();
	var selected = tabWidget.tabs('option', 'selected')
	tabWidget.tabs('select', selected + index);
	return false;
}

$('.next').live('click', function() {
	return moveToTabBy(1);
});

$('.back').live('click', function() {
	return moveToTabBy(-1);
});

