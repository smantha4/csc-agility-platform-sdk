/*
 * Copyright (C) 2008-2013 ServiceMesh, Inc
 * Copyright (C) 2013-Present Computer Sciences Corporation
 */
package com.servicemesh.agility.api.service;

import java.util.Map;

import com.servicemesh.agility.api.Asset;
import com.servicemesh.agility.api.Envelope;
import com.servicemesh.agility.api.ImportReport;

/**
 * Extended service interface containing import related methods.
 */
public interface Importable
{
    ImportReport validate(String login, Asset parent, Map<String, Envelope> envelopeMap, Context context, Boolean create,
            Boolean update, String host) throws Exception;
}