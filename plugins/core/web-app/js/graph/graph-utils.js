Array.prototype.sum = function() {
	var sum = 0;
	for (var i = 0; i < this.length; sum += this[i++]) {}
	return sum;
};
Array.prototype.max = function() {
	return Math.max.apply({},
	this);
};
Array.prototype.min = function() {
	return Math.min.apply({},
	this);
};

Array.prototype.pack = function() {
	var x = [],
		length = this[0].length;
	for (var i = 0; i < length; i++) {
		x[i] = 0;
		$.each(this, function(index, value) {
			x[i] += value[i];
		});
	}
	return x;
};

