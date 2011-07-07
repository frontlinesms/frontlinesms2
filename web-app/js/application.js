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

function launchWizard(id, html) {
	$("<div id=" + id + "><div>").html(html).appendTo(document.body);
	$("#" + id).dialog(
		{
			modal: true,
			title: "Manage Subscription",
			width: 600,
			close: function() { $(this).remove(); }
		}
	);
	$("#tabs").tabs();
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

