var contactsearch = {
	init: function(list) {
		$("#contactsearch").ajaxChosen({
			type: 'POST',
			url: '../search/contactSearch',
			dataType: 'json'
		}, function (data) 
		{
			var results = [];
			$.each(data, function (i, val) {
				var group = { // here's a group object:
					group: true,
					text: val.text, // label for the group
					items: [] // individual options within the group
				};
				$.each(val.items, function (i1, val1) {
					group.items.push({value: val1.value, text: val1.text});
				});
				results.push(group);
			});
			return results;
		});
	}
};

