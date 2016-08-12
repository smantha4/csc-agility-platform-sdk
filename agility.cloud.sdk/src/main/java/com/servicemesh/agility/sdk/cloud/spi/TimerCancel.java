package com.servicemesh.agility.sdk.cloud.spi;

import com.servicemesh.core.reactor.Timer;

public class TimerCancel implements ICancellable
{

    private Timer _timer;

    public TimerCancel(Timer timer)
    {
        _timer = timer;
    }

    @Override
    public void cancel()
    {
        _timer.cancel();
    }

}
