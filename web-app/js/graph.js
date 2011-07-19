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

Raphael.fn.plotStackedBarGraph = function(holder, data, xdata, caption, opts) {
	var padding = opts.padding || {
		left: 40,
		top: 20,
		bottom: 10,
		right: 50
	};
	var self = this,
		width = opts.width || $("#" + holder).width(),
		height = opts.height || $("#" + holder).height(),
		chartHeight = height - padding.bottom,
		axisPosition = chartHeight - padding.top,
		chartWidth = width - padding.left - padding.right,
		textStyle = opts.textStyle || {
		"font-size": 12
	};
	var chart = self.g.barchart(padding.left, 0, chartWidth, chartHeight, data, {
		stacked: true,
		gutter: opts.gutter || "20%",
		colors: opts.colors || ["#D4D5D6", "#949494"]
	});
	var packedData = data.pack();

	var yaxis = self.g.axis(padding.left, axisPosition, axisPosition - padding.top, 0, packedData.max(), packedData.length, 1, null, "-", 0);
	var i = xdata.length;
	chart.eachColumn(function() {
		var x1 = this.bars[0].x || padding.left,
			y1 = this.bars[0].y || axisPosition,
			h1 = this.bars[0].h || 0,
			val1 = this.bars[0].value || 0;
		self.text(x1 + 5, y1 + h1 + 15, xdata[--i]).rotate(45).attr(textStyle);
	});

	chart.hoverColumn(function() {
		var y = [],
			res = [],
			msg = "";
		for (var i = this.bars.length; i--;) {
			y.push(this.bars[i].y);
			res.push(caption[i] + " " + this.bars[i].value || "0");
		}
		this.flag = self.g.popup(this.bars[0].x, Math.min.apply(Math, y), res.join("\r\n"), 3).insertBefore(this);
	},
	function() {
		this.flag.animate({
			opacity: 0
		},
		1000, ">", function() {
			this.remove();
		});
	});

	var xaxis = self.g.axis(padding.left, axisPosition, chartWidth - 15, null, null, null, 2, " ", "_", 0);
	return chart;
};

Raphael.fn.plotBarGraph = function(holder, data, xdata, opts) {
	opts = opts || {};
	var self = this,
	width = opts.width || $("#" + holder).width(),
	height = opts.height || $("#" + holder).height();
	var padding = {
		left: 40,
		top: 20,
		bottom: 10,
		right: 50
	} || opts.padding;
	var chartHeight = height - padding.bottom;
	var axisPosition = chartHeight - padding.top;
	var chartWidth = width - padding.left - padding.right;

	var max_val = 0;
	var textStyle = opts.textStyle || {
		"font-size": 12
	};
	var chart = self.g.barchart(padding.left, 0, chartWidth, chartHeight, data, {
		stacked: true,
		gutter: "20%",
		colors: opts.colors || ["#D4D5D6", "#949494"]
	});
	var chart = self.g.barchart(padding.left, 0, chartWidth, chartHeight, [data], {
		gutter: opts.gutter || "10%"
	});
	var colors = opts.colors || ["#949494", "#F2202B", "#40B857"];
	var sum = data.sum() || 1;
	var i = 0,
		len = xdata.length;
	chart.each(function() { //TODO: pull this out to be generic
		var x = this.bar.x || padding.left,
			y = this.bar.y || axisPosition,
			h = this.bar.h || 0,
			val = this.bar.value || 0;
		this.bar.attr({
			fill: colors[i++]
		});
		max_val = h > max_val ? h : max_val;
		self.text(x, y + h / 2, val).attr(textStyle);
		self.text(x, y - 10, Math.round(val * 100 / sum) + "%").attr(textStyle);
		self.text(x, y + h + 15, xdata[--len]).attr(textStyle);
	});
	max_val = max_val > 0 ? max_val : chartHeight;
	var yaxis = self.g.axis(padding.left, axisPosition, axisPosition - padding.top, 0, data.max(), data.length, 1, null, "-", 0);
	var xaxis = self.g.axis(padding.left, axisPosition, chartWidth - 15, null, null, null, 2, " ", "_", 0);
	return chart;
};