package uk.ac.susx.mlcl.featureextraction;

import java.util.List;

public class OutputFormatters {

	public static Class<? extends OutputFormatter> defaultFormatter = NewlineOutput.class;
	
	public static class TabOutput implements OutputFormatter {

		@Override
		public String getOutput(IndexToken<?> key) {
    		StringBuilder out = new StringBuilder();
    		//System.out.println(key);
    		List<String> features = key.getFeature();
			out.append(key.getKey());
    		out.append("\t");
    		for(String feature : features) {
        		//System.out.println(feature);
    			out.append(feature);
    			out.append("\t");
    		}
			out.append("\n");
    		return out.toString();
		}
		
	}
	
	
	public static class NewlineOutput implements OutputFormatter {

		@Override
		public String getOutput(IndexToken<?> key) {
			
			String keyStr = key.getKey();
    		StringBuilder out = new StringBuilder();
    		//System.out.println(key);
    		List<String> features = key.getFeature();
    		
    		for(String feature : features) {
    			out.append(keyStr);
        		out.append("\n");
        		
        		//System.out.println(feature);
    			out.append(feature);
    			out.append("\n");
    		}
    		
    		return out.toString();
		}
		
	}
	
	
}
