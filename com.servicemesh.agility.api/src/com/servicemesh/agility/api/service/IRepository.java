package com.servicemesh.agility.api.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IRepository
{
    public static class Entry
    {
        public String name;
        public boolean isDir;
        public long size;
    }

    public void put(int repositoryId, String path, InputStream is, long size) throws Exception;

    public void get(int repositoryId, String path, OutputStream os) throws Exception;

    public void delete(int repositoryId, String path) throws Exception;

    public List<Entry> list(int repositoryId, String path) throws Exception;
}
