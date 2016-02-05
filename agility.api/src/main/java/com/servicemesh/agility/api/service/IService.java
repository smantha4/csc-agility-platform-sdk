package com.servicemesh.agility.api.service;

import java.io.File;
import java.util.List;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Assetlist;
import com.servicemesh.agility.api.Envelope;
import com.servicemesh.agility.api.Link;
import com.servicemesh.agility.api.Task;

public interface IService<T>
{
    // base operations supported by all assets
    T get(int id) throws Exception;

    T get(int id, Context context) throws Exception;

    T getForLookup(int id) throws Exception;

    List<T> list() throws Exception;

    List<T> list(Context context) throws Exception;

    Assetlist search(Context context) throws Exception;

    T lookup(T asset, Asset parent, Context context) throws Exception;

    T create(T asset, Asset parent, Context context) throws Exception;

    T update(T asset, Asset parent, Context context) throws Exception;

    Task delete(T asset, Asset parent) throws Exception;

    Task softDelete(T asset, Asset parent) throws Exception;

    Task hardDelete(T asset, Asset parent) throws Exception;

    T copy(T asset, Asset parent) throws Exception;

    List<Link> usedBy(int id) throws Exception;

    // asset validation before create/update
    void validate(T asset, Asset parent, Context context) throws Exception;

    // import/export of asset and any contained assets
    void export(T asset, Envelope envelope, List<File> files, ExportOptions options) throws Exception;

    // initialize/cleanup use of service 
    public void init(String login) throws Exception;

    public void fini();

    /**
     * @return the version of the REST API for which this call is executing. E.g. "3.2"
     */
    public String getCalledVersion();

    // check to see if additional interfaces are support
    public <C> C supports(Class<C> aClass);
}
