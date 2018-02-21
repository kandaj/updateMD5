package main.com.ebi.ega;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author imssbora
 */
public class ReadFile {
	public static ArrayList<String> getFileStableIDs() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("file")));

			String str=null;
			while((str = br.readLine()) != null){
				list.add(str);
			}
			br.close();

		} catch (FileNotFoundException e) {
			System.err.println("File not found");
		} catch (IOException e) {
			System.err.println("Unable to read the file.");
		}
		return ( list );
	}
}


