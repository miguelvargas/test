package com.mvwsolutions.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author smineyev
 *
 */
public class Utils {

	private Utils() {
		// instance creation prohibited
	}
	
    private static AtomicInteger baseCounter = new AtomicInteger((int) System.currentTimeMillis());
    
    public static int getUniqueInt() {
        return baseCounter.getAndIncrement();
    }

	public static String errorToString(Throwable e) {
        if (e == null) {
            return  null;
        }
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static long roundToDays(long timeInMs) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMs);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTimeInMillis();
	}

	@SuppressWarnings("unchecked")
	public static Set asSet(Object... objs) {
		Set res = new HashSet();
		for (Object obj : objs) {
			res.add(obj);
		}
		return res;
	}

	/**
	 * Copies content of given input stream into given output stream
	 * 
	 * @param in
	 * @param out
	 * @return number of bytes copied
	 * @throws IOException
	 */
	public static int inToOut(InputStream in, OutputStream out)
			throws IOException {
		int byteCopied = 0;
		byte[] buffer = new byte[4096];
		for (int k = in.read(buffer); k != -1; k = in.read(buffer)) {
			out.write(buffer, 0, k);
			byteCopied += k;
		}
		return byteCopied;
	}

	/**
	 * Copies content of given input stream into given output streams
	 * 
	 * @param in
	 * @param outs
	 * @return number of bytes copied
	 * @throws IOException
	 */
	public static int inToOut(InputStream in,
			Collection<? extends OutputStream> outs) throws IOException {
		int byteCopied = 0;
		byte[] buffer = new byte[4096];
		for (int k = in.read(buffer); k != -1; k = in.read(buffer)) {
			for (OutputStream out : outs) {
				out.write(buffer, 0, k);
			}
			byteCopied += k;
		}
		return byteCopied;
	}

	public static byte[] inToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		inToOut(in, byteOut);
		return byteOut.toByteArray();
	}

	public static String inToString(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[4096];
		try {
			for (int k = in.read(buffer); k != -1; k = in.read(buffer)) {
				sb.append(new String(buffer, 0, k, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}


	public static ArrayList<String> tokenize(String srcStr, String delim) {
		ArrayList<String> res = new ArrayList<String>();
		if (srcStr == null) {
			return res;
		}
		StringTokenizer st = new StringTokenizer(srcStr, delim);
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			res.add(t.trim());
		}
		return res;
	}

    public static String stackTraceToString(StackTraceElement[] elems) {
        return stackTraceToString(elems, "");
    }
    
    public static String stackTraceToString(StackTraceElement[] elems, String indent) {
        if (elems == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<elems.length; i++) {
            sb.append(indent);
            sb.append(elems[i].toString());
            if (i < elems.length - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
    
	public static int getUnusedPort() {
		int port = 1025;
		ServerSocket ss = null;
		while (port <= Short.MAX_VALUE * 2) {
			try {
				ss = new ServerSocket();
				ss.bind(new InetSocketAddress(port));
				break;
			} catch (IOException e) {
				port++;
				ss = null;
			}
		}
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return port;
		} else {
			throw new RuntimeException("Unable to find unused ports");
		}
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Returns the size in bytes of the given file or directory.
	 * 
	 * @param file
	 *            a File object representing a file or directory.
	 * @return the size of the given file or directory as a long value. Returns
	 *         -1 if an I/O error occurs.
	 */
	public static long getFileOrDirectorySize(File file) {
		long size = 0;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					long tmpSize = getFileOrDirectorySize(files[i]);
					if (tmpSize != -1) {
						size += tmpSize;
					}
				}
				return size;
			} else {
				return -1;
			}
		} else {
			return file.length();
		}
	}

    public static String composeString(Set<String> strs) {
        return composeString(strs, ",");
    }
    
    public static String composeString(Set<String> strs, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> i = strs.iterator(); i.hasNext();) {
            String str = i.next();
            sb.append(str);
            if (i.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

	public static interface FileVisitor {
		public void visit(File file);
	}

	public static void traverseFileTree(File dir, FileVisitor fileVisitor) {
		fileVisitor.visit(dir);
		File[] listFiles = dir.listFiles();
		if (listFiles != null) {
			for (File file : listFiles) {
				if (file.isDirectory()) {
					traverseFileTree(file, fileVisitor);
				} else {
					fileVisitor.visit(file);
				}
			}
		}
	}
	
	public static BufferedImage resizeCropImage(BufferedImage sourceImage, int destWidth, int destHeight) {
		
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        
        double xScale = ((double) destWidth) / (double) sourceWidth;
        double yScale = ((double) destHeight) / (double) sourceHeight;
        
        
    	// resize and crop image
    	double scale;
    	if (xScale < yScale) {
    		scale = xScale;
    		
    	} else {
    		scale = yScale;
    	}
        
        int width = (int) (sourceWidth * scale);
        int height = (int) (sourceHeight * scale);
        int x0 = (destWidth - width) / 2;
        int y0 = (destHeight - height) / 2;
        
        return Utils.resizeImage(sourceImage, x0, y0, width, height, true);
    }		

	/**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
	public static BufferedImage resizeImage(BufferedImage img, int x0, int y0,
			int targetWidth, int targetHeight, boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		boolean highQualityInUse = higherQuality && (targetWidth < img.getWidth() && targetHeight < img.getHeight());
		if (highQualityInUse ) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}
			
			int x00 = 0, y00 = 0;
			if (w == targetWidth && h == targetHeight) {
				x00 = x0;
				y00 = y0;
			}

			BufferedImage tmp = new BufferedImage(w + 2*x00, h + 2*y00, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(ret, x00, y00, w, h, Color.BLACK, null);
			g2.dispose();
			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

//		if (highQualityInUse) {
//			BufferedImage tmp = new BufferedImage(w + 2*x0, h + 2*y0, type);
//			Graphics2D g2 = tmp.createGraphics();
//			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//			g2.drawImage(ret, x0, y0, w, h, Color.BLACK, null);
//			g2.dispose();
//			ret = tmp;
//		}
		
		return ret;
	}

}
