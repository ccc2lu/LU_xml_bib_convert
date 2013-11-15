import java.io.InputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.Record;

public class test {

	public static void main(String[] args) {
		lu_char_converter converter = new lu_char_converter();
		String teststr = "<leader>03337cas  2200577 a 45&#2;0</leader>";
		teststr = converter.convert(teststr.getBytes());
		teststr = "<subfield code=\"&#31;\">zAvailable to Lehigh users</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"&#31;\">?UNAUTHORIZED</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"&#27;\">p1</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"a\">圖說香港&#0;影史</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"&#31;\">?UNAUTHORIZED</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"&#31;\">=^A274090</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"a\">美凍坚&#0;众国宪法</subfield>";
		teststr = converter.convert(teststr.getBytes());
	    teststr = "<subfield code=\"a\">易經基本認識包括易象易數易理應用硏究&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;&#0;</subfield>";
		teststr = converter.convert(teststr.getBytes());

	}
}
