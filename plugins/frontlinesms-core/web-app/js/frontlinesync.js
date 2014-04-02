var frontlinesync =  (function() {
	var connetionRow, updateConfigSynced, beforeUpdate, afterUpdate, toggleOptions, syncConfigContainer = $('.sync-config-container'), syncConfigStatus = $('.sync-config-status');
	beforeUpdate = function() {
		showThinking();
	};

	afterUpdate = function() {
		hideThinking();
		syncConfigContainer.hide();
		syncConfigStatus.html(i18n('frontlinesync.sync.config.dirty.true'));
	};

	toggleOptions = function() {
		syncConfigContainer.toggle();
	};

	updateConfigSynced = function(data) {
		connectionRow = $('#connection-' + data.id);
		var configSyncedMessage = i18n('frontlinesync.sync.config.dirty.' + !data.configSynced);
		connectionRow.find(".sync-config-status").html(configSyncedMessage);
		connectionRow.find("#sendEnabled").attr("checked", data.sendEnabled);
		connectionRow.find("#receiveEnabled").attr("checked", data.receiveEnabled);
		connectionRow.find("#missedCallEnabled").attr("checked", data.missedCallEnabled);
	};
	return {
		beforeUpdate: beforeUpdate,
		afterUpdate: afterUpdate,
		toggleOptions: toggleOptions,
		updateConfigSynced: updateConfigSynced
	};
})();
