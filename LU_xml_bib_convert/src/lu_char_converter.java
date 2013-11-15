import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.converter.CharConverter;
import org.apache.commons.codec.binary.*;

public class lu_char_converter extends CharConverter {
	
	AnselToUnicode converter;
	
	public lu_char_converter() {
		converter = new AnselToUnicode();
	}


	public String convert(char[] data) {
		// Find all the instances of invalid &#[0-9]+; sequences, then
		// replace them with Base64 encodings of the sequences
		String temp = new String(data);
		String replacement;
		Pattern p;
		Matcher m;
		
		System.out.println("Converting string: " + temp);
		
		// Replace the 2nd to last byte of the leader field with a 0 when it's an
		// invalid XML character reference number.  The 2nd to last byte of a leader
		// field should always be 0 according to:
		// http://www.loc.gov/marc/bibliographic/bdleader.html
		p = Pattern.compile("<leader>(.*)&#[0-9]*;(.*)</leader>");
		m = p.matcher(temp);
		while ( m.find() ) {
			replacement = "<leader>" + m.group(1) + "0" + m.group(2) + "</leader>";
			temp = m.replaceAll(replacement);
		}
		
		// In many cases, it looks like Sirsi or something mangled these 
		// subfield codes, putting the letter that should have been in quotes
		// after the tag ends but before the value begins.  So, we replace
		// them as follows:
		p = Pattern.compile("<subfield code=\"&#31;\">([a-z])");
		m = p.matcher(temp);
		while ( m.find() ) {
			replacement = "<subfield code=\"" + m.group(1) + "\">";
			temp = m.replaceAll(replacement);
		}
		
		// Replace any remaining such invalid character reference numbers with a
		// Base64 representation of them to keep subsequent attempts at XML parsing 
		// from choking on them
		p = Pattern.compile("(&#[0-9]+;)");
		m = p.matcher(temp);
		while (m.find()) {
			replacement = Base64.encodeBase64String(m.group(1).getBytes());
			//System.out.println("Matched on " + m.group(1) + ", replacing with " + replacement);
			temp = m.replaceAll(replacement);
		}
		System.out.println("Converted version: " + temp);
		
		// Let AnselToUnicode handle the rest
		return converter.convert(temp);
	}
}