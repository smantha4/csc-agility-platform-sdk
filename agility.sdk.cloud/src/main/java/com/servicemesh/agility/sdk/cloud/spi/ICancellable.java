package com.servicemesh.agility.sdk.cloud.spi;

/**
 * Operations that are can be cancelled should return an implementation of the ICancellable interface. This provides the user the
 * ability to potentially cancel long-running operations in the adapter (e.g. instance clone).
 */
public interface ICancellable
{
    public void cancel();
}
