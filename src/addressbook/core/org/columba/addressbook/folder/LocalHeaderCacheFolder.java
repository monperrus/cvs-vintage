// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

package org.columba.addressbook.folder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.columba.core.command.WorkerStatusController;
import org.columba.core.config.HeaderTableItem;
import org.columba.core.util.SwingWorker;
import org.columba.addressbook.config.AddressbookConfig;
import org.columba.addressbook.config.FolderItem;
import org.columba.addressbook.main.AddressbookInterface;
import org.columba.addressbook.parser.DefaultCardLoader;

/**
 * 
 * LocalHeaderCacheFolder adds a caching mechanism to our
 * locally saved Folder.
 * 
 * The cached HeaderItemList is saved in a binary-file.
 * 
 * HeaderItemList only saves relevant information we need 
 * to quickly show a ContactCard-List in our table
 * 
 * 
 */
public class LocalHeaderCacheFolder extends LocalFolder
{
	/**
	 * 
	 * keeps a list of HeaderItem's we need for the table-view
	 * 
	 */
	protected HeaderItemList headerList;

	/**
	 * 
	 * List which keeps all table-column
	 *  these are the keys we save in our cache
	 * 
	 */
	protected HeaderTableItem headerTableItemList;

	/**
	 * 
	 * binary file named "header"
	 * 
	 */
	protected File headerFile;

	/**
	 * 
	 * boolean variable shows if we already loaded the header-cache from disc
	 * 
	 */
	protected boolean headerCacheAlreadyLoaded;

	public LocalHeaderCacheFolder(
		FolderItem item,
		AddressbookInterface addressbookInterface)
	{
		super(item, addressbookInterface);

		headerList = new HeaderItemList();

		headerFile = new File(directoryFile.toString() + "/header");

		headerTableItemList =
			AddressbookConfig.getAddressbookOptionsConfig().getHeaderTableItem();

		if (headerFile.exists())
		{
			try
			{
				load(null);
				headerCacheAlreadyLoaded = true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();

				headerCacheAlreadyLoaded = false;

			}
		}

		

	}

	public void add(DefaultCard item)
	{
		super.add(item);

		if (item instanceof ContactCard)
			addHeaderItem((ContactCard) item, item.getUid());
		else
			addHeaderItem((GroupListCard) item, item.getUid());
	}

	public boolean exists( String email )
	{
		for ( int i=0; i<getHeaderItemList().count(); i++ )
		{
			HeaderItem item = getHeaderItemList().get(i);
			String address = (String) item.get("email;internet");
			
			if ( email.equals(address) ) return true;
			if ( address != null )
			{
				if ( address.indexOf( email ) != -1 ) return true;
			}
		}
		
		return false;
	}
	/*
	public void add(GroupListCard item)
	{
		super.add( item );
		
		addHeaderItem( item, item.getUid() );
	}
	*/

	public void remove(Object uid)
	{
		headerList.uidRemove(uid);

		super.remove(uid);
	}

	public void modify(DefaultCard card, Object uid)
	{

		super.modify(card, uid);
		
		HeaderItem item = headerList.uidGet(uid);
		
		if ( card instanceof ContactCard )
		{
			
			
			String column;
			Object o;
			for (int j = 0; j < headerTableItemList.count(); j++)
			{

				//item.setUid(uid);
				column = (String) headerTableItemList.getName(j);
				int index = column.indexOf(";");

				if (index != -1)
				{
					String prefix = column.substring(0, index);
					String suffix = column.substring(index + 1, column.length());

					o = card.get(prefix, suffix);
					item.add(column, o);

				}
				else
				{
					System.out.println("column:"+column);
					
					o = card.get(column);
					
					item.add(column, o);
				}

			}

			
			item.add("type","contact");
		}
		else
		{
			item.add("displayname", card.get("displayname"));	
			

			
		}

		/*
		File file =
				new File(folder.directoryFile.toString() + "/" + ((Integer) uid) + ".xml");
				
		try
			{
				//String source = loadString( new Integer(i) );
		
				//ContactCard card = VCardParser.parse(source);
				
				DefaultCardLoader parser = new DefaultCardLoader(file);
				parser.load();
		
				
				if ( parser.isContact() == true )
				{
					ContactCard card = parser.createContactCard();
		
					addHeaderItem(card, new Integer(i) );
				}
				else
				{
					GroupListCard card = parser.createGroupListCard();
					
					addHeaderItem( card, new Integer(i) );
				}
		
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
		*/
	}

	public DataStorage getDataStorageInstance()
	{

		return dataStorage;
	}

	protected int getMessageFileCount()
	{
		File[] list = directoryFile.listFiles();
		return list.length - 1;
	}

	public boolean isHeaderCacheAlreadyLoaded()
	{
		return headerCacheAlreadyLoaded;
	}

	public HeaderItemList getHeaderItemList()
	{
		return headerList;
	}

	public HeaderItemList getHeaderItemList(Object[] uids)
	{
		HeaderItemList l = new HeaderItemList();

		for (int i = 0; i < getHeaderItemList().count(); i++)
		{
			System.out.println("i="+i);
			HeaderItem item = getHeaderItemList().get(i);
			Integer uidInt = (Integer) item.getUid();
			String uid = uidInt.toString();
			
			System.out.println("uid="+uid);
			for (int j = 0; j < uids.length; j++)
			{
				System.out.println("  j="+j);
				System.out.println("uids[j]="+uids[j]);
				if ( uid.equals( (String) uids[j]) )
				{
					
					l.add(item);
					System.out.println("------->uid:" + uid);
					break;
				}
			}
		}

		return l;
	}

	protected void addHeaderItem(ContactCard card, Object uid)
	{
		System.out.println("addheaderItem() contact");
		HeaderItem item = new HeaderItem(HeaderItem.CONTACT);
		String column;
		Object o;
		for (int j = 0; j < headerTableItemList.count(); j++)
		{

			//item.setUid(uid);
			column = (String) headerTableItemList.getName(j);
			int index = column.indexOf(";");

			if (index != -1)
			{
				String prefix = column.substring(0, index);
				String suffix = column.substring(index + 1, column.length());

				o = card.get(prefix, suffix);
				item.add(column, o);

			}
			else
			{
				o = card.get(column);

				item.add(column, o);
			}

		}

		item.setUid(uid);
		item.add("type", "contact");
		item.setFolder(this);
		
		headerList.add(item);
		
		nextUid = ( (Integer)uid ).intValue()+1;
	}

	protected void addHeaderItem(GroupListCard card, Object uid)
	{
		System.out.println("addheaderItem()gouplist");

		/*
		HeaderItem item = new HeaderItem();
		String column;
		Object o;
		for (int j = 0; j < headerTableItemList.count(); j++)
		{
		
			//item.setUid(uid);
			column = (String) headerTableItemList.getName(j);
			int index = column.indexOf(";");
		
			if (index != -1)
			{
				String prefix = column.substring(0, index);
				String suffix = column.substring(index + 1, column.length());
		
				o = card.get(prefix, suffix);
				item.add(column, o);
				
			}
			else
			{
				o = card.get(column);
		
				item.add(column, o);
			}
		
		}
		*/

		HeaderItem item = new HeaderItem(HeaderItem.GROUPLIST);
		item.add("displayname", card.get("displayname"));

		item.setUid(uid);
		item.add("type", "grouplist");
		headerList.add(item);
		
		nextUid = ( (Integer)uid ).intValue()+1;
	}

	public void load(WorkerStatusController worker) throws Exception
	{
		

		FileInputStream istream = new FileInputStream(headerFile.getPath());
		ObjectInputStream p = new ObjectInputStream(istream);

		int capacity = p.readInt();

		

		//int capacity = getMessageFileCount();

		if (capacity != getMessageFileCount())
		{
			// messagebox headercache-file is corrupted
			System.out.println("Messagebox headercache-file is corrupted!");

			recreateIndex();
			return;
		}

		//System.out.println("worker: " + worker);

		Integer uid;

		//System.out.println("Number of Messages : " + capacity);

		if (worker != null)
			worker.setProgressBarMaximum(capacity);

		for (int i = 1; i <= capacity; i++)
		{

			if (worker != null)
				worker.setProgressBarValue(i);

			HeaderItem item = new HeaderItem();
			//ContactCard card = new ContactCard(null, null);

			// read current number of message
			p.readInt();

			uid = (Integer) p.readObject();
			item.setUid(uid);

			String column;
			Object o;
			for (int j = 0; j < headerTableItemList.count(); j++)
			{
				column = (String) headerTableItemList.getName(j);
				//int index = column.indexOf(";");

				o = p.readObject();
				item.add(column, o);

			}

			nextUid = uid.intValue() + 1;

			item.setFolder( this );
			headerList.add(item);

		}

		// close stream
		p.close();

	}

	public void save(WorkerStatusController worker) throws Exception
	{
		

		FileOutputStream istream = new FileOutputStream(headerFile.getPath());
		ObjectOutputStream p = new ObjectOutputStream(istream);

		//int count = getMessageFileCount();
		int count = headerList.count();

		p.writeInt(count);

		//ContactCard card;
		HeaderItem item;
		
		for (int i = 0; i < count; i++)
		{
			p.writeInt(i + 1);

			//card = (ContactCard) super.uidGet(new Integer(i));
			item = headerList.get(i);
			if (item == null)
			{
				System.out.println("there appears to be a card file,");
				System.out.println("but associated card index in folder");
				throw new Exception("addressbookfolder->save() == message is null!");
			}

			p.writeObject(item.getUid());

			String column;
			//HeaderItem item;
			Object o;
			for (int j = 0; j < headerTableItemList.count(); j++)
			{
				column = (String) headerTableItemList.getName(j);

				o = item.get(column);
				p.writeObject(o);

			}
		}

		//p.flush();
		p.close();

	}

	public void recreateIndex()
	{
		System.out.println("recreating index");

		File[] list = directoryFile.listFiles();
		Vector v = new Vector();

		for (int i = 0; i < list.length; i++)
		{
			File file = list[i];
			File renamedFile;
			String name = file.getName();
			int index = name.indexOf("header");

			if (index == -1)
			{
				// message file found
				String number = name;
				//                Integer numberString = new Integer( number );
				//System.out.println("number: "+ number );

				if ((file.exists()) && (file.length() > 0))
				{
					renamedFile = new File(file.getParentFile(), file.getName() + '~');
					file.renameTo(renamedFile);
					System.out.println("renamed file:" + renamedFile);
					v.add(renamedFile);
				}

				//System.out.println("v index: "+ v.indexOf( file ) );
			}
			else
			{
				// header file found
				headerFile.delete();
			}

		}

		for (int i = 0; i < v.size(); i++)
		{
			File file = (File) v.get(i);

			File newFile =
				new File(file.getParentFile(), (new Integer(i)).toString() + ".xml");
			file.renameTo(newFile);

			System.out.println("rename result:" + newFile.toString());
			try
			{
				//String source = loadString( new Integer(i) );

				//ContactCard card = VCardParser.parse(source);

				DefaultCardLoader parser = new DefaultCardLoader(newFile);
				parser.load();

				if (parser.isContact() == true)
				{
					ContactCard card = parser.createContactCard();

					addHeaderItem(card, new Integer(i));
				}
				else
				{
					GroupListCard card = parser.createGroupListCard();

					addHeaderItem(card, new Integer(i));
				}

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}

	}
	
	public HeaderItemList searchPattern( String pattern )
	{
		HeaderItemList searchResult = new HeaderItemList();
		
		for ( int i=0; i<getHeaderItemList().count(); i++ )
		{
			HeaderItem item = (HeaderItem) getHeaderItemList().get(i);
			
			if ( item != null )
			{
				if ( item.matchPattern(pattern) == true ) searchResult.add(item);		
			}
		}
		
		return searchResult;
	}
	
	
}