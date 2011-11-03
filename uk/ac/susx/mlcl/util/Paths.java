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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Static utility class for file manipulation.
 * 
 * @author Simon Wibberley
 */
public final class Paths {

    private static final Logger LOG = Logger.getLogger(
            Paths.class.getName());

    private Paths() {
    }

    public static void catFiles(String inputDir, String suffix, String outputDir,
            String name) {
        catFiles(inputDir, suffix, outputDir, name, true);
    }

    public static void catFiles(String inputDir, String suffix, String outputDir,
            String name, boolean rm) {

        String input = String.format("%s/*%s", inputDir, suffix);
        String output = String.format("%s/%s", outputDir, name);
        String cmdStr = String.format("/bin/cat %s > %s", input, output);
        Paths.exec(cmdStr);
        if (rm) {
            Paths.exec(String.format("rm %s/*%s", inputDir, suffix));
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

            BufferedReader input = new BufferedReader(new InputStreamReader(child.getInputStream()));
            String line;

            while ((line = input.readLine()) != null) {
                System.err.println(line);
            }

            int exitVal = child.waitFor();
            if (exitVal > 0) {
                System.err.println("Exited with error code " + exitVal);
                input = new BufferedReader(new InputStreamReader(child.getErrorStream()));

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
            final boolean includeHidden,
            final boolean recursive) {
        if(pathName == null)
            throw new NullPointerException("pathName is null");
        if(suffix == null)
            throw new NullPointerException("suffix is null");
        
        File path = new File(pathName);
        if (!path.exists()) {
            throw new IllegalArgumentException("path does not exist: " + path);
        }
        if (!path.isDirectory()) {
            throw new IllegalArgumentException("path is not a directory: " + path);
        }
        if (!path.canRead()) {
            throw new IllegalArgumentException("path is not readable: " + path);
        }

        final ArrayList<String> fileList = new ArrayList<String>();

        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (recursive) {
                    File f = new File(
                            dir.getAbsolutePath() + File.separator + name);
                    if (f.isDirectory()) {

                        List<String> subList = getFileList(f.getAbsolutePath(),
                                suffix, includeHidden,
                                recursive);

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

    public static CharSequence getText(String filePath) {
        return getText(filePath, false, false);

    }

    public static CharSequence getText(String filePath, boolean gzip,
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

    public static CharSequence getText(BufferedReader reader) {
        return getText(reader, false);
    }

    public static CharSequence getTextWholeFile(BufferedReader reader) {

        try {
            char[] strbuf = new char[9999];
            StringBuilder strbldr = new StringBuilder();
            int read;
            while ((read = reader.read(strbuf)) >= 0) {
                strbldr.append(strbuf, 0, read);
            }

            
//            String text = strbldr.toString();
            reader.close();
            return strbldr;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    public static CharSequence getText(BufferedReader reader, boolean incNewline) {
        if (incNewline) {
            return getTextWholeFile(reader);
        }
        try {
            StringBuilder strbfr = new StringBuilder();
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                strbfr.append(tmpStr);
                if (incNewline) {
                    strbfr.append("\n");
                }
            }

//            String text = strbfr.toString();
            reader.close();
            return strbfr;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    public static int countLines(String in) {
        try {
            return countLines(new BufferedReader(new FileReader(in)));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return -1;
    }

    public static int countLines(BufferedReader in) {
        BufferedReader r = new BufferedReader(in);
        int count = 0;
        try {
            while (r.readLine() != null) {
                ++count;
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }

        return count;
    }
}
