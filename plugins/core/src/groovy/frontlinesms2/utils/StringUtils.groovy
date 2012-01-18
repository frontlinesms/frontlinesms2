package frontlinesms2.utils

import java.text.Normalizer
import java.util.regex.Pattern

public class StringUtils {
  private StringUtils() {}

  public static def unAccent(String s) {
	String temp = Normalizer.normalize(s, Normalizer.Form.NFD)
	Pattern pattern = Pattern.compile("\\p{" + "InCombiningDiacriticalMarks}+")
	return pattern.matcher(temp).replaceAll("")
  }
}