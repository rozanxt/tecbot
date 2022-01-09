package zan.game.sprite;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import zan.game.resource.ResourceData;

/** Texture manager class */
public class TextureManager {
	private static final String LOGNAME = "TextureManager :: ";

	/** Texture resources directory */
	private static final String IMG_DIR = "res/img/";

	/** Texture storage */
	private static HashMap<String, Integer> textureStore;

	/** Graphics configuration */
	private static GraphicsConfiguration gc;
	private static final int BYTES_PER_PIXEL = 4;

	/** Initialize texture manager */
	public static void init() {
		textureStore = new HashMap<String, Integer>();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
	}

	/** Texture manager cleanup */
	public static void destroy() {
		for (Map.Entry<String, Integer> entry : textureStore.entrySet()) {
			glDeleteTextures(entry.getValue());
		}
		textureStore.clear();
	}

	/** Load textures from a resource data */
	public static void loadTextureData(ResourceData texData) {
		if (texData.isEmpty()) {
			System.err.println(LOGNAME + "Error loading texture data:\n no data found");
			return;
		}

		for (int i=0;i<texData.getNumNodes();i++) {
			ResourceData node = texData.getNode(i);
			if (node.getNumValues() == 1) {
				storeTexture(node.getName(), node.getValue("file"));
			}
		}
	}

	/** Store texture */
	private static boolean storeTexture(String name, String fnm) {
		if (name == null || fnm == null) {
			System.err.println(LOGNAME + "Error loading texture:\n no data found");
			return false;
		}
		if (textureStore.containsKey(name)) {
			System.err.println(LOGNAME + "Error loading texture:\n " + name + " is already used");
			return false;
		}

		int textureID = loadTexture(fnm);
		if (textureID != 0) {
			textureStore.put(name, textureID);
			return true;
		} else return false;
	}

	/** Load texture */
	private static int loadTexture(String fnm) {
		try {
			BufferedImage im = ImageIO.read(new File(IMG_DIR + fnm));

			int transparency = im.getColorModel().getTransparency();
			BufferedImage bi =  gc.createCompatibleImage(im.getWidth(), im.getHeight(), transparency);
			Graphics2D g2d = bi.createGraphics();
			g2d.setComposite(AlphaComposite.Src);

			g2d.drawImage(im, 0, 0, null);
			g2d.dispose();
			return genTexture(bi);
		} catch(IOException e) {
			System.err.println(LOGNAME + "Error loading texture for " + IMG_DIR + fnm + ":\n " + e);
			return 0;
		}
	}

	/** Generate texture ID */
	private static int genTexture(BufferedImage image) {
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
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); //GL_LINEAR / GL_NEAREST
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		// Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		return textureID;
	}

	/** Unload textures from a resource data */
	public static void unloadTextureData(ResourceData texData) {
		if (texData.isEmpty()) {
			System.err.println(LOGNAME + "Error unloading textures:\n no data found");
			return;
		}

		for (int i=0;i<texData.getNumNodes();i++) {
			ResourceData node = texData.getNode(i);
			if (!node.isEmpty()) {
				unloadTexture(node.getName());
			}
		}
	}

	/** Unload texture */
	private static boolean unloadTexture(String name) {
		if (isTextureLoaded(name)) {
			glDeleteTextures(getTextureID(name));
			textureStore.remove(name);
			return true;
		} else return false;
	}

	/** @return Texture ID */
	public static int getTextureID(String name) {
		if (!isTextureLoaded(name)) {
			System.out.println(LOGNAME + "No texture stored under " + name);
			return 0;
		}
		return textureStore.get(name);
	}

	/** @return true if texture is loaded */
	public static boolean isTextureLoaded(String name) {
		if (textureStore.get(name) == null || textureStore.get(name) == 0) return false;
		return true;
	}

}
