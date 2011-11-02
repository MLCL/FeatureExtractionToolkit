/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

import java.util.logging.Level;
import uk.ac.susx.mlcl.lib.MiscUtil;
import uk.ac.susx.mlcl.util.Config;
import uk.ac.susx.mlcl.util.Configurable;
import uk.ac.susx.mlcl.util.DocSplitter;
import uk.ac.susx.mlcl.util.Path;

/**
 * Change Log 09-09-2011
 * ==============================
 * * Added command line usage descriptions and "--help" option.
 * 
 * @author Simon Wibberley
 */
public abstract class Parser implements Configurable {

    private static final Logger LOG =
            Logger.getLogger(Parser.class.getName());

    public static final class OutputFormatterConverter implements IStringConverter<OutputFormatter> {

        @SuppressWarnings("unchecked")
        @Override
        public OutputFormatter convert(String value) {

            OutputFormatter f = null;
            try {
                try {
                    f = ((Class<OutputFormatter>) Class.forName(value)).newInstance();

                } catch (ClassNotFoundException e) {
                    LOG.log(Level.WARNING, "OutputFormatter {0} not found.", value);
                    f = OutputFormatters.defaultFormatterClass.newInstance();
                }
            } catch (InstantiationException e) {
                LOG.log(Level.SEVERE, null, e);
            } catch (IllegalAccessException e) {
                LOG.log(Level.SEVERE, null, e);
            }


            return f;

        }
    }

    public static class ParserConfig extends Config {

        private static final long serialVersionUID = 1L;
        @Parameter(names = {"-es", "--entrySeparator"},
        description = "base term / feature delimiter for the thesaurus output.")
        public String entrySeparator = "\t";
        @Parameter(names = {"-op", "--outPath"},
        required = true)
        public String outPath;
        @Parameter(names = {"-ip", "--inPath"},
        required = true)
        public String inPath;
        @Parameter(names = {"-r", "--recursive"},
        description = "Descend into subfolder of the input path.")
        public boolean recursive = false;
        @Parameter(names = {"-is", "--inSuffix"},
        description = "Only read files with the given suffix.",
        required = true)
        public String inSuffix;
        @Parameter(names = {"-gz", "--useGzip"})
        public boolean useGzip = false;
        @Parameter(names = {"-of", "--outputFormatter"},
        description = "class that specifies the output format.",
        converter = OutputFormatterConverter.class)
        public OutputFormatter outputFormatter = OutputFormatters.defaultFormatter;
        @Parameter(names = {"-v", "--verbose"})
        public boolean verbose = false;
        @Parameter(names = {"-l", "--limit"})
        public int limit = 0;
    }

    protected abstract ParserConfig config();
    protected BufferedWriter outFile;
    protected DocSplitter splitter;
    final AtomicInteger count = new AtomicInteger();
    private ExecutorService exec;
    private List<Future<Void>> futures;
    protected FeatureFactory featureFactory;

    protected String getOutPath() {
        return config().outPath;
    }

    protected abstract void buildFeatureFactory();

    public void parse() {

        System.err.println("Building file list...");

        List<String> files = Path.getFileList(
                config().inPath, config().inSuffix,
                false, config().recursive);

        System.err.println("File list built.");

        buildFeatureFactory();

        parse(config().inPath, files);

    }

    public void parse(String input) {
        parse(input, true);
    }

    /*
    public void parse(String input, boolean prePost) {
    //System.err.println(input);
    
    if(prePost) {
    handleOutputPre();
    }
    List<String> entries = splitter.split(input);
    
    
    for(String entry : entries){
    
    String output = handleEntry(entry);
    
    handleOutput(output);
    
    int c = count.addAndGet(1);
    if (c % 1000 == 0) {
    System.err.print("[" + c + "]");
    }
    }
    
    if(prePost) { 
    handleOutputPost();
    }
    }
     */
    public void parse(final CharSequence input, final boolean prePost) {
        //System.err.println(input);

        if (prePost) {
            handleOutputPre();
        }
        final List<String> entries = splitter.split(input.toString());

        final int limit = config().limit;

        final Semaphore throttle = new Semaphore(2 * config().numCores);

        for (final String entry : entries) {
            try {
                throttle.acquire();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, null, e);
            }

            if (limit > 0 && count.get() > limit) {
                break;
            }

            final Future<Void> f = exec.submit(new Callable<Void>() {

                @Override
                public Void call() {
                    try {
                        final CharSequence output = handleEntry(entry);
                        handleOutput(output);

                        if (count.addAndGet(1) % 1000 == 0) {
                            System.err.print("[" + count.get() + "]");
                        }

                    } catch (InvalidEntryException e) {
                        if (config().verbose) {
                            LOG.log(Level.WARNING, null, e);
                        }
                    } finally {
                        throttle.release();
                    }


                    return null;
                }
            });

            futures.add(f);
        }

        if (prePost) {
            handleOutputPost();
        }
    }

    private void initThreads() {
        exec = Executors.newFixedThreadPool(config().numCores);
        futures = new ArrayList<Future<Void>>();
    }

    private void cleanupThreads() {
        exec.shutdown();

        try {
            exec.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, null, e);
            exec.shutdownNow();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void parse(String prefix, List<String> files) {
        handleOutputPre();

        initThreads();

        for (String file : files) {

//            System.gc();
            System.err.println("processing " + prefix + File.separator + file);
            System.err.println(MiscUtil.memoryInfoString());

            parse(Path.getText(prefix + File.separator + file, false, true),
                    false);

//            try {
            futures.clear();
//                for (Future<Void> f : futures) {
////                    if (f.isDone()) {
//                    f.get();
////                    }
//                }
//            } catch (InterruptedException e) {
//                LOG.log(Level.SEVERE, null, e);
//            } catch (ExecutionException e) {
//                LOG.log(Level.SEVERE, null, e);
//            }
        }

        cleanupThreads();

        handleOutputPost();
    }

    protected void handleOutputPre() {
        String outPath = getOutPath();
        try {

            outFile = new BufferedWriter(new FileWriter(outPath));


        } catch (IOException e) {
            System.err.println(e);
        }
        System.err.println("opening " + outPath);
    }

    protected void handleOutputPost() {

        try {
            outFile.close();
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    protected final void handleOutput(final CharSequence output) {
        if(output.length() == 0)
            return;
        try {
            synchronized (outFile) {
                outFile.append(output);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    protected CharSequence allPairs(
            List<String> baseEntries, List<String> featureEntries) {

        StringBuilder strbldr = new StringBuilder();
        for (String el1 : baseEntries) {

            for (String el2 : featureEntries) {
                if (el2.equals(el1)) {
                    continue;
                }
                String line = el1 + config().entrySeparator + el2;
                strbldr.append(line);
                strbldr.append("\n");
                //System.err.println(line);
            }

        }
        return strbldr;
    }

    /**
     * Process one entry. An Entry is the scope of the context. 
     * @param entry
     * @return  
     */
    protected CharSequence handleEntry(String entry) {
        //System.err.println(entry);
        //List<String> baseEntries = new ArrayList<String>();
        //List<String> featureEntries = new ArrayList<String>();
        Sentence annotated = annotate(entry);

        //getElements(entry, baseEntries, featureEntries);
        //String lines = allPairs(baseEntries, featureEntries);

        CharSequence lines = getLines(annotated);

        return lines;
    }

    protected CharSequence getLines(Sentence sentence) {

        StringBuilder out = new StringBuilder();

        for (IndexToken<?> t : sentence.getKeys()) {

            CharSequence output = config().outputFormatter.getOutput(t);

            //System.err.print(output);
            out.append(output);
        }

        return out;

    }

    abstract protected Sentence annotate(String entry);
    //abstract protected void getElements(String entry, List<String> baseEntries, List<String> featureEntries);
}
