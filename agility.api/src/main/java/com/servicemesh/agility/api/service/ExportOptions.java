package com.servicemesh.agility.api.service;

import java.util.StringTokenizer;

public class ExportOptions
{

    private boolean _recursive = true;
    private boolean _lookupOnly = false;
    private boolean _exportDependencies = true;
    private boolean _exportResourceFirst = false;
    private boolean _exportInstances = false;
    private boolean _attachmentRefOnly = false;
    private boolean _exportConnectionsLast = false;
    private boolean _exportMultiEnvelope = false;
    private String _userMode = null; // "create" or "lookup"

    public ExportOptions()
    {
    }

    public ExportOptions(String opts)
    {
        super();
        parse(opts);
    }

    public void parse(String opts)
    {
        if (opts == null)
        {
            return;
        }
        // Parse the opts.  It should be of the format:
        //   name1:val1;name2:val2;name3:val3;...
        StringTokenizer tokenizer = new StringTokenizer(opts, ";");
        while (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            String[] s = token.split("\\:", 2);
            if (s.length > 1)
            {
                String name = s[0];
                String val = s[1];
                setField(name, val);
            }
        }
    }

    private void setField(String fieldName, String fieldValue)
    {
        if ("recursive".equals(fieldName))
        {
            Boolean recursive = new Boolean(fieldValue);
            setRecursive(recursive.booleanValue());
        }
        else if ("instances".equals(fieldName))
        {
            Boolean instances = new Boolean(fieldValue);
            setExportInstances(instances.booleanValue());
        }
        else if ("attachmentRefOnly".equals(fieldName))
        {
            Boolean refOnly = new Boolean(fieldValue);
            setAttachmentRefOnly(refOnly.booleanValue());
        }
        else if ("userMode".equals(fieldName))
        {
            setUserMode(fieldValue);
        }
    }

    public boolean isAttachmentRefOnly()
    {
        return _attachmentRefOnly;
    }

    public void setAttachmentRefOnly(boolean attachmentRefOnly)
    {
        _attachmentRefOnly = attachmentRefOnly;
    }

    public String getUserMode()
    {
        return _userMode;
    }

    public void setUserMode(String userMode)
    {
        _userMode = userMode;
    }

    public boolean isExportResourceFirst()
    {
        return _exportResourceFirst;
    }

    public void setExportResourceFirst(boolean exportResourceFirst)
    {
        _exportResourceFirst = exportResourceFirst;
    }

    public boolean isExportInstances()
    {
        return _exportInstances;
    }

    public void setExportInstances(boolean exportInstances)
    {
        _exportInstances = exportInstances;
    }

    public boolean isLookupOnly()
    {
        return _lookupOnly;
    }

    public boolean isRecursive()
    {
        return _recursive;
    }

    public void setRecursive(boolean recursive)
    {
        _recursive = recursive;
    }

    public boolean isLookpOnly()
    {
        return _lookupOnly;
    }

    public void setLookupOnly(boolean lookupOnly)
    {
        _lookupOnly = lookupOnly;
    }

    public boolean isExportDependencies()
    {
        return _exportDependencies;
    }

    public void setExportDependencies(boolean exportDependencies)
    {
        _exportDependencies = exportDependencies;
    }

    public boolean isResourceFirst()
    {
        return _exportResourceFirst;
    }

    public void setResourceFirst(boolean exportResourceFirst)
    {
        _exportResourceFirst = exportResourceFirst;
    }

    public boolean isExportConnectionsLast()
    {
        return _exportConnectionsLast;
    }

    public void setExportConnectionsLast(boolean exportConnectionsLast)
    {
        _exportConnectionsLast = exportConnectionsLast;
    }

    public boolean isExportMultiEnvelope()
    {
        return _exportMultiEnvelope;
    }

    public void setExportMultiEnvelope(boolean exportMultiEnvelope)
    {
        _exportMultiEnvelope = exportMultiEnvelope;
    }
}
