/**
 * Non thread-safe mock implementation of Javascript's timer methods.
 */
timer = (function() {
	var
	clock,
	callbacks,
	tickSize,
	init = function(t) {
		clock = 0;
		callbacks = [];
		tickSize = t || 1;
	},
	tick = function(count) {
		if(count === undefined) {
			count = 1;
		}
		while(--count >= 0) {
			processTick();
		}
	},
	processTick = function() {
		var c, i;
		++clock;
		for(i=callbacks.length-1; i>=0; --i) {
			c = callbacks[i];
			if(c.next === clock) {
				c.func();
				if(c.repeat) {
					c.next = clock + c.interval;
				} else {
					callbacks.splice(i, 1);
				}
			}
		}
	},
	addCallback = function(func, interval, repeat) {
		if(typeof func === "string") {
			throw new Exception("You probably shouldn't be passing code to setTimeout/setInterval.");
		}
		interval = Math.max(1, interval / tickSize);
		callbacks.push({
			func:func,
			next:clock + interval,
			interval:interval,
			repeat:repeat });
	},
	setInterval = function(func, interval) {
		addCallback(func, interval, true);
	},
	setTimeout = function(func, timeout) {
		addCallback(func, timeout, false);
	},
	___end___;
	return {
		init:init,
		tick:tick,
		setInterval:setInterval,
		setTimeout:setTimeout
	};
}());

