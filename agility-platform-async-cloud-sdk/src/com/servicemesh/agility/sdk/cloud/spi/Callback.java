package com.servicemesh.agility.sdk.cloud.spi;

public interface Callback<T>
{
    void onResponse(T aT);

    void onError(Throwable t);
};
