// Copyright (c) 1999 Brian Wellington (bwelling@xbill.org)
// Portions Copyright (c) 1999 Network Associates, Inc.

package org.xbill.DNS;

/**
 * Constants and functions relating to DNSSEC (algorithm constants).
 * DNSSEC provides authentication for DNS information.  RRsets are
 * signed by an appropriate key, and a SIG record is added to the set.
 * A KEY record is obtained from DNS and used to validate the signature,
 * The KEY record must also be validated or implicitly trusted - to
 * validate a key requires a series of validations leading to a trusted
 * key.  The key must also be authorized to sign the data.
 * @see SIGRecord
 * @see KEYRecord
 * @see RRset
 *
 * @author Brian Wellington
 */


public class DNSSEC {

public static final byte RSAMD5 = 1;
public static final byte RSA = RSAMD5;
public static final byte DH = 2;
public static final byte DSA = 3;
public static final byte RSASHA1 = 5;

public static final byte Failed = -1;
public static final byte Insecure = 0;
public static final byte Secure = 1;

}
