package com.servicemesh.core.async;

public interface Function<A,R> {

	public R invoke(A arg);
}
