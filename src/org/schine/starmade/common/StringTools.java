/**
 * <H1>Project R<H1>
 * <p/>
 * <p/>
 * <H2>StringTools</H2>
 * <H3>org.schema.common.util</H3>
 * StringTools.java
 * <HR>
 * Description goes here. If you see this message, please contact me and the
 * description will be filled.<BR>
 * <BR>
 *
 * @author Robin Promesberger (schema)
 * @mail <A HREF="mailto:schemaxx@gmail.com">schemaxx@gmail.com</A>
 * @site <A
 * HREF="http://www.the-schema.com/">http://www.the-schema.com/</A>
 * @project JnJ / VIR / Project R
 * @homepage <A
 * HREF="http://www.the-schema.com/JnJ">
 * http://www.the-schema.com/JnJ</A>
 * @copyright Copyright © 2004-2010 Robin Promesberger (schema)
 * @licence Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.schine.starmade.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The Class StringTools.
 */
public class StringTools {

	public static final Pattern p = Pattern.compile("\n|\r");
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrsuvwxyz";
	private final static NumberFormat m = NumberFormat.getNumberInstance(Locale.US);
	private static final boolean WITH_DASH = false;
	private static final Pattern parameterRegex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
	/**
	 * The four zero.
	 */
	private static DecimalFormat threeZero = new DecimalFormat("000");
	private static DecimalFormat fourZero = new DecimalFormat("0000");
	private static DecimalFormat twoZero = new DecimalFormat("00");
	private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
	//	static{
	//		otherSymbols.setDecimalSeparator('.');
	//		otherSymbols.setGroupingSeparator('');
	//	}
	private static DecimalFormat point0 = new DecimalFormat("#0.0", otherSymbols);
	private static DecimalFormat point1 = new DecimalFormat("#0.00", otherSymbols);
	private static DecimalFormat point2 = new DecimalFormat("#0.000", otherSymbols);
	private static DecimalFormat massFormat = new DecimalFormat("##0E0");
	private static String[] suffix = new String[]{"", "k", "m", "b", "t"};
	private static int MAX_LENGTH = 4;
	private static Random random = new Random();

	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String cpt(String cpt) {
		String new_string = "";
		for (int i = 0; i < cpt.length(); i++) {
			char c = cpt.charAt(i);
			if (Character.isUpperCase(c)) {
				new_string = new_string + " " + Character.toLowerCase(c);
			} else {
				new_string = new_string + c;
			}
		}
		return new_string;
	}

	public static String escapeRegexp(String regexp) {
		String specChars = "\\$.*+?|()[]{}^";
		String result = regexp;
		for (int i = 0; i < specChars.length(); i++) {
			Character curChar = specChars.charAt(i);
			result = result.replaceAll(
					"\\" + curChar,
					"\\\\" + (i < 2 ? "\\" : "") + curChar); // \ and $ must have special treatment
		}
		return result;
	}

	public static List<String> findGroup(String content, String pattern, int group) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group(group));
		}
		return result;
	}

	public static String formatSeperated(int nr) {
		return m.format(nr);
	}

	public static String formatSeperated(double nr) {
		return m.format(nr);
	}

	public static String formatSeperated(long nr) {
		return m.format(nr);
	}

	public static List<String> tokenize(String content, String firstToken, String lastToken) {
		String regexp = lastToken.length() > 1
				? escapeRegexp(firstToken) + "(.*?)" + escapeRegexp(lastToken)
				: escapeRegexp(firstToken) + "([^" + lastToken + "]*)" + escapeRegexp(lastToken);
		return findGroup(content, regexp, 1);
	}

	public static String[] splitTotokens(String line, String delim) {
		String s = line;
		int i = 0;

		while (s.contains(delim)) {
			s = s.substring(s.indexOf(delim) + delim.length());
			i++;
		}
		String token = null;
		String remainder = null;
		String[] tokens = new String[i];

		for (int j = 0; j < i; j++) {
			token = line.substring(0, line.indexOf(delim));
			//System.out.print("#" + token + "#");
			tokens[j] = token;
			remainder = line.substring(line.indexOf(delim) + delim.length());
			//System.out.println("#" + remainder + "#");
			line = remainder;
		}

		return tokens;
	}

	public static String massFormat(long number) {
		if (number < 1000) {
			return String.valueOf(number);
		}
		String r = massFormat.format(number);
		r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
		while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
			r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
		}
		return r;
	}

	public static String massFormat(int number) {
		if (number < 1000) {
			return String.valueOf(number);
		}
		String r = massFormat.format(number);
		r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
		while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
			r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
		}
		return r;
	}

	public static String massFormat(float number) {
		if (number < 1000f) {
			return formatPointZero(number);
		}
		String r = massFormat.format(number);
		r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
		while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
			r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
		}
		return r;
	}

	public static String massFormat(double number) {
		if (number < 1000d) {
			return formatPointZero(number);
		}
		String r = massFormat.format(number);
		r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
		while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
			r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
		}
		return r;
	}

	public static int coundNewLines(String input) {
		Matcher m = p.matcher(input);
		int lines = 1;
		while (m.find()) {
			lines++;
		}
		return lines;
	}

	/**
	 * Fill.
	 *
	 * @param in  the in
	 * @param max the max
	 * @return the string
	 */
	public static String fill(String in, int max) {
		int rest = max - in.length();
		StringBuffer r = new StringBuffer();
		for (int i = 0; i < rest; i++) {
			r.append(" ");
		}

		return in + r.toString();
	}

	/**
	 * Format four zero.
	 *
	 * @param i the i
	 * @return the string
	 */
	public static String formatFourZero(int i) {
		return fourZero.format(i);
	}

	public static String formatThreeZero(int i) {
		return threeZero.format(i);
	}

	public static String formatPointZero(double i) {
		return point0.format(i);
	}

	public static String formatPointZero(float i) {
		return point0.format(i);
	}

	public static String formatPointZeroZero(double i) {
		return point1.format(i);
	}

	public static String formatPointZeroZero(float i) {
		return point1.format(i);
	}

	public static String formatPointZeroZeroZero(double i) {
		return point2.format(i);
	}

	public static String formatPointZeroZeroZero(float i) {
		return point2.format(i);
	}

	

	/**
	 * Format four zero.
	 *
	 * @param i the i
	 * @return the string
	 */
	public static String formatTwoZero(int i) {
		return twoZero.format(i);
	}

	public static <E> List<E> LongestCommonSubsequence(E[] s1, E[] s2) {
		int[][] num = new int[s1.length + 1][s2.length + 1];  //2D array, initialized to 0

		//Actual algorithm
		for (int i = 1; i <= s1.length; i++)
			for (int j = 1; j <= s2.length; j++)
				if (s1[i - 1].equals(s2[j - 1]))
					num[i][j] = 1 + num[i - 1][j - 1];
				else
					num[i][j] = Math.max(num[i - 1][j], num[i][j - 1]);

		//	        System.out.println("length of LCS = " + num[s1.length][s2.length]);

		int s1position = s1.length, s2position = s2.length;
		List<E> result = new LinkedList<E>();

		while (s1position != 0 && s2position != 0) {
			if (s1[s1position - 1].equals(s2[s2position - 1])) {
				result.add(s1[s1position - 1]);
				s1position--;
				s2position--;
			} else if (num[s1position][s2position - 1] >= num[s1position - 1][s2position]) {
				s2position--;
			} else {
				s1position--;
			}
		}
		Collections.reverse(result);
		return result;
	}

	/**
	 * blargh.... this function is fucking memory inefficient....
	 *
	 * @param aS
	 * @param bS
	 * @return
	 */
	public static String LongestCommonSubsequence(String aS, String bS) {
		char[] a = aS.toCharArray();
		char[] b = bS.toCharArray();

		Character[] s1 = new Character[a.length];
		for (int z = 0; z < a.length; z++) {
			s1[z] = a[z];
		}
		Character[] s2 = new Character[b.length];
		for (int z = 0; z < b.length; z++) {
			s2[z] = b[z];
		}
		StringBuffer s = new StringBuffer();
		List<Character> longestCommonSubsequence = StringTools.LongestCommonSubsequence(s1, s2);
		for (int i = 0; i < longestCommonSubsequence.size(); i++) {
			if (s1[i].equals(longestCommonSubsequence.get(i)) && s2[i].equals(longestCommonSubsequence.get(i))) {
				s.append(longestCommonSubsequence.get(i));
			}
		}
		return s.toString();
	}

	/**
	 * Path to string.
	 *
	 * @param path the path
	 * @return the string
	 */
	public static String pathToString(ArrayList<int[]> path) {
		StringBuffer sb = new StringBuffer();
		for (int i = path.size() - 1; i >= 0; i--) {
			sb.append("[" + path.get(i)[0] + "," + path.get(i)[1] + "]->");
		}

		return sb.toString();

	}

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(random.nextInt(AB.length())));
		}
		return sb.toString();
	}

	public static String[] splitParameters(String command) {
		List<String> matchList = new ArrayList<String>();

		Matcher regexMatcher = parameterRegex.matcher(command);
		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
				// Add double-quoted string without the quotes
				matchList.add(regexMatcher.group(1));
			} else if (regexMatcher.group(2) != null) {
				// Add single-quoted string without the quotes
				matchList.add(regexMatcher.group(2));
			} else {
				// Add unquoted word
				matchList.add(regexMatcher.group());
			}
		}
		String[] parts = new String[matchList.size()];

		for (int i = 0; i < parts.length; i++) {
			System.err.println("[CLIENT] Admin parameter " + i + ": " + matchList.get(i));
			parts[i] = matchList.get(i);
		}
		return parts;
	}

	public static String wrap(String in, int len) {
		in = in.trim();
		if (in.length() < len)
			return in;
		if (in.substring(0, len).contains("\n"))
			return in.substring(0, in.indexOf("\n")).trim() + "\n\n"
					+ wrap(in.substring(in.indexOf("\n") + 1), len);
		int place;
		if (WITH_DASH) {
			int dash = in.lastIndexOf("-", len);
			place = Math.max(
					Math.max(in.lastIndexOf(" ", len), in.lastIndexOf("\t", len)),
					dash >= 0 ? dash + 1 : -1);
		} else {
			place = Math.max(in.lastIndexOf(" ", len), in.lastIndexOf("\t", len));
		}
		if (place < 0) {
			return in;
		}
		return in.substring(0, place).trim() + "\n"
				+ wrap(in.substring(place), len);
	}

	public static String formatDistance(float dist) {
		return dist > 1000f ? formatPointZero(dist / 1000f) + "km" : formatPointZero(dist) + "m";
	}

	public static String formatDistance(double dist) {
		return dist > 1000d ? formatPointZero(dist / 1000d) + "km" : formatPointZero(dist) + "m";
	}

	public static String getCommaSeperated(double[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i < a.length - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	public static String getCommaSeperated(long[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i < a.length - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	public static String getCommaSeperated(float[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i < a.length - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	public static String getCommaSeperated(byte[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i < a.length - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	public static String getCommaSeperated(int[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i < a.length - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	public static String getCommaSeperated(short[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i]);
			if (i < a.length - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	public static String formatTimeFromMS(long t) {
		if (t < 60000) {
			return formatPointZero((t) / 1000d) + " secs";
		} else {
			int secs = (int) (t / 1000L);
			int minutes = secs / 60;

			int hours = minutes / 60;

			int days = hours / 24;

			secs = secs %= 60;
			minutes = minutes %= 60;
			hours = hours %= 24;

			if (days > 0) {
				return days + " d, " + hours + " h" + ", " + minutes + " min";
			} else if (hours > 0) {
				return hours + " h" + ", " + minutes + " min";
			} else if (minutes > 0) {
				return minutes + " min" + ", " + secs + " secs";
			} else {
				return secs + " secs";
			}
		}
	}

	public static String formatSmallAndBig(int v) {
		return formatSeperated(v);
	}

	public static String formatSmallAndBig(long v) {
		return formatSeperated(v);
	}

	public static String formatSmallAndBig(float v) {
		boolean hasDecimal = v % 1f != 0f;
		if (hasDecimal && Math.abs(v) < 1f) {
			String formatPointZeroZero = formatPointZeroZero(v);
			return formatPointZeroZero.endsWith("0") ? formatPointZero(v) : formatPointZeroZero;
		} else if (hasDecimal && Math.abs(v) < 100f) {
			return formatPointZero(v);
		} else {
			return formatSeperated(((int) (v * 10f)) / 10f); //shorten to one decimal
		}
	}

	public static String formatSmallAndBig(double v) {
		boolean hasDecimal = v % 1d != 0d;
		if (hasDecimal && Math.abs(v) < 1d) {
			return formatPointZeroZero(v);
		} else if (hasDecimal && Math.abs(v) < 100d) {
			return formatPointZero(v);
		} else {
			return formatSeperated(((long) (v * 10d)) / 10d); //shorten to one decimal
		}
	}

	public class StringLengthComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			if (o1.length() < o2.length()) {
				return -1;
			} else if (o1.length() > o2.length()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	

    public static String formatRaceTime(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d.%03d", min, sec, ms);
    }

	public static SimpleDateFormat getSimpleDateFormat(String string, String defaultT) {
		try{
			return new SimpleDateFormat(string);
		}catch(Exception e){
			System.err.println("Exception: INVALID DATE TIME: "+string);
			e.printStackTrace();
			return new SimpleDateFormat(defaultT);
		}
	}

}
