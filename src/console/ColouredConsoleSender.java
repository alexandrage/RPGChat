package console;

import java.util.regex.Matcher;
import java.awt.Color;
import java.util.regex.Pattern;

public class ColouredConsoleSender {
	private static String RGB_STRING;
	private static Pattern RBG_TRANSLATE;
	static {
		RGB_STRING = String.valueOf(String.valueOf('\u001b')) + "[38;2;%d;%d;%dm";
		RBG_TRANSLATE = Pattern
				.compile(String.valueOf(String.valueOf('§')) + "x(" + String.valueOf('§') + "[A-F0-9]){6}", 2);
	}

	public static String convertRGBColors(final String input) {
		final Matcher matcher = ColouredConsoleSender.RBG_TRANSLATE.matcher(input);
		final StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			final String s = matcher.group().replace("§", "").replace('x', '#');
			final Color color = Color.decode(s);
			final int red = color.getRed();
			final int blue = color.getBlue();
			final int green = color.getGreen();
			final String replacement = String.format(ColouredConsoleSender.RGB_STRING, red, green, blue);
			matcher.appendReplacement(buffer, replacement);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}