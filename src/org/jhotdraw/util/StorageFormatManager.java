/*
 * @(#)StorageFormatManager.java 5.2
 *
 */
 
package CH.ifa.draw.util;

import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import CH.ifa.draw.framework.Drawing;

/**
 * The StorageFormatManager is a contains StorageFormats.
 * It is not a Singleton because it could be necessary to deal with different
 * format managers, e.g. one for importing Drawings, one for exporting Drawings.
 * If one StorageFormat matches the file extension of the Drawing file, then this
 * StorageFormat can be used to store or restore the Drawing.
 *
 * @see StorageFormat
 * @author Wolfram Kaiser
 */
public class StorageFormatManager {

	/**
	 * Vector containing all registered storage formats
	 */
	private Vector myStorageFormats;
	
	/**
	 * Default storage format that should be selected in a javax.swing.JFileChooser
	 */
	private StorageFormat myDefaultStorageFormat;
	
	/**
	 * Create a new StorageFormatManager.
	 */
	public StorageFormatManager() {
		myStorageFormats = new Vector();
	}
	
	/**
	 * Add a StorageFormat that should be supported by this StorageFormatManager.
	 *
	 * @param newStorageFormat new StorageFormat to be supported
	 */
	public void addStorageFormat(StorageFormat newStorageFormat) {
		myStorageFormats.add(newStorageFormat);
	}

	/**
	 * Remove a StorageFormat that should no longer be supported by this StorageFormatManager.
	 * The StorageFormat is excluded in when search for a StorageFormat.
	 *
	 * @param oldStorageFormat old StorageFormat no longer to be supported
	 */
	public void removeStorageFormat(StorageFormat oldStorageFormat) {
		myStorageFormats.remove(oldStorageFormat);
	}
	
	/**
	 * Test, whether a StorageFormat is supported by this StorageFormat
	 */
	public boolean containsStorageFormat(StorageFormat checkStorageFormat){
		return myStorageFormats.contains(checkStorageFormat);
	}
	
	/**
	 * Set a StorageFormat as the default storage format which is selected in a
	 * javax.swing.JFileChooser. The default storage format must be already
	 * added with addStorageFormat. Setting the default storage format to null
	 * does not automatically remove the StorageFormat from the list of
	 * supported StorageFormats.
	 *
	 * @param newDefaultStorageFormat StorageFormat that should be selected in a JFileChooser
	 */
	public void setDefaultStorageFormat(StorageFormat newDefaultStorageFormat) {
		myDefaultStorageFormat = newDefaultStorageFormat;
	}
	
	/**
	 * Return the StorageFormat which is used as selected file format in a javax.swing.JFileChooser
	 *
	 * @return default storage format
	 */
	public StorageFormat getDefaultStorageFormat() {
		return myDefaultStorageFormat;
	}
	
	/**
	 * Register all FileFilters supported by StorageFormats
	 *
	 * @param fileChooser javax.swing.JFileChooser to which FileFilters are added
	 */
	public void registerFileFilters(JFileChooser fileChooser) {
		Iterator formatsIterator = myStorageFormats.iterator();
		while (formatsIterator.hasNext()) {
			fileChooser.addChoosableFileFilter(((StorageFormat)formatsIterator.next()).getFileFilter());
		}

		// set a current activated file filter if a default storage Format has been defined
		if (getDefaultStorageFormat() != null) {
			fileChooser.setFileFilter(getDefaultStorageFormat().getFileFilter());
		}
	}

	/**
	 * Find a StorageFormat that can be used according to a FileFilter to store a Drawing
	 * in a file or restore it from a file respectively.
	 *
	 * @param findFileFilter FileFilter used to identify a StorageFormat
	 * @return StorageFormat, if a matching file extension could be found, false otherwise
	 */
	public StorageFormat findStorageFormat(FileFilter findFileFilter) {
		Iterator formatsIterator = myStorageFormats.iterator();
		StorageFormat currentStorageFormat = null;
		while (formatsIterator.hasNext()) {
			currentStorageFormat = (StorageFormat)formatsIterator.next();
			if (currentStorageFormat.getFileFilter().equals(findFileFilter)) {
				return currentStorageFormat;
			}
		}
		
		return null;
	}
}