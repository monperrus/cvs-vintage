// Copyright (c) 2002-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import org.xbill.DNS.utils.*;

/**
 * DS - contains a Delegation Signer record, which acts as a
 * placeholder for KEY records in the parent zone.
 * @see DNSSEC
 *
 * @author David Blacka
 * @author Brian Wellington
 */

public class DSRecord extends Record {

public static final byte SHA1_DIGEST_ID = 1;

private int footprint;
private int alg;
private int digestid;
private byte [] digest;

DSRecord() {}

Record
getObject() {
	return new DSRecord();
}

/**
 * Creates a DS Record from the given data
 * @param footprint The original KEY record's footprint (keyid).
 * @param alg The original key algorithm.
 * @param digestid The digest id code.
 * @param digest A hash of the original key.
 */
public
DSRecord(Name name, int dclass, long ttl, int footprint, int alg,
	 int digestid, byte []  digest)
{
	super(name, Type.DS, dclass, ttl);
	checkU16("footprint", footprint);
	checkU8("alg", alg);
	checkU8("digestid", digestid);
	this.footprint = footprint;
	this.alg = alg;
	this.digestid = digestid;
	this.digest = digest;
}

void
rrFromWire(DNSInput in) throws IOException {
	footprint = in.readU16();
	alg = in.readU8();
	digestid = in.readU8();
	digest = in.readByteArray();
}

void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	footprint = st.getUInt16();
	alg = st.getUInt8();
	digestid = st.getUInt8();
	digest = st.getHex();
}

/**
 * Converts rdata to a String
 */
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(footprint);
	sb.append(" ");
	sb.append(alg);
	sb.append(" ");
	sb.append(digestid);
	if (digest != null) {
		sb.append(" ");
		sb.append(base16.toString(digest));
	}

	return sb.toString();
}	

/**
 * Returns the key's algorithm.
 */
public int
getAlgorithm() {
	return alg;
}

/**
 *  Returns the key's Digest ID.
 */
public int
getDigestID()
{
	return digestid;
}
  
/**
 * Returns the binary hash of the key.
 */
public byte []
getDigest() {
	return digest;
}

/**
 * Returns the key's footprint.
 */
public int
getFootprint() {
	return footprint;
}

void
rrToWire(DataByteOutputStream out, Compression c, boolean canonical) {
	out.writeShort(footprint);
	out.writeByte(alg);
	out.writeByte(digestid);
	if (digest != null)
		out.writeArray(digest);
}

}
