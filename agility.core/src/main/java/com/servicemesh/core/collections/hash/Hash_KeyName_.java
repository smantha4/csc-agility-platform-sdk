// _Warning_

package com.servicemesh.core.collections.hash;

import com.servicemesh.core.collections.common.*;

/** Interface that allows hashing of a _KeyType_ value. */
public interface Hash_KeyName_
{
    /**
     * Returns a hash code for an object of type _KeyType_
     * 
     * @param v
     *            the _KeyType_ for which a hash is desired
     * @return a hash code for the given value (see Object.hashCode for a definition of hash codes)
     */
    int code(_KeyType_ v);
}
