/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

import uk.ac.susx.mlcl.featureextraction.features.FeatureFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.beust.jcommander.Parameter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import uk.ac.susx.mlcl.util.Config;
import uk.ac.susx.mlcl.util.Configurable;
import uk.ac.susx.mlcl.strings.StringSplitter;
import uk.ac.susx.mlcl.util.MiscUtil;
import uk.ac.susx.mlcl.util.Paths;

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

        @Parameter(names = {"-z", "--gzip"})
        public boolean useGzip = false;

        @Parameter(names = {"-of", "--outputFormatter"},
        description = "class that specifies the output format.",
        converter = OutputFormatterConverter.class)
        public OutputFormatter outputFormatter = new TabOutputFormatter();

        @Parameter(names = {"-v", "--verbose"})
        public boolean verbose = false;

        @Parameter(names = {"-l", "--limit"}, description = "Maximum number of documents (not files) to process.")
        public int limit = 0;

    }

    protected BufferedWriter outFile;

    protected StringSplitter splitter;

    final AtomicInteger count = new AtomicInteger();

    private FeatureFactory featureFactory;

    private ExecutorService exec;

    private Semaphore throttle;

    private Queue<Future<Void>> futures;

    protected String getOutPath() {
        return config().outPath;
    }

    public void parse() {

        System.err.println("Building file list...");

        List<String> files = Paths.getFileList(
                config().inPath, config().inSuffix,
                false, config().recursive);

        System.err.println("File list built.");

        featureFactory = buildFeatureFactory();

        parse(config().inPath, files);

    }

    public FeatureFactory getFeatureFactory() {
        return featureFactory;
    }

    private void parse(String prefix, List<String> files) {
        handleOutputPre();

        initThreads();

        for (String file : files) {

            if (config().limit > 0 && count.get() > config().limit) {
                break;
            }

            System.err.println("Processing file: " + new File(prefix, file).toString());

            parse(Paths.getText(new File(prefix, file).toString(), false, true),
                  false);

            try {
                while (!futures.isEmpty()) {
                    futures.poll().get();
                }
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, null, e);
            } catch (ExecutionException e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }

        cleanupThreads();

        handleOutputPost();
    }

    private void parse(final CharSequence input, final boolean prePost) {
        if (prePost) {
            handleOutputPre();
        }
        final List<String> entries = splitter.split(input.toString());

        for (final String entry : entries) {
            try {
                throttle.acquire();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, null, e);
            }

            if (config().limit > 0 && count.get() > config().limit) {
                throttle.release();
                break;
            }

            final int entryid = count.addAndGet(1);
            if (entryid % 100 == 0) {
                System.err.println("Queing entry " + entryid);
                System.err.println(MiscUtil.memoryInfoString());
            }

            futures.offer(exec.submit(new Callable<Void>() {

                @Override
                public final Void call() throws IOException {
                    try {
                        final CharSequence output = handleEntry(entry);

                        handleOutput(output);

                    } catch (InvalidEntryException e) {
                        // just ignore singlton or empty sentences
                    } finally {
                        throttle.release();
                    }
                    return null;

                }

            }));
        }

        if (prePost) {
            handleOutputPost();
        }
    }

    private void initThreads() {
        if (exec != null || throttle != null || futures != null) {
            throw new IllegalStateException();
        }
        exec = Executors.newFixedThreadPool(config().numCores);
        throttle = new Semaphore(3 * config().numCores);
        futures = new ArrayDeque<Future<Void>>();
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

    private void handleOutputPre() {
        String outPath = getOutPath();
        try {

            outFile = new BufferedWriter(new FileWriter(outPath));


        } catch (IOException e) {
            System.err.println(e);
        }
        System.err.println("opening " + outPath);
    }

    private void handleOutputPost() {

        try {
            outFile.close();
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private void handleOutput(final CharSequence output) throws IOException {
        if (output.length() == 0) {
            return;
        }
        synchronized (outFile) {
            outFile.append(output);
//            System.out.println(output);
        }


    }

    /**
     * Process one entry. An Entry is the scope of the context. 
     * @param entry
     * @return  
     */
    private CharSequence handleEntry(String entry) {

        Sentence annotated = annotate(entry);


        if (annotated.isEmpty()) {
            throw new InvalidEntryException("empty sentence!");
        } else if (annotated.size() <= 1) {
            throw new InvalidEntryException("single entry sentence!");
        }

        CharSequence lines = getLines(annotated);

        return lines;
    }

    private CharSequence getLines(Sentence sentence) {

        StringBuilder out = new StringBuilder();

        for (IndexToken<?> t : sentence.getKeys()) {

            CharSequence output = config().outputFormatter.getOutput(t);

            out.append(output);
        }

        return out;

    }

    protected abstract Sentence annotate(String entry);

    protected abstract FeatureFactory buildFeatureFactory();

    protected abstract ParserConfig config();

}
