/**
 *              COPYRIGHT (C) 2008-2014 SERVICEMESH, INC.
 *                        ALL RIGHTS RESERVED.
 *                   CONFIDENTIAL AND PROPRIETARY.
 *
 *  ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS
 *  (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT
 *  ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.
 *  ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE
 *  AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED
 *  IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY
 *  OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY
 *  SERVICEMESH IS STRICTLY PROHIBITED.
 */

package com.servicemesh.io.http.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.servicemesh.io.http.IHttpHeader;

public class BaseHttpHeader
    implements IHttpHeader
{
    final private String name;
    final private String value;
    final private List<String> values;

    public BaseHttpHeader(final String name, final String value)
    {
        if ((name == null) || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name parameter: " + name);
        }

        this.name = name;
        this.value = (value != null) ? value.trim() : "";

        List<String> values = processMultiValueString(this.value, "Invalid value parameter: " + value);

        this.values = Collections.unmodifiableList(values);
    }

    public BaseHttpHeader(final String name, final List<String> values)
    {
        if ((name == null) || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name parameter: " + name);
        }

        this.name = name;

        if ((values != null) && !values.isEmpty()) {
            final List<String> valuesList = new ArrayList<String>();

            for (final String nextValue : values) {
                final String workValue = (nextValue != null) ? nextValue : " ";
                final List<String> multiValues = processMultiValueString(workValue, "Invalid values parameter: " + values);

                valuesList.addAll(multiValues);
            }

            this.values = Collections.unmodifiableList(valuesList);

            StringBuilder valueBuilder = new StringBuilder();
            for (String nextValue : valuesList) {
                if (valueBuilder.length() > 0) {
                    valueBuilder.append(",");
                }

                valueBuilder.append(nextValue);
            }

            this.value = valueBuilder.toString();
        } else {
            this.values = Collections.emptyList();
            this.value = "";
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public List<String> getValues()
    {
        return values;
    }

    @Override
    public String toString()
    {
        return getName() + ": " + getValue();
    }

    private List<String> processMultiValueString(final String value, final String errMsg)
    {
        final List<String> values = new ArrayList<String>();

        if (value != null) {
            String workValue = (value.endsWith(",")) ? value + " " : value;

            for (final String nextValue : workValue.split(",")) {
                values.add((nextValue != null) ? nextValue.trim() : "");
            }
        } else {
            values.add("");
        }

        return values;
    }
}
