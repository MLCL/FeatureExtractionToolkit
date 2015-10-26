============================================================
  Description of uk.ac.susx.mlcl.util resources.
  Author: Hamish Morgan <hamish.morgan@sussex.ac.uk>
============================================================

This folder contains resources used for the testing of CompressorStreamFactory2 class, and Apache commons-compress library. 

There are two source files: 

Wikipedia-Brighton.xml is an xml dump of the wikipedia entry titled "Brighton". This file is used to test the BZip2, GZip, and XZ compression algorithms. The respective compressed variants are created withe system utilities, for comparison with the commons-compress algorithm.

DummyCompressorInputStream.class is a byte-code java class used to test JAR archiving and the compression Pack200 algorithm (which only works on Jar files.) The respective .jar, .jar.pack, and .jar.pack.gz are compared against the common-compress algorithms.

