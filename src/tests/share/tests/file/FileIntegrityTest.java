/* $Id: FileIntegrityTest.java,v 1.2 1999/10/14 23:48:54 akv Exp $
 */

/**
 * Tests whether or not file lengths are being correctly set.
 */

package tests.file;

import java.io.*;
import java.net.*;

import org.apache.tools.moo.TestableBase;
import org.apache.tools.moo.TestResult;
import org.apache.tools.moo.URLHelper;

public class FileIntegrityTest extends TestableBase {

    public String getDescription() {
        return "File Integrity Test";
    }

    public TestResult runTest() {
	TestResult testResult = new TestResult();
  	try {
  	    URL url = URLHelper.getURL("/binaries/image1.gif");
  	    URLConnection con = url.openConnection();
  	    con.connect();
	    byte[] conbuf = new byte[con.getContentLength()];
	    InputStream in = con.getInputStream();
	    int total = 0;
	    do {
		total += in.read(conbuf, total, conbuf.length - total);
	    } while (total < conbuf.length);
	    in.close();
	    File f = new File("webpages" + File.separator +
                "binaries" + File.separator + "image1.gif");
	    byte[] filebuf = new byte[con.getContentLength()];
	    BufferedInputStream fin =
		new BufferedInputStream(new FileInputStream(f));
	    total = 0;
	    do {
		total += fin.read(filebuf, total, filebuf.length - total);
	    } while (total < filebuf.length);
	    fin.close();
	    int badbytes = 0;
	    for (int i = 0; i < conbuf.length; i++) {
		int a = conbuf[i];
		int b = filebuf[i];
		if (a != b) {
		    badbytes++;
		}
	    }
	    if (badbytes == 0) {
		testResult.setStatus(true);
		return testResult;
	    } else {
		testResult.setStatus(false);
		testResult.setMessage("Got: " + badbytes + " bad bytes");
		return testResult;
	    }
	    
  	} catch (Exception e) {
  	    testResult.setStatus(false);
  	    testResult.setMessage("Exception: " + e);
  	    return testResult; 
  	}

    }
}
