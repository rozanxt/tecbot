package zan.game.sprite;

import static org.lwjgl.opengl.GL11.*;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import zan.game.resource.ResourceData;

/** Text manager class */
public class TextManager {
	private static final String LOGNAME = "TextManager :: ";
	
	/** Font resources directory */
	private static final String FNT_DIR = "res/fnt/";
	
	/** Font bitmap storage */
	private static HashMap<String, BufferedImage[][]> fontMap;
	
	/** Font metrics information storage */
	private static HashMap<String, FontInfo> fontStore;
	
	/** Graphics configuration */
	private static GraphicsConfiguration gc;
	private static final int BYTES_PER_PIXEL = 4;
	
	/** Font bitmap characters */
	private static final String chars =
			" !\"#$%&\'()*+,-./" +
			"0123456789:;<=>?" +
			"@ABCDEFGHIJKLMNO" +
			"PQRSTUVWXYZ[\\]^_" +
			"`abcdefghijklmno" +
			"pqrstuvwxyz{|}~";
	
	/** Font info class */
	private static class FontInfo {
		public String name, file;
		public int x_res, y_res;
		public int x_tiles, y_tiles;
		public int def_w, def_h;
		public int x_os;//, y_os;
		
		public FontInfo() {
			name = "nofont"; file = "nofont.png";
			x_res = 128; y_res = 128;
			x_tiles = 16; y_tiles = 16;
			def_w = 8; def_h = 8;
			x_os = 1;// y_os = 2;
		}
	}
	
	/** Character info class */
	private static class CharInfo {
		public int xid, yid;
		public int width;
		
		public CharInfo(int sx, int sy, int sw) {
			xid = sx; yid = sy;
			width = sw;
		}
	}
	
	/** Initialize text manager */
	public static void init() {
		fontMap = new HashMap<String, BufferedImage[][]>();
		fontStore = new HashMap<String, FontInfo>();
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
	}
	
	/** Text manager cleanup */
	public static void destroy() {
		fontMap.clear();
		fontStore.clear();
	}
	
	/**
	 * Render text with shadow
	 * @param text
	 * @param font
	 * @param sx
	 * @param sy
	 * @param size
	 * @param align
	 * @param shadowoffset
	 * @param shadowalpha
	 */
	public static void renderTextShadow(String text, String font, float sx, float sy, float size, int align, float shadowoffset, float shadowalpha) {
		glPushAttrib(GL_CURRENT_BIT);
		glColor4f(0f, 0f, 0f, shadowalpha);
		renderText(text, font, sx+(size*shadowoffset), sy-(size*shadowoffset), size, align);
		glPopAttrib();
		renderText(text, font, sx, sy, size, align);
	}
	
	/**
	 * Render boxed text
	 * @param text
	 * @param font
	 * @param sx
	 * @param sy
	 * @param sw
	 * @param sh
	 */
	public static void renderTextBox(String text, String font, float sx, float sy, float sw, float sh) {
		int tid = loadText(text, font);
		
		glBindTexture(GL_TEXTURE_2D, tid);
		
		glBegin(GL_QUADS);
			glTexCoord2f(0f, 1f); glVertex2f(sx, sy);
			glTexCoord2f(0f, 0f); glVertex2f(sx, sy + sh);
			glTexCoord2f(1f, 0f); glVertex2f(sx + sw, sy + sh);
			glTexCoord2f(1f, 1f); glVertex2f(sx + sw, sy);
		glEnd();
		
		glDeleteTextures(tid);
	}
	
	/**
	 * Render text
	 * @param text
	 * @param font
	 * @param sx
	 * @param sy
	 * @param size
	 * @param align
	 */
	public static void renderText(String text, String font, float sx, float sy, float size, int align) {
		int tid = loadText(text, font);
		
		float ow = (float) glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
		float oh = (float) glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
		float or = (float) (ow / oh);
		
		float th = size;
		float tw = th * or;
		
		glBindTexture(GL_TEXTURE_2D, tid);
		
		if (align == 0) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx, sy);
				glTexCoord2f(0f, 0f); glVertex2f(sx, sy + th);
				glTexCoord2f(1f, 0f); glVertex2f(sx + tw, sy + th);
				glTexCoord2f(1f, 1f); glVertex2f(sx + tw, sy);
			glEnd();
		} else if (align == 1) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx - (tw/2f), sy);
				glTexCoord2f(0f, 0f); glVertex2f(sx - (tw/2f), sy + th);
				glTexCoord2f(1f, 0f); glVertex2f(sx + (tw/2f), sy + th);
				glTexCoord2f(1f, 1f); glVertex2f(sx + (tw/2f), sy);
			glEnd();
		} else if (align == 2) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx - tw, sy);
				glTexCoord2f(0f, 0f); glVertex2f(sx - tw, sy + th);
				glTexCoord2f(1f, 0f); glVertex2f(sx, sy + th);
				glTexCoord2f(1f, 1f); glVertex2f(sx, sy);
			glEnd();
		} else if (align == 3) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx, sy - (th/2f));
				glTexCoord2f(0f, 0f); glVertex2f(sx, sy + (th/2f));
				glTexCoord2f(1f, 0f); glVertex2f(sx + tw, sy + (th/2f));
				glTexCoord2f(1f, 1f); glVertex2f(sx + tw, sy - (th/2f));
			glEnd();
		} else if (align == 5) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx - tw, sy - (th/2f));
				glTexCoord2f(0f, 0f); glVertex2f(sx - tw, sy + (th/2f));
				glTexCoord2f(1f, 0f); glVertex2f(sx, sy + (th/2f));
				glTexCoord2f(1f, 1f); glVertex2f(sx, sy - (th/2f));
			glEnd();
		} else if (align == 6) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx, sy - th);
				glTexCoord2f(0f, 0f); glVertex2f(sx, sy);
				glTexCoord2f(1f, 0f); glVertex2f(sx + tw, sy);
				glTexCoord2f(1f, 1f); glVertex2f(sx + tw, sy - th);
			glEnd();
		} else if (align == 7) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx - (tw/2f), sy - th);
				glTexCoord2f(0f, 0f); glVertex2f(sx - (tw/2f), sy);
				glTexCoord2f(1f, 0f); glVertex2f(sx + (tw/2f), sy);
				glTexCoord2f(1f, 1f); glVertex2f(sx + (tw/2f), sy - th);
			glEnd();
		} else if (align == 8) {
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx - tw, sy - th);
				glTexCoord2f(0f, 0f); glVertex2f(sx - tw, sy);
				glTexCoord2f(1f, 0f); glVertex2f(sx, sy);
				glTexCoord2f(1f, 1f); glVertex2f(sx, sy - th);
			glEnd();
		} else {	// (align == 4)
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f); glVertex2f(sx - (tw/2f), sy - (th/2f));
				glTexCoord2f(0f, 0f); glVertex2f(sx - (tw/2f), sy + (th/2f));
				glTexCoord2f(1f, 0f); glVertex2f(sx + (tw/2f), sy + (th/2f));
				glTexCoord2f(1f, 1f); glVertex2f(sx + (tw/2f), sy - (th/2f));
			glEnd();
		}
		
		glDeleteTextures(tid);
	}
	
	/** Load text */
	private static int loadText(String text, String font) {
		BufferedImage[][] fontMatrix = getFont(font);
		FontInfo fontInfo = getFontInfo(font);
		
		if (fontMatrix == null || fontInfo == null) {
			System.err.println(LOGNAME + "Error retrieving font " + font + ":\n no data found");
			return 0;
		}
		
		int textureID = genText(createText(text, fontMatrix, fontInfo));
		if (textureID != 0) return textureID;
		return 0;
	}
	
	/** Create text */
	private static BufferedImage createText(String text, BufferedImage[][] font, FontInfo info) {
		if (text == null) {
			System.out.println(LOGNAME + "No text defined");
			return null;
		}
		if (font == null) {
			System.err.println(LOGNAME + "Error retrieving font for text \"" + text + "\":\n no font found");
			return null;
		}
		
		int xos = info.x_os;
		int ymargin = 1;
		int dw = xos;
		for (int i=0;i<text.length();i++) {
			int ch = chars.indexOf(text.charAt(i));
			if (ch < 0) continue;
			
			int fi = (int)(ch / 16);
			int fj = (int)(ch % 16);
			if (font[fi][fj] == null) continue;
			
			dw += font[fi][fj].getWidth() + xos;
		}
		
		BufferedImage genText = new BufferedImage(dw, info.def_h + ymargin*2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = genText.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		
		dw = xos;
		for (int i=0;i<text.length();i++) {
			int ch = chars.indexOf(text.charAt(i));
			if (ch < 0) continue;
			
			int fi = (int)(ch / 16);
			int fj = (int)(ch % 16);
			if (font[fi][fj] == null) continue;
			
			g2d.drawImage(font[fi][fj], dw, ymargin, font[fi][fj].getWidth(), info.def_h, null);
			
			dw += font[fi][fj].getWidth() + xos;
		}
		
		g2d.dispose();
		
		return genText;
	}
	
	/** Generate text texture */
	private static int genText(BufferedImage image) {
		if (image == null) return 0;
		
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
		
		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));	// Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));	// Green component
				buffer.put((byte) (pixel & 0xFF));			// Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));	// Alpha component. Only for RGBA
			}
		}
		
		buffer.flip();
		
		int textureID = glGenTextures();			// Generate texture ID
		glBindTexture(GL_TEXTURE_2D, textureID);	// Bind texture ID
		
		// Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		// Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		
		return textureID;
	}
	
	/** Load font from a resource data */
	public static void loadFontFile(ResourceData fontData) {
		if (fontData.isEmpty()) {
			System.err.println(LOGNAME + "Error loading font data:\n no data found");
			return;
		}
		
		FontInfo fontInfo = new FontInfo();
		ArrayList<CharInfo> charInfo = new ArrayList<CharInfo>();
		
		fontInfo.name = fontData.getName();
		for (int i=0;i<fontData.getNumNodes();i++) {
			ResourceData node = fontData.getNode(i);
			if (node.getName().contentEquals("bitmap") && node.getNumValues() == 5) {
				fontInfo.file = node.getValue("file");
				fontInfo.x_tiles = node.getIntegerValue("x");
				fontInfo.y_tiles = node.getIntegerValue("y");
				fontInfo.x_res = node.getIntegerValue("w");
				fontInfo.y_res = node.getIntegerValue("h");
			} else if (node.getName().contentEquals("offset") && node.getNumValues() == 2) {
				fontInfo.x_os = node.getIntegerValue("x");
				//fontInfo.y_os = node.getIntegerValue("y");
			} else if (node.getName().contentEquals("default") && node.getNumValues() == 2) {
				fontInfo.def_w = node.getIntegerValue("w");
				fontInfo.def_h = node.getIntegerValue("h");
			} else if (node.getName().contentEquals("char")) {
				int xi = node.getIntegerValue("x");
				int yi = node.getIntegerValue("y");
				int wi = node.getIntegerValue("w");
				charInfo.add(new CharInfo(xi, yi, wi));
			}
		}
		
		loadFont(fontInfo, charInfo);
	}
	
	/** Store font */
	private static boolean loadFont(FontInfo fin, ArrayList<CharInfo> cin) {
		String name = fin.name;
		
		if (fontMap.containsKey(name)) {
			System.err.println(LOGNAME + "Error loading font:\n " + name + " is already used");
			return false;
		}
		
		BufferedImage[][] fm = createFont(fin, cin);
		if (fm != null) {
			fontMap.put(name, fm);
			fontStore.put(name, fin);
			return true;
		} else return false;
	}
	
	/** Create font */
	private static BufferedImage[][] createFont(FontInfo fin, ArrayList<CharInfo> cin) {
		BufferedImage fontBmp = loadBitmapFont(fin.file);
		if (fontBmp == null) {
			System.err.println(LOGNAME + "Error retrieving font for " + FNT_DIR + fin.file + ":\n no font bitmap found");
			return null;
		}
		
		int charWidth = fin.def_w;
		int charHeight = fin.def_h;
		int transparency = fontBmp.getColorModel().getTransparency();
		
		int tile_w = (int)(fin.x_res / fin.x_tiles);
		int tile_h = (int)(fin.y_res / fin.y_tiles);
		
		BufferedImage[][] fontMatrix = new BufferedImage[6][16];
		Graphics2D g2d;
		
		for (int i=0;i<6;i++) {
			for (int j=0;j<16;j++) {
				charWidth = fin.def_w;
				charHeight = fin.def_h;
				for (int k=0;k<cin.size();k++) {
					if (i == cin.get(k).yid && j == cin.get(k).xid) {
						charWidth = cin.get(k).width;
						break;
					}
				}
				
				fontMatrix[i][j] =  gc.createCompatibleImage(charWidth, charHeight, transparency);
				
				g2d = fontMatrix[i][j].createGraphics();
				
				g2d.drawImage(fontBmp, 0, 0, charWidth, charHeight, tile_w*j, tile_h*i, (tile_w*j)+charWidth, (tile_h*i)+charHeight, null);
				g2d.dispose();
			}
		} 
		return fontMatrix;
	}
	
	/** Load bitmap font */
	private static BufferedImage loadBitmapFont(String fnm) {
		try {
			BufferedImage im = ImageIO.read(new File(FNT_DIR + fnm));
			
			int transparency = im.getColorModel().getTransparency();
			BufferedImage bi =  gc.createCompatibleImage(im.getWidth(), im.getHeight(), transparency);
			Graphics2D g2d = bi.createGraphics();
			g2d.setComposite(AlphaComposite.Src);
			
			g2d.drawImage(im, 0, 0, null);
			g2d.dispose();
			return bi;
		} catch(IOException e) {
			System.err.println(LOGNAME + "Error loading font for " + FNT_DIR + fnm + ":\n " + e); 
			return null;
		}
	}
	
	/** Unload font from a resource data */
	public static void unloadFontData(ResourceData fontData) {
		if (fontData.isEmpty()) {
			System.err.println(LOGNAME + "Error loading font data:\n no data found");
			return;
		}
		unloadFont(fontData.getName());
	}
	
	/** Unload font */
	private static boolean unloadFont(String name) {
		if (isFontLoaded(name)) {
			fontMap.remove(name);
			fontStore.remove(name);
			return true;
		} else return false;
	}
	
	/** @return true if font is loaded */
	private static boolean isFontLoaded(String name) {
		BufferedImage[][] fm = (BufferedImage[][]) fontMap.get(name);
		FontInfo im = (FontInfo) fontStore.get(name);
		if (fm == null || im == null) return false;
		return true;
	}
	
	/** @return font bitmap */
	private static BufferedImage[][] getFont(String name) {
		BufferedImage[][] fm = (BufferedImage[][]) fontMap.get(name);
		if (fm == null) {
			System.out.println(LOGNAME + "No font bitmap stored under " + name);  
			return null;
		}
		
		return (BufferedImage[][]) fontMap.get(name);
	}
	
	/** @return font metrics information */
	private static FontInfo getFontInfo(String name) {
		FontInfo fm = (FontInfo) fontStore.get(name);
		if (fm == null) {
			System.out.println(LOGNAME + "No font information stored under " + name);  
			return null;
		}
		
		return (FontInfo) fontStore.get(name);
	}
	
	/** @return true if char is available in the font bitmap */
	public static boolean isCharAvailable(char ch) {
		if (chars.indexOf(ch) == -1) return false;
		return true;
	}
	
	/** @return width of a generated text */
	public static float getTextWidth(String text, String font, float size) {
		if (loadText(text, font) == 0) return 0f;
		
		float ow = (float) glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
		float oh = (float) glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
		float or = (float) (ow / oh);
		
		float th = size;
		float tw = th * or;
		
		return tw;
	}
	
}
