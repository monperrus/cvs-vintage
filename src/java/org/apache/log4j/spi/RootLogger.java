/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.spi;

import org.apache.log4j.*;


// Contibutors: Mathias Bogaert

/**
   RootLogger sits at the top of the category hierachy. It is a
   regular logger except that it provides several guarantees.

   <p>First, it cannot be assigned a <code>null</code>
   level. Second, since root logger cannot have a parent, the
   {@link #getChainedLevel} method always returns the value of the
   level field without walking the hierarchy.

   @author Ceki G&uuml;lc&uuml;

 */
public final class RootLogger extends Logger {
  /**
     The root category names itself as "root". However, the root
     category cannot be retrieved by name.
  */
  public RootLogger(Level level) {
    super("root");
    setLevel(level);
  }

  /**
     Return the assigned level value without walking the category
     hierarchy.
  */
  public final Level getChainedLevel() {
    return level;
  }

  /**
     Setting a null value to the level of the root logger may have catastrophic
     results. We prevent this here.

     @since 0.8.3 */
  public final void setLevel(Level level) {
    if (level == null) {
      if (repository != null) {
        repository.addErrorItem(
          new ErrorItem(
            "You have tried to set a null level to root.", new Exception()));
      }
    } else {
      this.level = level;
    }
  }
}
