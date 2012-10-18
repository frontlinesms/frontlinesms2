Array.prototype.sum = function() {
	var i, sum;
	sum = 0;
	for (i=0; i<this.length; i++) {
		sum+=this[i];
	}
	return sum;
};

Array.prototype.max = function() {
	return Math.max.apply({}, this);
};

Array.prototype.min = function() {
	return Math.min.apply({}, this);
};

Array.prototype.pack = function() {
	var i, x = [],
		length = this[0].length;
	for (i=0; i<length; i++) {
		x[i] = 0;
		$.each(this, function(index, value) {
			x[i] += value[i];
		});
	}
	return x;
};

