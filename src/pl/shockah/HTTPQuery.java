package pl.shockah;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class HTTPQuery {
	public enum Method{GET,POST,HEAD}
	
	protected URL url = null;
	protected Method method;
	protected HttpURLConnection c;
	
	public HTTPQuery(String adr) throws MalformedURLException {this(adr,Method.GET);}
	public HTTPQuery(String adr, Method method) throws MalformedURLException {
		url = new URL(adr);
		this.method = method;
	}
	
	public void connect(boolean input, boolean output) throws IOException {connect(false,input,output);}
	public void connect(boolean cache, boolean input, boolean output) throws IOException {
		c = (HttpURLConnection)url.openConnection();
		c.setRequestMethod(method.name());
		c.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		
		c.setUseCaches(cache);
		c.setDoInput(input);
		c.setDoOutput(output);
	}
	public HttpURLConnection getConnection() {
		return c;
	}
	public void close() {
		if (c != null) c.disconnect();
		c = null;
	}
	
	public void setUserAgentFirefox() {setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0) Gecko/20100101 Firefox/10.0");}
	public void setUserAgent(String s) {
		System.setProperty("http.agent","");
		c.setRequestProperty("User-Agent",s);
	}
	
	public void write(String s) throws IOException {
		write(s.getBytes());
	}
	
	public void write(byte[] bytes) throws IOException {
		c.setRequestProperty("Content-Length",Integer.toString(bytes.length));
		OutputStream os = c.getOutputStream();
		os.write(bytes);
	}
	
	public ArrayList<String> read() throws UnsupportedEncodingException,IOException {
		ArrayList<String> ret = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(),"UTF-8"));
		String line;
		while ((line = br.readLine()) != null) ret.add(line);
		br.close();
		
		return ret;
	}
	public String readWhole() throws UnsupportedEncodingException,IOException {
		ArrayList<String> lines = read();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			if (i != 0) sb.append('\n');
			sb.append(lines.get(i));
		}
		return sb.toString();
	}
	
	public static String parseArgs(Map<String,String> args) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> pair : args.entrySet()) {
			if (sb.length() != 0) sb.append('&');
			try {
				sb.append(URLEncoder.encode(pair.getKey()+'='+pair.getValue(),"UTF-8"));
			} catch (Exception e) {e.printStackTrace();}
		}
		return sb.toString();
	}
	public static String parseArgs(String... args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i += 2) {
			if (sb.length() != 0) sb.append('&');
			try {
				sb.append(URLEncoder.encode(args[i],"UTF-8")+'='+URLEncoder.encode(args[i+1],"UTF-8"));
			} catch (Exception e) {e.printStackTrace();}
		}
		return sb.toString();
	}
}