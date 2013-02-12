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
		console.log("timer.tick() :: clock=" + clock + "; count=" + count);
		while(--count >= 0) {
			processTick();
		}
	},
	processTick = function() {
		var c, i;
		console.log("timer.processTick() :: callbacks.length=" + callbacks.length);
		++clock;
		for(i=callbacks.length-1; i>=0; --i) {
			c = callbacks[i];
			console.log("___________________________________________");
			console.log("timer.processTick() :: c.repeat=" + c.repeat);
			console.log("timer.processTick() :: c.interval=" + c.interval);
			console.log("timer.processTick() :: c.next=" + c.next);
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
		console.log("timer.addCallback() :: ENTRY");
		if(typeof func === "string") {
			throw new Exception("You probably shouldn't be passing code to setTimeout/setInterval.");
		}
		interval = Math.max(1, interval / tickSize);
		callbacks.push({
			func:func,
			next:clock + interval,
			interval:interval,
			repeat:repeat });
		console.log("timer.addCallback() :: EXIT");
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

