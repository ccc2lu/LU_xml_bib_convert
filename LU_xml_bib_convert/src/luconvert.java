import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.Record;

/**
 * Writes MARc XML to standard output
 * 
 */
public class luconvert {

    public static void main(String args[]) throws Exception {

    	System.out.println("Opening " + args[0] + ", converting to MarcXML, and writing to " + args[1]);
        //InputStream input = luconvert.class.getResourceAsStream(args[0]);
    	File inputFile = new File(args[0]);
    	File outputFile = new File(args[1]);
    	FileInputStream input = new FileInputStream(inputFile); 
    	FileOutputStream output = new FileOutputStream(outputFile);
    	//PrintStream outputprintstream = new PrintStream(output);
    	
        MarcReader reader = new MarcStreamReader(input);
        MarcWriter writer = new MarcXmlWriter(output, "UTF-8", true);

        //ByteArrayOutputStream tempout;
        //String marcXML;
        
        // This program creates subfield codes with invalid XML character
        // references in it.  I tried to filter them out using my
        // own subclass of AnselToUnicode, called lu_char_converter,
        // and just registering that converter with the MarcWriter object,
        // but it turns out it is only applied to the data between the tags, 
        // not the tags themselves.  
        
        // I the tried writing all the MarcXML to a ByteArrayOutputStream one
        // record at a time, and then applying my converter to that, but that
        // re-generated the xml version tag over and over again and put each
        // record in its own collection tag.  So, instead I just wrote
        // a perl script to post-process the entire output of this program 
        // with the same regular expressions I put in the lu_char_converter
        // class's convert method.  It's an extra step, but necessary to remove 
        // the invalid character references.
        
        //lu_char_converter lu_converter = new lu_char_converter();
        
        AnselToUnicode converter = new AnselToUnicode();
        writer.setConverter(converter);
        
        //int limit = -1; // All records
        int limit = 50000;
        int curr = 0;
        int showprogress = 1000;
        while (reader.hasNext() && (limit <= 0 || curr < limit)) {
            Record record = reader.next();
            //tempout = new ByteArrayOutputStream();
            //writer = new MarcXmlWriter(tempout, "UTF-8");
            writer.write(record);
            //writer.close();
            //marcXML=tempout.toString("UTF-8");
            //outputprintstream.println(lu_converter.convert(marcXML.toCharArray()));
            if ( curr % showprogress == 0 ) {
            	System.out.println("Converted " + curr + " records ...");
            }
            curr++;
        }
        writer.close();
        input.close();
        System.out.println("Done converting to MarcXML");
    }
}
