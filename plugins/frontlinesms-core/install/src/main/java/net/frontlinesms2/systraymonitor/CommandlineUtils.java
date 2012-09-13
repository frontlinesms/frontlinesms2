package net.frontlinesms2.systraymonitor;

class CommandlineUtils {
	static boolean isFlagSet(String[] args, String flag) {
		flag = toVerbose(flag);
		for(String arg : args) {
			if(arg.equals(flag)) {
				return true;
			}
		}
		return false;
	}

	static boolean isValSet(String[] args, String key) {
		key = toVerbose(key);
		return getValIndex(args, key) != -1;
	}

	static String getVal(String[] args, String key) {
		key = toVerbose(key);
		String val = args[getValIndex(args, key)];
		return val.substring(val.indexOf("=") + 1);
	}

//> PRIVATE HELPER METHODS
	private static int getValIndex(String[] args, String key) {
		key = key + "=";
		for(int i=0; i<args.length; ++i) if(args[i].startsWith(key)) return i;
		return -1;
	}

	private static String toVerbose(String s) { return "--" + s; }
}

