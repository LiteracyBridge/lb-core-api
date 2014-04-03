package org.literacybridge.stats;

/**
 * Defines all the operations and file structures for the statistics coming back from the talking books.  This package and
 * its sub-packages do NOT depend on other services or processes running.  Other layers above this one are responsible for
 * tasks such as monitoring Dropbox or putting results into databases.
 *
 * The purpose of this package and its children is to create re-usable classes to process the base file formats, without tieing
 * them to the import process.
 *
 * The main class for processing these results is the DirectoryIterator class that will run through a properly built and
 * unzipped stats package (as defined in the
 * <a href="https://docs.google.com/document/d/12Q0a7x15FqeZ4ys0gYy4O2MtWYrvGDUegOXwlsG9ZQY">Stats Reporting Formats and Protocols</a>
 * Google Doc)
 **/