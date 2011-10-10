package uk.ac.susx.mlcl.util;

import java.io.Serializable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Config implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    @Parameter(names = {"-nc", "--numCores"},
               description = "Number of concurrent worker threads to use.")
    public int numCores = Runtime.getRuntime().availableProcessors();
	
    @Parameter(names = {"-h", "--help"},
            description = "Display this help message.")
    private boolean usageRequested = false;
	
    
    public boolean isUsageRequested() {
        return usageRequested;
    }
    
	public void load(String[] args) {

		JCommander jc = new JCommander(this, args);
		
		try {
			jc.parse();
			
			if (isUsageRequested()) {
				jc.usage();
				if (jc.getParsedCommand() != null) {
					jc.usage(jc.getParsedCommand());
				}
				System.exit(0);
			}
			
		} catch (ParameterException e) {
			e.printStackTrace();
			jc.usage();
		}
	}
	
}
