package org.apache.scaffold.lang;


/**
 * http://www.javaworld.com/javaworld/javatips/jw-javatip91.html
 * @author Terren Suydam 
 * @version $Revision: 1.1 $ $Date: 2001/11/10 11:53:12 $
 */
public class ConfigureException extends NestingException {
	 public ConfigureException() {}
	 public ConfigureException(String msg) {
			 super(msg);
	 }
	 public ConfigureException(Exception nestedException) {
			 super(nestedException);
	 }
	 public ConfigureException(String msg, Exception nestedException){
			 super(msg, nestedException);
	 }
}
