// _Warning_

package com.servicemesh.core.collections.comparator;

import com.servicemesh.core.collections.common.*;

/** Interface that campares two values of type _ValueType_. */
public interface Cmp_ValueName_
{
    /**
     * Compares two values of type _ValueType_.
     * 
     * @param a
     *            the first value to be compared.
     * @param b
     *            the second value to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the
     *         second, respectively.
     */
    int compare(_ValueType_ a, _ValueType_ b);
}
