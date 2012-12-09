/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows the individual lines within a char sequence to be read and 
 * @author jp242
 */
public class NewlineReader implements LineReader{
    
    private final Matcher matcher;
    private final CharSequence file;
    private int start;
    private int end;
    
    public NewlineReader(CharSequence file, String newLineDelim){
        this.file = file;
        matcher = Pattern.compile(newLineDelim).matcher(file);
        start = 0;
        end = 0;
    }
    
    @Override
    public String readLine(){
        end = matcher.start();
        String line = file.subSequence(start, end).toString();
        start = matcher.end();
        return line;
    }

    @Override
    public Boolean hasLine() {
	    //todo this returns false if the text is all on one line
        return matcher.find();
    }
    
}
