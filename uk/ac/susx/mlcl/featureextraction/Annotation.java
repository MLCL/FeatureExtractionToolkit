package uk.ac.susx.mlcl.featureextraction;

import java.util.Collection;

import javax.naming.OperationNotSupportedException;

public abstract class Annotation<T> {
	
	public abstract T getValue();
	public abstract void setValue(T v);

	public abstract void addToCollection(Collection<String> list, String prefix) throws OperationNotSupportedException;
	
}