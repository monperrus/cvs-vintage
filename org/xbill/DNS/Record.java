// Copyright (c) 1999 Brian Wellington (bwelling@xbill.org)
// Portions Copyright (c) 1999 Network Associates, Inc.

package org.xbill.DNS;

import java.io.*;
import java.text.*;
import java.lang.reflect.*;
import java.util.*;
import org.xbill.DNS.utils.*;

/**
 * A generic DNS resource record.  The specific record types extend this class.
 * A record contains a name, type, class, and rdata.
 *
 * @author Brian Wellington
 */

public abstract class Record implements Cloneable, Comparable {

protected Name name;
protected short type, dclass;
protected int ttl;

private static final Record [] knownRecords = new Record[256];
private static final Class [] emptyClassArray = new Class[0];
private static final Object [] emptyObjectArray = new Object[0];

private static final DecimalFormat byteFormat = new DecimalFormat();

static {
	byteFormat.setMinimumIntegerDigits(3);
}

protected
Record() {}

Record(Name name, short type, short dclass, int ttl) {
	if (!name.isAbsolute())
		throw new RelativeNameException(name);
	this.name = name;
	this.type = type;
	this.dclass = dclass;
	this.ttl = ttl;
}

private static final Record
getTypedObject(short type) {
	if (type < 0 || type > knownRecords.length)
		return UNKRecord.getMember();
	if (knownRecords[type] != null)
		return knownRecords[type];
	try {
		String s = Record.class.getName();
		/*
		 * Remove "Record" from the end and construct the new
		 * class name.
		 */
		Class c = Class.forName(s.substring(0, s.length() - 6) +
					Type.string(type) + "Record");
		Method m = c.getDeclaredMethod("getMember", emptyClassArray);
		knownRecords[type] = (Record) m.invoke(null, emptyObjectArray);
	}
	catch (ClassNotFoundException e) {
		/* This is normal; do nothing */
	}
	catch (InvocationTargetException e) {
		if (Options.check("verbose"))
			System.err.println(e);
	}
	catch (NoSuchMethodException e) {
		if (Options.check("verbose"))
			System.err.println(e);
	}
	catch (IllegalAccessException e) {
		if (Options.check("verbose"))
			System.err.println(e);
	}
	if (knownRecords[type] == null)
		knownRecords[type] = UNKRecord.getMember();
	return knownRecords[type];
}

/**
 * Converts the type-specific RR to wire format - must be overriden
 */
abstract Record rrFromWire(Name name, short type, short dclass, int ttl,
			   int length, DataByteInputStream in)
throws IOException;

private static Record
newRecord(Name name, short type, short dclass, int ttl, int length,
	  DataByteInputStream in) throws IOException
{
	Record rec;
	int recstart;
	if (in == null)
		recstart = 0;
	else
		recstart = in.getPos();

	rec = getTypedObject(type);
	rec = rec.rrFromWire(name, type, dclass, ttl, length, in);
	if (in != null && in.getPos() - recstart != length)
		throw new IOException("Invalid record length");
	return rec;
}

/**
 * Creates a new record, with the given parameters.
 * @param name The owner name of the record.
 * @param type The record's type.
 * @param dclass The record's class.
 * @param ttl The record's time to live.
 * @param length The length of the record's data.
 * @param data The rdata of the record, in uncompressed DNS wire format.  Only
 * the first length bytes are used.
 */
public static Record
newRecord(Name name, short type, short dclass, int ttl, int length,
	  byte [] data)
{
	if (!name.isAbsolute())
		throw new RelativeNameException(name);
	DataByteInputStream dbs;
	if (data != null)
		dbs = new DataByteInputStream(data);
	else
		dbs = null;
	try {
		return newRecord(name, type, dclass, ttl, length, dbs);
	}
	catch (IOException e) {
		return null;
	}
}

/**
 * Creates a new record, with the given parameters.
 * @param name The owner name of the record.
 * @param type The record's type.
 * @param dclass The record's class.
 * @param ttl The record's time to live.
 * @param data The complete rdata of the record, in uncompressed DNS wire
 * format.
 */
public static Record
newRecord(Name name, short type, short dclass, int ttl, byte [] data) {
	return newRecord(name, type, dclass, ttl, data.length, data);
}

/**
 * Creates a new empty record, with the given parameters.
 * @param name The owner name of the record.
 * @param type The record's type.
 * @param dclass The record's class.
 * @param ttl The record's time to live.
 * @return An object of a subclass of Record
 */
public static Record
newRecord(Name name, short type, short dclass, int ttl) {
	return newRecord(name, type, dclass, ttl, 0, (byte []) null);
}

/**
 * Creates a new empty record, with the given parameters.  This method is
 * designed to create records that will be added to the QUERY section
 * of a message.
 * @param name The owner name of the record.
 * @param type The record's type.
 * @param dclass The record's class.
 * @return An object of a subclass of Record
 */
public static Record
newRecord(Name name, short type, short dclass) {
	return newRecord(name, type, dclass, 0, 0, (byte []) null);
}

static Record
fromWire(DataByteInputStream in, int section) throws IOException {
	short type, dclass;
	int ttl;
	short length;
	Name name;
	Record rec;
	int start;

	start = in.getPos();

	name = new Name(in);
	type = in.readShort();
	dclass = in.readShort();

	if (section == Section.QUESTION)
		return newRecord(name, type, dclass);

	ttl = in.readInt();
	length = in.readShort();
	if (length == 0)
		return newRecord(name, type, dclass, ttl);
	rec = newRecord(name, type, dclass, ttl, length, in);
	return rec;
}

/**
 * Builds a Record from DNS uncompressed wire format.
 */
public static Record
fromWire(byte [] b, int section) throws IOException {
	DataByteInputStream in = new DataByteInputStream(b);
	return fromWire(in, section);
}

void
toWire(DataByteOutputStream out, int section, Compression c) {
	int start = out.getPos();
	name.toWire(out, c);
	out.writeShort(type);
	out.writeShort(dclass);
	if (section == Section.QUESTION)
		return;
	out.writeInt(ttl);
	int lengthPosition = out.getPos();
	out.writeShort(0); /* until we know better */
	rrToWire(out, c, false);
	out.writeShortAt(out.getPos() - lengthPosition - 2, lengthPosition);
}

/**
 * Converts a Record into DNS uncompressed wire format.
 */
public byte []
toWire(int section) {
	DataByteOutputStream out = new DataByteOutputStream();
	toWire(out, section, null);
	return out.toByteArray();
}

void
toWireCanonical(DataByteOutputStream out) {
	name.toWireCanonical(out);
	out.writeShort(type);
	out.writeShort(dclass);
	out.writeInt(ttl);
	int lengthPosition = out.getPos();
	out.writeShort(0); /* until we know better */
	rrToWire(out, null, true);
	out.writeShortAt(out.getPos() - lengthPosition - 2, lengthPosition);
}

/**
 * Converts a Record into canonical DNS uncompressed wire format (all names are
 * converted to lowercase).
 */
public byte []
toWireCanonical() {
	DataByteOutputStream out = new DataByteOutputStream();
	toWireCanonical(out);
	return out.toByteArray();
}

/**
 * Converts the rdata in a Record into canonical DNS uncompressed wire format
 * (all names are converted to lowercase).
 */
public byte []
rdataToWireCanonical() {
	DataByteOutputStream out = new DataByteOutputStream();
	rrToWire(out, null, true);
	return out.toByteArray();
}

public abstract String rdataToString();

/**
 * Converts a Record into a String representation
 */
public String
toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(name);
	if (sb.length() < 8)
		sb.append("\t");
	if (sb.length() < 16)
		sb.append("\t");
	sb.append("\t");
	if (Options.check("BINDTTL"))
		sb.append(TTL.format(ttl));
	else
		sb.append((long)ttl & 0xFFFFFFFFL);
	sb.append("\t");
	if (dclass != DClass.IN || !Options.check("noPrintIN")) {
		sb.append(DClass.string(dclass));
		sb.append("\t");
	}
	sb.append(Type.string(type));
	sb.append("\t");
	sb.append(rdataToString());
	return sb.toString();
}

/**
 * Converts the text format of an RR to the internal format - must be overriden
 */
abstract Record
rdataFromString(Name name, short dclass, int ttl,
		Tokenizer st, Name origin)
throws IOException;

/**
 * Returns a concatenation of the remaining strings from a Tokenizer,
 * or throws an IOException.
 */
protected static String
remainingStrings(Tokenizer st) throws IOException {
	StringBuffer sb = null;
	while (true) {
		Tokenizer.Token t = st.get();
		if (!t.isString())
			break;
		if (sb == null)
			sb = new StringBuffer();
		sb.append(t.value);
	}
	st.unget();
	if (sb == null)
		return null;
	return sb.toString();
}

/**
 * Converts a String into a byte array.
 */
protected static byte []
byteArrayFromString(String s) {
	byte [] b = s.getBytes();
	boolean escaped = false;
	int escapes = 0;

	for (int i = 0; i < b.length; i++) {
		if (escaped)
			escaped = false;
		else if (b[i] == '\\') {
			escaped = true;
			escapes++;
		}
	}
	if (escapes > 0) {
		byte [] compact = new byte[b.length - escapes];
		escaped = false;
		for (int i = 0, j = 0; i < b.length; i++) {
			if (escaped)
				escaped = false;
			else if (b[i] == '\\') {
				escaped = true;
				continue;
			}
			compact[j++] = b[i];
		}
		b = compact;
	}
	return b;
}

/**
 * Converts a byte array into a String.
 */
protected static String
byteArrayToString(byte [] array, boolean quote) {
	StringBuffer sb = new StringBuffer();
	if (quote)
		sb.append('"');
	for (int i = 0; i < array.length; i++) {
		short b = (short)(array[i] & 0xFF);
		if (b < 0x20 || b >= 0x7f) {
			sb.append('\\');
			sb.append(byteFormat.format(b));
		} else if (b == '"' || b == ';' || b == '\\') {
			sb.append('\\');
			sb.append((char)b);
		} else
			sb.append((char)b);
	}
	if (quote)
		sb.append('"');
	return sb.toString();
}

/**
 * Builds a new Record from its textual representation
 * @param name The owner name of the record.
 * @param type The record's type.
 * @param dclass The record's class.
 * @param ttl The record's time to live.
 * @param st A tokenizer containing the textual representation of the rdata.
 * @param origin The default origin to be appended to relative domain names.
 * @return The new record
 * @throws IOException The text format was invalid.
 */
public static Record
fromString(Name name, short type, short dclass, int ttl, Tokenizer st,
	   Name origin)
throws IOException
{
	Record rec;

	if (!name.isAbsolute())
		throw new RelativeNameException(name);

	Tokenizer.Token t = st.get();
	if (t.type == Tokenizer.IDENTIFIER && t.value.equals("\\#")) {
		int length = st.getUInt16();
		String s = remainingStrings(st);
		byte [] data = base16.fromString(s);
		if (length != data.length)
			throw st.exception("invalid unknown RR encoding: " +
					   "length mismatch");
		DataByteInputStream in = new DataByteInputStream(data);
		return newRecord(name, type, dclass, ttl, length, in);
	}
	st.unget();
	rec = getTypedObject(type);
	return rec.rdataFromString(name, dclass, ttl, st, origin);
}

/**
 * Builds a new Record from its textual representation
 * @param name The owner name of the record.
 * @param type The record's type.
 * @param dclass The record's class.
 * @param ttl The record's time to live.
 * @param st The textual representation of the rdata.
 * @param origin The default origin to be appended to relative domain names.
 * @return The new record
 * @throws IOException The text format was invalid.
 */
public static Record
fromString(Name name, short type, short dclass, int ttl, String s, Name origin)
throws IOException
{
	return fromString(name, type, dclass, ttl, new Tokenizer(s), origin);
}

/**
 * Returns the record's name
 * @see Name
 */
public Name
getName() {
	return name;
}

/**
 * Returns the record's type
 * @see Type
 */
public short
getType() {
	return type;
}

/**
 * Returns the type of RRset that this record would belong to.  For all types
 * except SIGRecord, this is equivalent to getType().
 * @return The type of record, if not SIGRecord.  If the type is SIGRecord,
 * the type covered is returned.
 * @see Type
 * @see RRset
 * @see SIGRecord
 */
public short
getRRsetType() {
	if (type == Type.SIG) {
		SIGRecord sig = (SIGRecord) this;
		return sig.getTypeCovered();
	}
	return type;
}

/**
 * Returns the record's class
 */
public short
getDClass() {
	return dclass;
}

/**
 * Returns the record's TTL
 */
public int
getTTL() {
	return ttl;
}

/**
 * Converts the type-specific RR to wire format - must be overriden
 */
abstract void
rrToWire(DataByteOutputStream out, Compression c, boolean canonical);

/**
 * Determines if two Records are identical
 */
public boolean
equals(Object arg) {
	if (arg == null || !(arg instanceof Record))
		return false;
	Record r = (Record) arg;
	if (type != r.type || dclass != r.dclass || !name.equals(r.name))
		return false;
	byte [] array1 = rdataToWireCanonical();
	byte [] array2 = r.rdataToWireCanonical();
	return Arrays.equals(array1, array2);
}

/**
 * Generates a hash code based on the Record's data
 */
public int
hashCode() {
	byte [] array = toWireCanonical();
	return array.hashCode();
}

private Record
cloneRecord() {
	try {
		return (Record) clone();
	}
	catch (CloneNotSupportedException e) {
		throw new IllegalStateException();
	}
}

/**
 * Creates a new record identical to the current record, but with a different
 * name.  This is most useful for replacing the name of a wildcard record.
 */
public Record
withName(Name name) {
	if (!name.isAbsolute())
		throw new RelativeNameException(name);
	Record rec = cloneRecord();
	rec.name = name;
	return rec;
}

/**
 * Creates a new record identical to the current record, but with a different
 * class.  This is most useful for dynamic update.
 */
Record
withDClass(short dclass) {
	Record rec = cloneRecord();
	rec.dclass = dclass;
	return rec;
}

/**
 * Compares this Record to another Object.
 * @param o The Object to be compared.
 * @return The value 0 if the argument is a record equivalent to this record;
 * a value less than 0 if the argument is less than this record in the
 * canonical ordering, and a value greater than 0 if the argument is greater
 * than this record in the canonical ordering.  The canonical ordering
 * is defined to compare by name, class, type, and rdata.
 * @throws ClassCastException if the argument is not a Record.
 */
public int
compareTo(Object o) {
	Record arg = (Record) o;

	if (this == arg)
		return (0);

	int n = name.compareTo(arg.name);
	if (n != 0)
		return (n);
	n = dclass - arg.dclass;
	if (n != 0)
		return (n);
	n = type - arg.type;
	if (n != 0)
		return (n);
	byte [] rdata1 = rdataToWireCanonical();
	byte [] rdata2 = arg.rdataToWireCanonical();
	for (int i = 0; i < rdata1.length && i < rdata2.length; i++) {
		n = (rdata1[i] & 0xFF) - (rdata2[i] & 0xFF);
		if (n != 0)
			return (n);
	}
	return (rdata1.length - rdata2.length);
}

/**
 * Returns the name for which additional data processing should be done
 * for this record.  This can be used both for building responses and
 * parsing responses.
 * @return The name to used for additional data processing, or null if this
 * record type does not require additional data processing.
 */
public Name
getAdditionalName() {
	return null;
}

}
