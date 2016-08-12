/**
 *              COPYRIGHT (C) 2008-2015 SERVICEMESH, INC.
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

package com.servicemesh.io.util;

import com.servicemesh.core.reactor.TimerReactor;

public class IOUtil
{
    private static final String IO_TIMER_REACTOR = "AgilityIOTimerReactor";

    public static TimerReactor getTimerReactor()
    {
        return IOTimerReactor.getInstance().timerReactor();
    }

    /**
     * Provided as a lazy initialization holder for the timer reactor to be used by the IO bundle.
     */
    private static class IOTimerReactor
    {
        private static class LazyReactorHolder
        {
            private static final IOTimerReactor INSTANCE = new IOTimerReactor();
        }

        private final TimerReactor _timerReactor;

        private IOTimerReactor()
        {
            _timerReactor = TimerReactor.getTimerReactor(IO_TIMER_REACTOR);
        }

        public static IOTimerReactor getInstance()
        {
            return LazyReactorHolder.INSTANCE;
        }

        public TimerReactor timerReactor()
        {
            return _timerReactor;
        }
    }
}
