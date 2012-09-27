/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;


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
import uk.ac.susx.mlcl.featureextraction.featureconstraint.NoDeterminersKeyConstraint;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.FeatureConstraint;
import uk.ac.susx.mlcl.lib.io.Files;
import uk.ac.susx.mlcl.lib.MiscUtil;
import uk.ac.susx.mlcl.util.Config;
import uk.ac.susx.mlcl.util.Configurable;
import uk.ac.susx.mlcl.strings.StringSplitter;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;
import uk.ac.susx.mlcl.featureextraction.featurefunction.FeatureFunction;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.PoSKeyConstraint;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.ChunkTagKeyConstraint;
import uk.ac.susx.mlcl.strings.NewlineReader;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * Change Log 07-03-2012
 * ==============================
 * * Added command line usage descriptions and "--help" option.
 * * Added ability to have key constraints in the configuration options
 * 
 * @author Simon Wibberley
 */
public abstract class AbstractParser implements Configurable {

    private static final Logger LOG =
            Logger.getLogger(AbstractParser.class.getName());

    protected abstract static class AbstractParserConfig extends Config {

        private static final long serialVersionUID = 1L;
        
        @Parameter(names = {"-cb", "--useChunkAsBase"},
        description = "produce phrases as base-entries")
        private boolean useChunkAsBase = false;

        @Parameter(names = {"-tb", "--useTokenAsBase"},
        description = "produce tokens as base-entries")
        private boolean useTokenAsBase = false;

        @Parameter(names = {"-cf", "--useChunkAsFeature"},
        description = "produce co-occurent phrases as features")
        private boolean useChunkAsFeature = false;

        @Parameter(names = {"-tf", "--useTokenAsFeature"},
        description = "produce co-occurent tokens as features")
        private boolean useTokenAsFeature = false;

        @Parameter(names = {"-es", "--entrySeparator"},
        description = "base term / feature delimiter for the thesaurus output.")
        private String entrySeparator = "\t";

        @Parameter(names = {"-op", "--outPath"},
        required = true)
        private String outPath;

        @Parameter(names = {"-ip", "--inPath"},
        required = true)
        private String inPath;

        @Parameter(names = {"-r", "--recursive"},
        description = "Descend into subfolder of the input path.")
        private boolean recursive = false;

        @Parameter(names = {"-is", "--inSuffix"},
        description = "Only read files with the given suffix.",
        required = true)
        private String inSuffix;

        @Parameter(names = {"-z", "--gzip"})
        private boolean useGzip = false;

        @Parameter(names = {"-of", "--outputFormatter"},
        description = "class that specifies the output format.",
        converter = FormatterConverter.class)
        private OutputFormatter outputFormatter = new TabOutputFormatter();

        @Parameter(names = {"-v", "--verbose"})
        private boolean verbose = false;
        
        @Parameter (names = {"-ep","--posTagToExclude"},
        description = "Excludes from the output all entry tokens with a given PoS tag")
        private String exPoS = null;

        @Parameter(names = {"-l", "--limit"}, description = "Maximum number of documents (not files) to process.")
        private int limit = 0;
        
        @Parameter (names = {"-nl", "--useNounsOnLeft"},
        description = "Use nouns on left of another noun as features")
        private int useNounsOnLeft = 0;
        
        @Parameter (names = {"-nr", "--useNounsOnRight"},
        description = "Use nouns on left of another noun as features")
        private int useNounsOnRight = 0;
                
        @Parameter (names = {"-al","--useAdjectivesLeft"},
        description = "Use Adjective on left as a feature")
        private int useAdjectiveLeft =  0;
        
        @Parameter (names = {"-ar","--useAdjectivesRight"},
        description = "Use Adjective on right as a feature")
        private int useAdjectiveRight =  0;
        
        @Parameter (names = {"-pr", "--usePrepositionOnRight"},
        description = "Use a preposition on the right as a feature if it is immediately to the right")
        private int usePrepositionOnRight = 0;
        
        @Parameter (names = {"-pl", "--usePrepositionOnLeft"},
        description = "Use a preposition on the left as a feature if it is immediately to the right")
        private int usePrepositionOnLeft = 0;
        
        @Parameter (names = {"-posl", "--usePoSLeft"},
        description = "Use PoS tag on left as feature")
        private int usePoSTagLeft = 0;
        
        @Parameter (names = {"-posr", "--usePoSRight"},
        description = "Use PoS tag on the right as feature")
        private int usePoSTagRight = 0;

        public String getEntrySeparator() {
            return entrySeparator;
        }

        public String getOutPath() {
            return outPath;
        }

        public String getInPath() {
            return inPath;
        }

        public boolean isRecursive() {
            return recursive;
        }

        public String getInSuffix() {
            return inSuffix;
        }

        public boolean isUseGzip() {
            return useGzip;
        }

        public OutputFormatter getOutputFormatter() {
            return outputFormatter;
        }

        public boolean isVerbose() {
            return verbose;
        }

        public int getLimit() {
            return limit;
        }
        
        
        public boolean isUseChunkAsBase() {
            return useChunkAsBase;
        }

        public boolean isUseTokenAsBase() {
            return useTokenAsBase;
        }

        public boolean isUseChunkAsFeature() {
            return useChunkAsFeature;
        }

        public boolean isUseTokenAsFeature() {
            return useTokenAsFeature;
        }
        
        public int getNounsLeft(){
            return useNounsOnLeft;
        }
        
        public int getNounsRight(){
            return useNounsOnRight;
        }
        
        public int getAdjectiveLeft(){
            return useAdjectiveLeft;
        }
        
        public int getAdjectiveRight(){
            return useAdjectiveRight;
        }
        
        public int getPoSLeft(){
            return usePoSTagLeft;
        }
        
        public int getPoSRight(){
            return usePoSTagRight;
        }
        
        public int getPrepositionLeft(){
            return usePrepositionOnLeft;
        }
        
        public int getPrepositionRight(){
            return usePrepositionOnRight;
        }
               
        public String getExPoSCon(){
            return exPoS;
        }
        
        public boolean isUseNounsOnRight()
        {
            return useNounsOnRight != 0;
        }
        
        public boolean isUseNounsOnLeft()
        {
            return useNounsOnLeft != 0;
        }
                
        public boolean isUseAdjectiveLeft(){
            return useAdjectiveLeft != 0;
        }
        
        public boolean isUseAdjectiveRight(){
            return useAdjectiveRight != 0;
        }
        
        public boolean isUsePoSLeft(){
            return usePoSTagLeft != 0;
        }
        
        public boolean isUsePoSRight(){
            return usePoSTagRight != 0;
        }
                
        public boolean isUsePrepositionLeft()
        {
            return usePrepositionOnLeft != 0;
        }
        
        public boolean isUsePrepositionRight()
        {
            return usePrepositionOnRight != 0;
        }
        
    }

    private BufferedWriter outFile;

    private StringSplitter splitter;

    private final AtomicInteger count = new AtomicInteger();

    private FeatureFactory featureFactory;

    private ExecutorService exec;

    private Semaphore throttle;

    private Queue<Future<Void>> futures;
    
    private List<FeatureConstraint> constraints;

    public StringSplitter getSplitter() {
        return splitter;
    }
    
    public List<FeatureConstraint> getConstraints(){
        return constraints;
    }

    public void setSplitter(StringSplitter splitter) {
        this.splitter = splitter;
    }

    protected String getOutPath() {
        return config().getOutPath();
    }
    
    /*
     * Used when creating the output file path to prevent already existing 
     * files from being overwritten.
     */
    protected String enumeratePath(String outPath, int enu) {
        String path = "";
        if(enu > 0){
            path = outPath + "-(" + enu + ")";
        }
        else{
            path = outPath;
        }
        if(new File(path).exists()){
            enu++;
            outPath = enumeratePath(outPath, enu);
        }
        else{
            if(enu > 0){
                outPath = outPath + "-(" + enu + ")";
            }
        }
        return outPath;
    }

    public void parse() {

        System.err.println("Building file list...");

        List<String> files = Files.getFileList(
                config().getInPath(), config().getInSuffix(),
                false, false);
        
        for(String file : files){
            System.err.println(file);
        }

        System.err.println("File list built.");
        
        constraints = new ArrayList<FeatureConstraint>();
        setKeyConstraints();

        featureFactory = buildFeatureFactory();

        parse(config().getInPath(), files);

    }

    public FeatureFactory getFeatureFactory() {
        return featureFactory;
    }

    private void parse(String prefix, List<String> folders) {
        handleOutputPre();

        initThreads();
        
        for (String folderName : folders){
            final File folder = new File(folderName);
            final List<String> files;
            if(folder.isDirectory()){
                files = Files.getFileList(
                folderName, config().getInSuffix(),
                false, config().isRecursive());
            }
            else { 
                files = new ArrayList<String>();
                files.add(folderName);
            }
            for (String fileName : files) {
                

                if (config().getLimit() > 0 && count.get() > config().getLimit()) {
                    break;
                }

                File file = new File(fileName);
                System.err.println("Processing file: " + file);

                try {
                    CharSequence text = Files.getText(file, false, true);
                    parse(text, false);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                }

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
        }
        cleanupThreads();

        handleOutputPost();
    }

    private void parse(final CharSequence input, final boolean prePost) {
        if (prePost) {
            handleOutputPre();
        }

        //final List<String> entries = getSplitter().split(input.toString());
        final NewlineReader reader = new NewlineReader(input);
        //for (final String entry : entries) {
        while(reader.hasLine()){
            final String entry = reader.readLine();
            try {
                throttle.acquire();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, null, e);
            }

            if (config().getLimit() > 0 && count.get() > config().getLimit()) {
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
        exec = Executors.newFixedThreadPool(config().getNumCores());
        throttle = new Semaphore(3 * config().getNumCores());
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

    protected CharSequence getLines(Sentence sentence) {

        StringBuilder out = new StringBuilder();
        
        for (IndexToken<?> t : sentence.getKeys()) {
            CharSequence output = "";
            if(t.getKey() != null){
                output = config().getOutputFormatter().getOutput(t);
            }
            
            out.append(output);
        }

        return out;
    }
    
    
    public void applyFeatureFactory(FeatureFactory featureFactory, Sentence s) {

        Collection<FeatureFunction> fns = featureFactory.getAllFeatures();
        for (IndexToken<?> key : s.getKeys()) {
            boolean accept = true;
            for(FeatureConstraint constraint : constraints){
                if(!constraint.accept(s, key, key.getSpan().left)){
                    accept = false;
                    break;
                }
            }
            if(accept){
                CharSequence keyStr = s.getKeyString(config().getExPoSCon(),key);
                List<CharSequence> featureList = new ArrayList<CharSequence>();
                key.setKey(keyStr);
                
                for (FeatureFunction f : fns) {
                    Collection<String> features = f.extractFeatures(s, key);
                    featureList.addAll(features);
                }

                key.setFeatures(featureList);
            }
        }
    }

    protected abstract Sentence annotate(String entry);
    
    protected abstract void setKeyConstraints();

    protected abstract FeatureFactory buildFeatureFactory();

    protected abstract AbstractParserConfig config();

}
