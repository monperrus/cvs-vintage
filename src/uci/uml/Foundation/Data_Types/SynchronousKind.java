// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products
// must be negotiated with University of California. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.



package uci.uml.Foundation.Data_Types;

import java.util.*;

public class SynchronousKind implements java.io.Serializable {
  public static final SynchronousKind SYNC =
  new SynchronousKind("synchronized");
  public static final SynchronousKind UNSYNC =
  new SynchronousKind("unsynchronized");

  public static final SynchronousKind[] POSSIBLE_SYNCS = { SYNC, UNSYNC };

  protected String _label = null;

  public SynchronousKind(String label) { _label = label; }

  public boolean equals(Object o) {
    if (!(o instanceof SynchronousKind)) return false;
    String oLabel = ((SynchronousKind)o)._label;
    return _label.equals(oLabel);
  }

  public int hashCode() { return _label.hashCode(); }

  public String toString() { return _label.toString(); }

  static final long serialVersionUID = -8653590924173133621L;
}
