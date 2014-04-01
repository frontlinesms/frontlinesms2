var frontlinesync =  (function() {
	var before, after, toggleOptions, syncConfigContainer = $('.sync-config-container'), syncConfigStatus = $('.sync-config-status');
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
	}
	return {
		beforeUpdate: beforeUpdate,
		afterUpdate: afterUpdate,
		toggleOptions: toggleOptions
	};
})();
