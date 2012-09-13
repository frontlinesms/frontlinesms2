package net.frontlinesms2.systraymonitor.system;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

public abstract class ProcessExecutor {
	private PrintStream out = System.out;

	public abstract void doExecute(BufferedReader reader) throws IOException;

	public void execute(String... command) {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader bufferedReader = null;
		try {
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			is = p.getInputStream();
			isr = new InputStreamReader(is);
			bufferedReader = new BufferedReader(isr);
			doExecute(bufferedReader);
		} catch(Exception ex) {
			try { bufferedReader.close(); } catch(Exception _) { /* ignore */ }
			try { isr.close(); } catch(Exception _) { /* ignore */ }
			try { is.close(); } catch(Exception _) { /* ignore */ }
		}
	}

	public void echoOutput(BufferedReader reader) throws IOException {
		String line;
		while((line = reader.readLine()) != null) {
			out.println(line);
		}
	}
}

