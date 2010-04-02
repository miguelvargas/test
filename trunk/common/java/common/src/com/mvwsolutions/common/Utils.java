package com.mvwsolutions.common;

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

/**
 * 
 * @author smineyev
 *
 */
public class Utils {

	private Utils() {
		// instance creation prohibited
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

    
}
