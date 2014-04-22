package zan.game.util;

import java.util.ArrayList;
import java.util.Random;

/** Game utilities class */
public class GameUtility {
	private static final String LOGNAME = "GameUtility :: ";
	
	/** Random generator */
	private static Random rnd;
	
	/** Initialize game utilities */
	public static void init() {
		rnd = new Random();
	}
	
	/** @return Random generator */
	public static Random getRnd() {
		return rnd;
	}
	
	/** @return true if string is parseable to integer */
	public static boolean isIntegerString(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/** @return Prefix of filename */
	public static String getPrefix(String fnm) {
		int dot;
		if ((dot = fnm.lastIndexOf(".")) == -1) {
			System.out.println(LOGNAME + "No prefix found for " + fnm);
			return fnm;
		} else return fnm.substring(0, dot);
	}
	
	/** @return Tokens from string */
	public static String[] split(String str) {
		String[] a = str.split(" |\t|\n");
		ArrayList<String> b = new ArrayList<String>();
		for (int i=0;i<a.length;i++) if (!a[i].isEmpty()) b.add(a[i]);
		
		if (b.isEmpty()) {
			String[] tkns = new String[1];
			tkns[0] = "";
			return tkns;
		}
		
		String[] tkns = new String[b.size()];
		for (int i=0;i<tkns.length;i++) tkns[i] = b.get(i);
		return tkns;
	}
	
}
