/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.strings;

import java.util.regex.Pattern;

/**
 * Interface to allow the storing and reading of individual lines in a CharSequence
 * @author jp242
 */
public interface LineReader {

    public String readLine();
    public Boolean hasLine();
}
