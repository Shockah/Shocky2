package pl.shockah;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FileLine {
	public static ArrayList<String> read(InputStream is) throws UnsupportedEncodingException,IOException {
		ArrayList<String> ret = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String line; while ((line = br.readLine()) != null) ret.add(line);
		br.close();
		
		return ret;
	}
	public static ArrayList<String> read(File file) throws UnsupportedEncodingException,IOException {
		if (!file.exists()) return new ArrayList<String>();
		return read(new FileInputStream(file));
	}
	public static String readString(InputStream is) throws UnsupportedEncodingException,IOException {
		String ret = "";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String line; boolean first = true; while ((line = br.readLine()) != null) {
			if (first) first = false; else ret += "\n";
			ret += line;
		}
		br.close();
		
		return ret;
	}
	public static String readString(File file) throws UnsupportedEncodingException,IOException {
		try {
			return readString(new FileInputStream(file));
		} catch (FileNotFoundException e) {return "";}
	}
	
	public static void write(File file, ArrayList<String> lines) throws IOException {
		if (file.exists()) file.delete();
		file.createNewFile();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
		for (int i = 0; i < lines.size(); i++) {
			if (i != 0) bw.write('\n');
			bw.write(lines.get(i));
		}
		bw.close();
	}
}