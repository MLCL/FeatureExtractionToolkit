/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Static utility class for file manipulation.
 * 
 * @author Simon Wibberley
 */
public final class Path {
    
    private Path() {}

    public static void catFiles(String inputDir, String suffix, String outputDir,
            String name) {
        catFiles(inputDir, suffix, outputDir, name, true);
    }

    public static void catFiles(String inputDir, String suffix, String outputDir,
            String name, boolean rm) {

        String input = String.format("%s/*%s", inputDir, suffix);
        String output = String.format("%s/%s", outputDir, name);
        String cmdStr = String.format("/bin/cat %s > %s", input, output);
        Path.exec(cmdStr);
        if (rm) {
            Path.exec(String.format("rm %s/*%s", inputDir, suffix));
        }


    }

    public static void exec(String command) {
        System.err.println(command);
        //String cmdStr = String.format("/bin/bash -c \"%s\"", command);
        try {

            //System.err.println(cmdStr);
            Process child = Runtime.getRuntime().exec(
                    new String[]{"/bin/bash", "-c", command});

            //BufferedWriter output = new BufferedWriter(new OutputStreamWriter(child.getOutputStream()));
            //output.write(command);
            //output.write("\n");
            //output.flush();

            child.waitFor();

            BufferedReader input = new BufferedReader(new InputStreamReader(child.
                    getInputStream()));
            String line;

            while ((line = input.readLine()) != null) {
                System.err.println(line);
            }

            int exitVal = child.waitFor();
            if (exitVal > 0) {
                System.err.println("Exited with error code " + exitVal);
                input = new BufferedReader(new InputStreamReader(child.
                        getErrorStream()));

                while ((line = input.readLine()) != null) {
                    System.err.println(line);
                }

            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public static List<String> getFileList(String pathName, final String suffix) {
        return getFileList(pathName, suffix, false);
    }

    public static List<String> getFileList(String pathName, final String suffix,
            final boolean includeHidden) {
        return getFileList(pathName, suffix, includeHidden, false);
    }

    public static List<String> getFileList(String pathName, final String suffix,
            final boolean includeHidden, final boolean recursive) {
        File path = new File(pathName);

        final ArrayList<String> fileList = new ArrayList<String>();

        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (recursive) {
                    File f = new File(
                            dir.getAbsolutePath() + File.separator + name);
                    if (f.isDirectory()) {

                        List<String> subList = getFileList(f.getAbsolutePath(),
                                suffix, includeHidden, recursive);

                        for (String file : subList) {
                            String subPath = name + File.separator + file;
                            fileList.add(subPath);
                        }
                    }
                }
                if (includeHidden) {
                    return name.toLowerCase().endsWith(suffix);
                } else {
                    String lc = name.toLowerCase();
                    return lc.endsWith(suffix) && !lc.startsWith(".");
                }

            }
        };

        fileList.addAll(Arrays.asList(path.list(filter)));

        return fileList;
    }

    public static String getText(String filePath) {
        return getText(filePath, false, false);

    }

    public static String getText(String filePath, boolean gzip,
            boolean incNewline) {
        try {
            if (gzip) {
                return getText(new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
                        filePath)))), incNewline);
            } else {
                return getText(new BufferedReader(new FileReader(filePath)),
                        incNewline);
            }

        } catch (Exception e) {
            return null;
        }

    }

    public static String getText(BufferedReader reader) {
        return getText(reader, false);
    }

    public static String getTextWholeFile(BufferedReader reader) {

        try {
            char[] strbuf = new char[9999];
            StringBuilder strbldr = new StringBuilder();
            int read;
            while ((read = reader.read(strbuf)) >= 0) {
                strbldr.append(strbuf, 0, read);
            }

            String text = strbldr.toString();
            reader.close();
            return text;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    public static String getText(BufferedReader reader, boolean incNewline) {
        if (incNewline) {
            return getTextWholeFile(reader);
        }
        try {
            StringBuffer strbfr = new StringBuffer();
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                strbfr.append(tmpStr);
                if (incNewline) {
                    strbfr.append("\n");
                }
            }

            String text = strbfr.toString();
            reader.close();
            return text;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }
    
    
    public static int countLines(String in) {
    	try{
    		return countLines(new BufferedReader(new FileReader(in)));	
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	return -1;
    }
    
    public static int countLines(BufferedReader in) {
    	BufferedReader r = new BufferedReader(in);
    	int count = 0;
    	try{
	    	while (r.readLine()!=null) {
	    		++count;
	    	}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return count;
    }
}
