package com.servicemesh.core.async;

public interface Callback<T> {

	public void invoke(T arg);
}
