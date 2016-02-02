package com.servicemesh.agility.api.service;

import java.util.List;

public interface IProjectStats<T>
{

    List<T> stats(Context context) throws Exception;
}
