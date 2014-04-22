package zan.game.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import zan.game.util.GameUtility;

/** Resource reader class */
public class ResourceReader {
	private static final String LOGNAME = "ResourceReader :: ";
	
	/** Resource files directory */
	private static final String RES_DIR = "res/";
	
	/** Resource data */
	private ResourceData resource;
	
	/** Constructor */
	public ResourceReader(String fnm) {
		resource = new ResourceData(GameUtility.getPrefix(fnm));
		
		try {
			ArrayList<ResourceData> parent = new ArrayList<ResourceData>();
			parent.add(resource);
			
			BufferedReader br = new BufferedReader(new FileReader(RES_DIR + fnm));
			String line;
			while((line = br.readLine()) != null) {
				if (line.length() == 0)	continue;
				String[] tkns = GameUtility.split(line);
				if (tkns[0].isEmpty() || tkns[0].startsWith("//")) continue;
				
				if (tkns.length == 1) {
					if (tkns[0].contentEquals("}")) {
						if (parent.size() > 1) {
							parent.remove(parent.size()-1);
							continue;
						}
					}
				} else if (tkns.length == 2) {
					if (tkns[1].contentEquals("{")) {
						ResourceData child = new ResourceData(tkns[0]);
						parent.get(parent.size()-1).addNode(child);
						parent.add(child);
						continue;
					}
				}
				if (line.contains("{") || line.contains("}")) {
					System.out.println(LOGNAME + "Invalid syntax in file " + RES_DIR + fnm + ":\n " + line);
				}
				
				if (tkns.length == 1) {
					parent.get(parent.size()-1).addValue(tkns[0]);
					continue;
				} else if (tkns.length > 1) {
					ResourceData data = new ResourceData(tkns[0]);
					for (int i=1;i<tkns.length;i++) data.addValue(tkns[i]);
					parent.get(parent.size()-1).addNode(data);
					continue;
				}
			}
			
			br.close();
		} catch (IOException e) {
			System.err.println(LOGNAME + "Error reading file " + RES_DIR + fnm + ":\n " + e);
		}
	}
	
	/** @return Resource data */
	public ResourceData getData() {
		return resource;
	}
	
}
