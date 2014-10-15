/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 1997, 2010 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package cn.edu.swust.berkeley.je.rep.quote;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

class DataAccessor {
    /* Quote Accessor */
    final PrimaryIndex<String, Quote> quoteById;

    DataAccessor(EntityStore store) {
        /* Primary index of the Employee database. */
        quoteById = store.getPrimaryIndex(String.class, Quote.class);
    }
}
