var frontlinesync =  (function() {
	var connetionRow, updateConfigSynced, beforeUpdate, afterUpdate, toggleOptions, updateCheckFrequencyLabel, bindEventListeners;
	beforeUpdate = function() {
		showThinking();
	};

	afterUpdate = function(connectionId) {
		var syncConfigStatus = $(getConnectionRow(connectionId) + ' .sync-config-status')
		hideThinking();
		toggleOptions(connectionId);
		syncConfigStatus.html(i18n('frontlinesync.sync.config.dirty.true'));
	};

	toggleOptions = function(connectionId) {
		var syncConfigContainer = $(getConnectionRow(connectionId) + ' .sync-config-container');
		var expandIcon = $(getConnectionRow(connectionId) +  " .sync-config-status-container .expand");
		var signRightIcon = "fa-chevron-circle-right";
		var signDownIcon = "fa-chevron-circle-down";
		syncConfigContainer.toggle();
		if(expandIcon.hasClass(signRightIcon)) {
			expandIcon.removeClass(signRightIcon);
			expandIcon.addClass(signDownIcon);
		} else {
			expandIcon.addClass(signRightIcon);
			expandIcon.removeClass(signDownIcon);
		}
	};

	getConnectionRow = function(connectionId) {
		return ("#connection-" + connectionId);
	}

	updateConfigSynced = function(data) {
		connectionRow = $(getConnectionRow(data.id)); 
		var configSyncedMessage = i18n('frontlinesync.sync.config.dirty.' + !data.configSynced);
		connectionRow.find(".sync-config-status").html(configSyncedMessage);
		connectionRow.find("#sendEnabled").attr("checked", data.sendEnabled);
		connectionRow.find("#receiveEnabled").attr("checked", data.receiveEnabled);
		connectionRow.find("#missedCallEnabled").attr("checked", data.missedCallEnabled);
	};

	updateCheckFrequencyLabel = function(connectionId) {
		var row = $(getConnectionRow(connectionId)),
		indexValue = row.find('input[name=syncFrequency]').val();
		row.find('em.syncFrequencyValue').html(frontlineSyncCheckSettingOptions[indexValue].i18n);
	};

	updateAllFrequencyLabels = function() {
		$('input[name=syncFrequency]').trigger('slider:changed');
	}

	bindEventListeners = function() {
		$('input[name=syncFrequency]').bind('slider:changed', function() {
			updateCheckFrequencyLabel($(this).closest('tr').attr('id').split('-')[1]);
		});
	};

	bindEventListeners();
	return {
		beforeUpdate: beforeUpdate,
		afterUpdate: afterUpdate,
		toggleOptions: toggleOptions,
		updateConfigSynced: updateConfigSynced,
		updateAllFrequencyLabels: updateAllFrequencyLabels
	};
})();
