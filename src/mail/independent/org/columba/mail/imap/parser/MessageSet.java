package org.columba.mail.imap.parser;

/**
 * @author freddy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MessageSet {

	String messageSetString;
	/**
	 * Constructor for MessageSet.
	 */
	public MessageSet( Object[] uids) {
		messageSetString = parse( uids );		
		
	}
	
	public String getString()
	{
		return messageSetString;
	}
	
	public String parse(Object[] uids) {
		String messageSet = new String();
		StringBuffer messageSetList = new StringBuffer();
		//Message message = null;
		//Integer lastPos;
		int lastPos = 0;
		int newPos = 0;
		char lastChar = 'p';

		for (int i = 0; i < uids.length; i++) {
			System.out.println("parsing="+uids[i]);
			
			Integer uid = new Integer(Integer.parseInt((String) uids[i]));

			if (i == 0) {

				lastPos = (uid).intValue();
				messageSetList.append(lastPos);
				//System.out.println("append_0: "+lastPos);

			} else if (i == 1) {
				newPos = (uid).intValue();
				if (lastPos + 1 == newPos) {
					//messageSetList.append("-");
					messageSetList.append(":");
					lastChar = '-';
					lastPos = (uid).intValue();

					System.out.println("i==1");

					//System.out.println("append_1a: "+ message.getUID());
				} else {
					lastPos = (uid).intValue();
					messageSetList.append("," + lastPos);
					lastChar = 'p';
					//System.out.println("append_1b: "+  message.getUID());
				}
			} else if (i == uids.length - 1) {
				if (lastChar == '-') {
					lastPos = (uid).intValue();
					messageSetList.append(lastPos);
					//System.out.println("append_last_a: "+ message.getUID());
				} else {
					lastPos = (uid).intValue();
					messageSetList.append("," + lastPos);
					//System.out.println("append_last_b: "+ message.getUID());
				}

			} else {
				newPos = (uid).intValue();

				if (lastChar == '-') {
					if (lastPos + 1 == newPos) {
						lastPos = (uid).intValue();
						//System.out.println("append_between_aa: "+ message.getUID());
					} else {
						messageSetList.append(lastPos);
						lastPos = (uid).intValue();
						messageSetList.append("," + lastPos);
						lastChar = 'p';
						//System.out.println("append_between_ab: "+  message.getUID());
					}
				} else {
					if (lastPos + 1 == newPos) {
						messageSetList.append(":");

						lastChar = '-';
						lastPos = (uid).intValue();

						//System.out.println("append :");
						//System.out.println("append_between_ba: "+  message.getUID());
					} else {
						lastPos = (uid).intValue();
						messageSetList.append("," + lastPos);

						//System.out.println("append_between_bb: "+  message.getUID());
					}

				}

			}

		}

		System.out.println("messageSet: "+ messageSetList );
		
		
		return messageSetList.toString();
	}

}
