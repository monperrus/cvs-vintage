/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.chainsaw;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.ProgressMonitor;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;


/**
 * A CyclicBuffer implementation of the EventContainer.
 *
 * NOTE:  This implementation prevents duplicate rows from being added to the model.
 *
 * Ignoring duplicates was added to support receivers which may attempt to deliver the same
 * event more than once but can be safely ignored (for example, the database receiver
 * when set to retrieve in a loop).
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
class ChainsawCyclicBufferTableModel extends AbstractTableModel
  implements EventContainer, PropertyChangeListener {
  private boolean cyclic = true;
  private final int DEFAULT_CAPACITY = 5000;
  private int capacity = DEFAULT_CAPACITY;
  private static final String PANEL_CAPACITY = "CHAINSAW_CAPACITY";
  List unfilteredList;
  List filteredList;
  Set idSet = new HashSet(capacity);
  private boolean currentSortAscending;
  private int currentSortColumn;
  private EventListenerList eventListenerList = new EventListenerList();
  private List columnNames = new ArrayList(ChainsawColumns.getColumnsNames());
  private boolean sortEnabled = false;
  private boolean reachedCapacity = false;

  //  protected final Object syncLock = new Object();
  private LoggerNameModel loggerNameModelDelegate =
    new LoggerNameModelSupport();

  //because we may be using a cyclic buffer, if an ID is not provided in the property, 
  //use and increment this row counter as the ID for each received row
  int uniqueRow;
  private Set uniqueMDCKeys = new HashSet();
  private Rule displayRule;
  private PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);

  public ChainsawCyclicBufferTableModel() {
    propertySupport.addPropertyChangeListener("cyclic", new ModelChanger());
    if (System.getProperty(PANEL_CAPACITY) != null) {
        try {
            capacity = Integer.parseInt(System.getProperty(PANEL_CAPACITY));
        } catch (NumberFormatException nfe) {}
    }
    unfilteredList = new CyclicBufferList(capacity);
    filteredList = new CyclicBufferList(capacity);
  }
  
  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() instanceof Rule) {
      reFilter();
    }
  }
  
  private void reFilter() {
    synchronized(unfilteredList) {
      filteredList.clear();
      Iterator iter = unfilteredList.iterator();
      while (iter.hasNext()) {
          LoggingEvent e = (LoggingEvent)iter.next();
          if ((displayRule == null) || (displayRule.evaluate(e))) {
              filteredList.add(e);
          }
      }
      fireTableDataChanged();
      notifyCountListeners();
    }
  }
  
  /**
   * @param l
   */
  public void removeLoggerNameListener(LoggerNameListener l) {
    loggerNameModelDelegate.removeLoggerNameListener(l);
  }

  /**
   * @param loggerName
   * @return
   */
  public boolean addLoggerName(String loggerName) {
    return loggerNameModelDelegate.addLoggerName(loggerName);
  }

  /**
   * @param l
   */
  public void addLoggerNameListener(LoggerNameListener l) {
    loggerNameModelDelegate.addLoggerNameListener(l);
  }

  /**
   * @return
   */
  public Collection getLoggerNames() {
    return loggerNameModelDelegate.getLoggerNames();
  }

  public void addEventCountListener(EventCountListener listener) {
    eventListenerList.add(EventCountListener.class, listener);
  }

  public boolean isSortable(int col) {
    return true;
  }

  public void notifyCountListeners() {
    EventCountListener[] listeners =
      (EventCountListener[]) eventListenerList.getListeners(
        EventCountListener.class);

    for (int i = 0; i < listeners.length; i++) {
      listeners[i].eventCountChanged(
        filteredList.size(), unfilteredList.size());
    }
  }

  /**
   * Changes the underlying display rule in use.  If there was
   * a previous Rule defined, this Model removes itself as a listener
   * from the old rule, and adds itself to the new rule (if the new Rule is not Null).
   *
   * In any case, the model ensures the Filtered list is made up to date in a separate thread.
   */
  public void setDisplayRule(Rule displayRule) {
    if (this.displayRule != null) {
      this.displayRule.removePropertyChangeListener(this);
    }

    this.displayRule = displayRule;

    if (this.displayRule != null) {
      this.displayRule.addPropertyChangeListener(this);
    }
    reFilter();
  }

  /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.EventContainer#sort()
     */
  public void sort() {
    if (sortEnabled) {

      synchronized (filteredList) {
        Collections.sort(
          filteredList,
          new ColumnComparator(getColumnName(currentSortColumn), currentSortColumn, currentSortAscending));
      }
      fireTableRowsUpdated(0, Math.max(filteredList.size() - 1, 0));
    }
  }
  
  public boolean isSortEnabled() {
      return sortEnabled;
  }

  public void sortColumn(int col, boolean ascending) {
    LogLog.debug(
      "request to sort col=" + col);
    currentSortAscending = ascending;
    currentSortColumn = col;
    sortEnabled = true;
    sort();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#clear()
   */
  public void clearModel() {
    reachedCapacity = false;
    synchronized (unfilteredList) {
      unfilteredList.clear();
      filteredList.clear();
      idSet.clear();
      uniqueRow = 0;
    }
    fireTableDataChanged();
    notifyCountListeners();
  }

  /**
   * @deprecated - should this be replaced with a Refinement filter?
   *
   * If not it should be replaced with something inside LogPanel, a Finder class, it should not be in the Model.
   */
  public int find(int startRow, String text) {
    if (text == null) {
      text = "";
    } else {
      text = text.toLowerCase();
    }

    int currentRow = -1;

    synchronized (filteredList) {
      ListIterator iter = filteredList.listIterator();

      while (iter.hasNext()) {
        currentRow++;

        LoggingEvent event = (LoggingEvent) iter.next();

        if (currentRow < startRow) {
          continue;
        }

        if (event.getMessage().toString().toLowerCase().indexOf(text) > 0) {
          return currentRow;
        }
      }
    }

    return -1;
  }

  public List getAllEvents() {
    List list = new ArrayList(unfilteredList.size());

    synchronized (unfilteredList) {
      list.addAll(unfilteredList);
    }

    return list;
  }

  public int getRowIndex(LoggingEvent e) {
    synchronized (filteredList) {
      return filteredList.indexOf(e);
    }
  }

  public int getColumnCount() {
    return columnNames.size();
  }

  public String getColumnName(int column) {
    return columnNames.get(column).toString();
  }

  public LoggingEvent getRow(int row) {
    synchronized (filteredList) {
      if (row < filteredList.size()) {
        return (LoggingEvent) filteredList.get(row);
      }
    }

    return null;
  }

  public int getRowCount() {
    synchronized (filteredList) {
      return filteredList.size();
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    LoggingEvent event = null;

    synchronized (filteredList) {
      if (rowIndex < filteredList.size()) {
        event = (LoggingEvent) filteredList.get(rowIndex);
      }
    }

    if (event == null) {
      return null;
    }

    LocationInfo info = null;
    if (event.locationInformationExists()) {
        info = event.getLocationInformation();
    }

    if (event == null) {
      LogLog.error("Invalid rowindex=" + rowIndex);
      throw new NullPointerException("Invalid rowIndex=" + rowIndex);
    }

    switch (columnIndex + 1) {
    case ChainsawColumns.INDEX_ID_COL_NAME:

      Object id = event.getProperty(ChainsawConstants.LOG4J_ID_KEY);

      if (id != null) {
        return id;
      }

      return new Integer(rowIndex);

    case ChainsawColumns.INDEX_LEVEL_COL_NAME:
      return event.getLevel();

    case ChainsawColumns.INDEX_LOGGER_COL_NAME:
      return event.getLoggerName();

    case ChainsawColumns.INDEX_TIMESTAMP_COL_NAME:
      return new Date(event.timeStamp);

    case ChainsawColumns.INDEX_MESSAGE_COL_NAME:
      return event.getRenderedMessage();

    case ChainsawColumns.INDEX_MDC_COL_NAME:
      return getMDC(event);

    case ChainsawColumns.INDEX_NDC_COL_NAME:
      return event.getNDC();

    case ChainsawColumns.INDEX_PROPERTIES_COL_NAME:
      return getProperties(event);

    case ChainsawColumns.INDEX_THREAD_COL_NAME:
      return event.getThreadName();

    case ChainsawColumns.INDEX_THROWABLE_COL_NAME:
      return event.getThrowableStrRep();

    case ChainsawColumns.INDEX_CLASS_COL_NAME:
      return (info == null || (info != null && info.getClassName().equals("?"))) ?  "" : info.getClassName();

    case ChainsawColumns.INDEX_FILE_COL_NAME:
      return (info == null || (info != null && info.getFileName().equals("?"))) ? "" : info.getFileName();

    case ChainsawColumns.INDEX_LINE_COL_NAME:
      return (info == null || (info != null && info.getLineNumber().equals("?"))) ? "" : info.getLineNumber();

    case ChainsawColumns.INDEX_METHOD_COL_NAME:
      return (info == null || (info != null && info.getMethodName().equals("?"))) ? "" : info.getMethodName();
    
    default:

      if (columnIndex <= columnNames.size()) {
        return event.getMDC(columnNames.get(columnIndex).toString());
      }
    }

    return "";
  }
  
  private String getMDC(LoggingEvent event) {
      Iterator iter = event.getMDCKeySet().iterator();
      StringBuffer mdc = new StringBuffer("{");
      
      while (iter.hasNext()) {
          mdc.append("{");
          Object key = iter.next(); 
          mdc.append(key);
          mdc.append(",");
          mdc.append(event.getMDC(key.toString()));
          mdc.append("}");
      }
      mdc.append("}");
      return mdc.toString();
  }
  
  private String getProperties(LoggingEvent event) {
      Iterator iter = event.getPropertyKeySet().iterator();
      StringBuffer prop = new StringBuffer("{");
      
      while (iter.hasNext()) {
          prop.append("{");
          Object key = iter.next(); 
          prop.append(key);
          prop.append(",");
          prop.append(event.getProperty(key.toString()));
          prop.append("}");
      }
      prop.append("}");
      return prop.toString();
  }

  public boolean isAddRow(LoggingEvent e, boolean valueIsAdjusting) {
    boolean rowAdded = false;

    Object id = e.getProperty(ChainsawConstants.LOG4J_ID_KEY);

    if (id == null) {
      id = new Integer(++uniqueRow);
      e.setProperty(ChainsawConstants.LOG4J_ID_KEY, id.toString());
    }

    //prevent duplicate rows
    if (idSet.contains(id)) {
      return false;
    }
    idSet.add(id);
    unfilteredList.add(e);

    if ((displayRule == null) || (displayRule.evaluate(e))) {
      synchronized (filteredList) {
        filteredList.add(e);
        rowAdded = true;
      }
    }

    /**
     * Is this a new MDC key we haven't seen before?
     */
    boolean newColumn = uniqueMDCKeys.addAll(e.getMDCKeySet());

    if (newColumn) {
        /**
         * If so, we should add them as columns and notify listeners.
         */
        for (Iterator iter = e.getMDCKeySet().iterator(); iter.hasNext();) {
          Object key = iter.next();
    
          if (!columnNames.contains(key)) {
            columnNames.add(key);
            LogLog.debug("Adding col '" + key + "', columNames=" + columnNames);
            fireNewKeyColumnAdded(
              new NewKeyEvent(
                this, columnNames.indexOf(key), key, e.getMDC(key.toString())));
          }
        }
    }
    if (!valueIsAdjusting) {
        int lastAdded = getLastAdded();
        fireTableEvent(lastAdded, lastAdded, 1);
    }

    return rowAdded;
  }
  
  public int getLastAdded() {
      int last = 0;
      if (cyclic) {
          last = ((CyclicBufferList)filteredList).getLast();
      } else {
          last = filteredList.size();
      }
      return last;
  }
  
  public void fireTableEvent(int begin, int end, int count) {
      if (cyclic) {
          if (!reachedCapacity) {
              //if we didn't loop and it's the 1st time, insert
              if (begin + count < capacity) {
                fireTableRowsInserted(begin, end);
              } else {
                //we did loop - insert and then update rows
                fireTableRowsInserted(begin, capacity);
                fireTableRowsUpdated(0, capacity);
                reachedCapacity = true;
              }
          } else {
            fireTableRowsUpdated(0, capacity);
          } 
      } else {
          fireTableRowsInserted(begin, end);
      }
  }

  /**
  * @param key
  */
  private void fireNewKeyColumnAdded(NewKeyEvent e) {
    NewKeyListener[] listeners =
      (NewKeyListener[]) eventListenerList.getListeners(NewKeyListener.class);

    for (int i = 0; i < listeners.length; i++) {
      NewKeyListener listener = listeners[i];
      listener.newKeyAdded(e);
    }
  }

  /**
     * Returns true if this model is Cyclic (bounded) or not
     * @return true/false
     */
  public boolean isCyclic() {
    return cyclic;
  }

  /**
   * @return
   */
  public int getMaxSize() {
      return capacity;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#addNewKeyListener(org.apache.log4j.chainsaw.NewKeyListener)
   */
  public void addNewKeyListener(NewKeyListener l) {
    eventListenerList.add(NewKeyListener.class, l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#removeNewKeyListener(org.apache.log4j.chainsaw.NewKeyListener)
   */
  public void removeNewKeyListener(NewKeyListener l) {
    eventListenerList.remove(NewKeyListener.class, l);
  }

  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    switch (columnIndex + 1) {
    case ChainsawColumns.INDEX_THROWABLE_COL_NAME:
      return true;
    }

    return super.isCellEditable(rowIndex, columnIndex);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#setCyclic(boolean)
   */
  public void setCyclic(final boolean cyclic) {
    if (this.cyclic == cyclic) {
      return;
    }
    final boolean old = this.cyclic;
    this.cyclic = cyclic;
    propertySupport.firePropertyChange("cyclic", old, cyclic);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    propertySupport.addPropertyChangeListener(l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(
    String propertyName, PropertyChangeListener l) {
    propertySupport.addPropertyChangeListener(propertyName, l);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.EventContainer#size()
   */
  public int size() {
    return unfilteredList.size();
  }

  private class ModelChanger implements PropertyChangeListener {
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent arg0) {
      Thread thread =
        new Thread(
          new Runnable() {
            public void run() {
              ProgressMonitor monitor = null;

              int index = 0;

              try {
                synchronized (unfilteredList) {
                  monitor =
                    new ProgressMonitor(
                      null, "Switching models...",
                      "Transferring between data structures, please wait...", 0,
                      unfilteredList.size() + 1);
                  monitor.setMillisToDecideToPopup(250);
                  monitor.setMillisToPopup(100);
                  LogLog.debug(
                    "Changing Model, isCyclic is now " + isCyclic());

                  List newUnfilteredList = null;
                  List newFilteredList = null;
                  HashSet newIDSet = null;

				  newIDSet = new HashSet(capacity);
                  if (isCyclic()) {
                    newUnfilteredList = new CyclicBufferList(capacity);
                    newFilteredList = new CyclicBufferList(capacity);
                  } else {
                    newUnfilteredList = new ArrayList(capacity);
                    newFilteredList = new ArrayList(capacity);
                  }

                  int increment = 0;
                  for (Iterator iter = unfilteredList.iterator();
                      iter.hasNext();) {
                  	LoggingEvent e = (LoggingEvent)iter.next();
                    newUnfilteredList.add(e);
                    Object o = e.getProperty(e.getProperty(ChainsawConstants.LOG4J_ID_KEY));
                    if (o != null) {
                    	newIDSet.add(o);
                    } else {
                    	newIDSet.add(new Integer(increment++));
                    }
                    monitor.setProgress(index++);
                  }

                  unfilteredList = newUnfilteredList;
                  filteredList = newFilteredList;
                  idSet = newIDSet;
                }

                monitor.setNote("Refiltering...");
                reFilter();

                monitor.setProgress(index++);
              } finally {
                monitor.close();
              }

              LogLog.debug("Model Change completed");
            }
          });
      thread.setPriority(Thread.MIN_PRIORITY + 1);
      thread.start();
    }
  }
}
