package packy;

import java.io.BufferedReader;
import java.io.File;
 
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		List<String> lines = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();
		Path outfile = Paths.get("out.txt");
		Path errfile = Paths.get("error.txt");
		File file = new File("/home/catalin/eclipse-workspace/checker_v3/tests/in/input1_bonus2");
		CommandFactory fact = new CommandFactory();
		Director rad =  Director.getRoot();
		BufferedReader bf = new BufferedReader(new FileReader(file));	
		String line;
		Integer contor = 0;
		while((line = bf.readLine() )!= null)
		{
			 lines.add((++contor).toString());//adauga nr comenzii
			 errors.add(contor.toString());
			Command com = fact.createCommand(line, rad, lines, errors);//e creata comanda
			if(com != null)
				com.execute();//executarea comenzii
		}
		Files.write(outfile, lines, Charset.forName("UTF-8"));
		Files.write(errfile, errors, Charset.forName("UTF-8"));
		bf.close();
	}
}
