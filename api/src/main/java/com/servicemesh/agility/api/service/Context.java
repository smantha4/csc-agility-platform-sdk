/**
 * COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.  ALL RIGHTS RESERVED.  CONFIDENTIAL AND PROPRIETARY.
 *
 * ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.     ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY SERVICEMESH IS STRICTLY PROHIBITED.
 *
 */

package com.servicemesh.agility.api.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.validation.Schema;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Envelope;
import com.servicemesh.agility.api.Link;

public class Context
{
    private HashMap<String, HashMap<Integer, Asset>> _imports = new HashMap<String, HashMap<Integer, Asset>>();
    private boolean _useImports = true;

    private HashMap<String, File> _files = new HashMap<String, File>();
    private Map<String, List<String>> _queryParams = new HashMap<String, List<String>>();
    private String _prefix = null;
    private Envelope _envelope;

    private boolean _verbose = false;
    private boolean _commandLine = false;
    private boolean _createVersionedAssets = false;
    private boolean _updateVersionedAssets = false;
    private boolean _preserveUseHead = false;
    private boolean _preserveLatest = false;
    private boolean _forceInProgress = false;
    private boolean _validate = false;
    private boolean _signatureValid = false;
    private boolean _forceOnError = false;
    private boolean _checkUse = true;
    private boolean _minimalConversion = false;

    private static final String LINK_TYPE_START = "application/";
    private static final String LINK_TYPE_END = "+xml";

    public enum ArtifactOrderType
    {
        AnyOrder, FixedOrder
    };

    private ArtifactOrderType _artifactOrder = ArtifactOrderType.AnyOrder;

    public ArtifactOrderType getArtifactOrder()
    {
        return _artifactOrder;
    }

    public void setArtifactOrder(ArtifactOrderType order)
    {
        _artifactOrder = order;
    }

    @Deprecated
    /**
     * @deprecated consider using _useImports instead
     */
    private boolean _import = false;
    private Map<String, Object> _objects = new HashMap<String, Object>();
    private Schema _schema = null;

    public Envelope getEnvelope()
    {
        return _envelope;
    }

    public void setEnvelope(Envelope envelope)
    {
        _envelope = envelope;
    }

    public enum TopologyOrderType
    {
        AnyOrder, FixedOrder, ManualOrder
    };

    private TopologyOrderType _topo_order = TopologyOrderType.AnyOrder;

    public TopologyOrderType getTopologyOrder()
    {
        return _topo_order;
    }

    public void setTopologyOrder(TopologyOrderType order)
    {
        _topo_order = order;
    }

    public Object getObject(String name)
    {
        return _objects.get(name);
    }

    public void setObject(String name, Object object)
    {
        _objects.put(name, object);
    }

    public String getPrefix()
    {
        return _prefix;
    }

    public void setPrefix(String prefix)
    {
        _prefix = prefix;
    }

    public boolean isUseImports()
    {
        return _useImports;
    }

    /**
     * If useImports is true (defaulted to true), then the imports list will be used. Set this to false if you are not performing
     * an import, and want to make double sure that the context imports map is never relied upon to resolve an id.
     * 
     * @param useImports
     */
    public void setUseImports(boolean useImports)
    {
        _useImports = useImports;
    }

    public Schema get_schema()
    {
        return _schema;
    }

    public void set_schema(Schema schema)
    {
        _schema = schema;
    }

    public boolean isImport()
    {
        return _import;
    }

    public void setImport(boolean isImport)
    {
        _import = isImport;
    }

    public boolean isSignatureValid()
    {
        return _signatureValid;
    }

    public void setSignatureValid(boolean signatureValid)
    {
        _signatureValid = signatureValid;
    }

    public boolean isVerbose()
    {
        return _verbose;
    }

    public void setVerbose(boolean verbose)
    {
        _verbose = verbose;
    }

    public boolean isValidate()
    {
        return _validate;
    }

    public void setValidate(boolean validate)
    {
        _validate = validate;
    }

    public Schema getSchema()
    {
        return _schema;
    }

    public void setSchema(Schema schema)
    {
        _schema = schema;
    }

    public boolean isCommandLine()
    {
        return _commandLine;
    }

    public void setCommandLine(boolean commandLine)
    {
        _commandLine = commandLine;
    }

    public boolean canCreateVersionedAssets()
    {
        return _createVersionedAssets;
    }

    public void setCreateVersionedAssets(boolean createVersionedAssets)
    {
        _createVersionedAssets = createVersionedAssets;
    }

    public boolean canUpdateVersionedAssets()
    {
        return _updateVersionedAssets;
    }

    public void setUpdateVersionedAssets(boolean updateVersionedAssets)
    {
        _updateVersionedAssets = updateVersionedAssets;
    }

    public boolean canPreserveUseHead()
    {
        return _preserveUseHead;
    }

    public void setPreseverUseHead(boolean preserveUseHead)
    {
        _preserveUseHead = preserveUseHead;
    }

    public boolean canPreserveLatest()
    {
        return _preserveLatest;
    }

    public void setPreserveLatest(boolean preserveLatest)
    {
        _preserveLatest = preserveLatest;
    }

    public boolean isForceInProgress()
    {
        return _forceInProgress;
    }

    public void setForceInProgress(boolean forceInProgress)
    {
        _forceInProgress = forceInProgress;
    }

    /** Returns the link type for a specified asset class name */
    public static String getLinkType(String className)
    {
        return LINK_TYPE_START + className + LINK_TYPE_END;
    }

    /** Returns the asset class name from a specified link type */
    public static String getClassName(String linkType) throws Exception
    {
        if ((!linkType.startsWith(LINK_TYPE_START)) || (!linkType.endsWith(LINK_TYPE_END)))
        {
            throw new Exception("Invalid linkType: " + linkType);
        }

        return linkType.substring(LINK_TYPE_START.length(), linkType.lastIndexOf(LINK_TYPE_END));
    }

    /**
     * Returns the Asset with the given id and of the given type, or null if Asset not found. type should be the fully qualified
     * class name, e.g. com.servicemesh.agility.api.Workload
     */
    public synchronized Asset get(int id, String type)
    {
        if (!isUseImports())
        {
            return null;
        }
        type = getLinkType(type);
        HashMap<Integer, Asset> assets = _imports.get(type);
        if (assets == null)
        {
            return null;
        }
        else
        {
            return assets.get(id);
        }
    }

    public synchronized void put(int id, Asset asset)
    {
        if (!isUseImports())
        {
            return;
        }
        String type = getLinkType(asset.getClass().getName());
        HashMap<Integer, Asset> assets = _imports.get(type);
        if (assets == null)
        {
            assets = new HashMap<Integer, Asset>();
            _imports.put(type, assets);
        }
        assets.put(id, asset);
    }

    public synchronized Asset get(int id, Asset asset)
    {
        String type = getLinkType(asset.getClass().getName());
        HashMap<Integer, Asset> assets = _imports.get(type);
        if (assets == null)
        {
            return null;
        }

        Asset new_asset = assets.get(id);
        return new_asset;
    }

    public synchronized void putFile(String name, File file)
    {
        _files.put(name, file);
    }

    public File getFile(String name)
    {
        return _files.get(name);
    }

    public Collection<File> getFiles()
    {
        return _files.values();
    }

    public boolean removeFile(String name)
    {
        return (_files.remove(name) != null);
    }

    /**
     * Returns a mapping from Link types to a map of old/new IDs for imported Assets
     */
    public synchronized Map<String, Map<Integer, Integer>> getImports()
    {
        HashMap imports = new HashMap<String, HashMap<Integer, Integer>>();

        if (_useImports)
        {
            for (Map.Entry<String, HashMap<Integer, Asset>> entry : _imports.entrySet())
            {
                HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();

                for (Map.Entry<Integer, Asset> idEntry : entry.getValue().entrySet())
                {
                    idMap.put(idEntry.getKey(), new Integer(idEntry.getValue().getId()));
                }
                imports.put(entry.getKey(), idMap);
            }
        }
        return imports;
    }

    public List<Asset> getAssetsOfType(String linkType)
    {
        ArrayList<Asset> list = new ArrayList<Asset>();

        if (_imports.size() == 0 || linkType == null)
        {
            return list;
        }

        Map<Integer, Asset> map = _imports.get(linkType);
        if (map == null)
        {
            return list;
        }

        for (Map.Entry<Integer, Asset> entry : map.entrySet())
        {
            list.add(entry.getValue());
        }

        return list;
    }

    public int getId(Link link)
    {
        if (!isUseImports())
        {
            return link.getId();
        }
        if (_imports.size() == 0)
        {
            return link.getId();
        }
        String type = link.getType();
        if (type == null)
        {
            return link.getId();
        }
        type = type.trim();
        Map<Integer, Asset> assets = _imports.get(link.getType());
        if (assets == null)
        {
            return link.getId();
        }
        Asset asset = assets.get(link.getId());
        if (asset == null)
        {
            return -1;
        }
        return asset.getId();
    }

    public int getIdDB(Link link)
    {
        if (!isUseImports())
        {
            return -1;
        }
        if (_imports.size() == 0)
        {
            return -1;
        }
        String type = link.getType();
        if (type == null)
        {
            return -1;
        }
        type = type.trim();
        Map<Integer, Asset> assets = _imports.get(link.getType());
        if (assets == null)
        {
            return -1;
        }
        Asset asset = assets.get(link.getId());
        if (asset == null)
        {
            return -1;
        }
        return asset.getId();
    }

    public Asset resolve(Link link)
    {
        Map<Integer, Asset> assets = _imports.get(link.getType());
        if (assets == null)
        {
            return null;
        }
        return assets.get(link.getId());
    }

    public void clear()
    {
        _imports.clear();
        for (File file : _files.values())
        {
            file.delete();
        }
        _files.clear();
        _queryParams.clear();
        _objects.clear();
    }

    public String[] getParamNames()
    {
        Set<String> keys = _queryParams.keySet();
        return keys.toArray(new String[0]);
    }

    public List<String> getParamValue(String key)
    {
        return _queryParams.get(key);
    }

    public String getParamStringValue(String key)
    {
        String result = null;
        List<String> values = _queryParams.get(key);
        if (values != null && values.size() > 0)
        {
            result = values.get(0);
        }
        return result;
    }

    public Integer getParamIntegerValue(String key)
    {
        String result = null;
        List<String> values = _queryParams.get(key);
        if (values != null && values.size() > 0)
        {
            result = values.get(0);
        }
        return Integer.valueOf(result);
    }

    public void clearParams()
    {
        _queryParams.clear();
    }

    public void addParam(String key, String value)
    {
        List<String> values = _queryParams.get(key);
        if (values == null)
        {
            values = new ArrayList<String>();
            _queryParams.put(key, values);
        }
        values.add(value);
    }

    public void addParams(String key, List<String> values)
    {
        _queryParams.put(key, values);
    }

    public boolean isForceOnError()
    {
        return _forceOnError;
    }

    public void setForceOnError(boolean forceOnError)
    {
        _forceOnError = forceOnError;
    }

    public boolean isCheckUse()
    {
        return _checkUse;
    }

    public void setCheckUse(boolean checkUse)
    {
        _checkUse = checkUse;
    }

    public boolean isMinimalConversion()
    {
        return _minimalConversion;
    }

    public void setMinimalConversion(boolean _minimalConversion)
    {
        this._minimalConversion = _minimalConversion;
    }
}
