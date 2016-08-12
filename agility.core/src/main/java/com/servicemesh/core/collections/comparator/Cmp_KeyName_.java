// _Warning_

package com.servicemesh.core.collections.comparator;

import com.servicemesh.core.collections.common.*;

/** Interface that campares two values of type _KeyType_. */
public interface Cmp_KeyName_
{
    /**
     * Compares two values of type _KeyType_.
     * 
     * @param a
     *            the first value to be compared.
     * @param b
     *            the second value to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the
     *         second, respectively.
     */
    int compare(_KeyType_ a, _KeyType_ b);
}
