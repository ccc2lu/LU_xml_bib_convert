import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 * Reads in MARC from a file named by the first argument, writes out
 * MarcXML to the file given by the second argument.
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
    	
        MarcReader reader = new MarcStreamReader(input, "ISO-8859-1");
        
        //OutputFormat format = new OutputFormat("text", "UTF-8", true);
        //XMLSerializer serializer = new XMLSerializer(output, format);
        //Result result = new SAXResult(serializer.asContentHandler());
        //MarcXmlWriter writer = new MarcXmlWriter(result);

        MarcXmlWriter writer = new MarcXmlWriter(output, "UTF-8", true);
        
        //ByteArrayOutputStream tempout;
        //String marcXML;
        
        // This program creates subfield codes with invalid XML character
        // references in it.  I tried to filter them out using my
        // own subclass of AnselToUnicode, called lu_char_converter,
        // and just registering that converter with the MarcWriter object,
        // but it turns out it is only applied to the data between the tags, 
        // not the tags themselves.  
        
        // I then tried writing all the MarcXML to a ByteArrayOutputStream one
        // record at a time, and then applying my converter to that, but that
        // re-generated the xml version tag over and over again and put each
        // record in its own collection tag.  So, instead I just wrote
        // a perl script to post-process the entire output of this program 
        // with the same regular expressions I put in the lu_char_converter
        // class's convert method.  It's an extra step, but necessary to remove 
        // the invalid character references.
        
        //lu_char_converter lu_converter = new lu_char_converter();
        
        AnselToUnicode converter = new AnselToUnicode();
        //converter.setTranslateNCR(false);
        writer.setConverter(converter);
        writer.setUnicodeNormalization(true);

        int limit = -1; // No limit, convert all records
        if ( args.length == 3 ) {
        	limit = Integer.parseInt(args[2]);
        	System.out.println("Converting only " + limit + " records");
        }
        int curr = 0;
        int showprogress = 1000;
        MarcFactory fact = MarcFactory.newInstance();
        int controlnumber = 0;
        String formattednumber = "";
        while (reader.hasNext() && (limit <= 0 || curr < limit)) {
            Record record = reader.next();
            //tempout = new ByteArrayOutputStream();
            //writer = new MarcXmlWriter(tempout, "UTF-8");

            // We strip the starting "a" off the 001 field, then pad it to 11 characters long
            // with leading zeros
            formattednumber = record.getControlNumber();
            if ( formattednumber.substring(0, 1).equals("a") ) {
                controlnumber = Integer.parseInt(formattednumber.substring(1));
                //formattednumber = String.format("%011d", controlnumber);
                formattednumber = Integer.toString(controlnumber);
            } else {
            	System.out.println("001 field without an \"a\" at the beginning: " + formattednumber);
            }

            record.addVariableField(fact.newControlField("001", formattednumber));

            // How can we add fields to edit the 001?  I want to remove the "a" from it, maybe pad it to 11 characters total
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
