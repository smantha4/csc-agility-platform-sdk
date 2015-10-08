/**
 * COPYRIGHT (C) 2008-2012 SERVICEMESH, INC.  ALL RIGHTS RESERVED.  CONFIDENTIAL AND PROPRIETARY. 
 * 
 * ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.     ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY SERVICEMESH IS STRICTLY PROHIBITED.
 * 
 */

package com.servicemesh.agility.api.service;

import java.util.List;

import com.servicemesh.agility.api.AccessList;
import com.servicemesh.agility.api.Cloud;
import com.servicemesh.agility.api.ConfigurationResource;
import com.servicemesh.agility.api.Image;
import com.servicemesh.agility.api.Instance;
import com.servicemesh.agility.api.Script;
import com.servicemesh.agility.api.ScriptStatus;
import com.servicemesh.agility.api.Snapshot;
import com.servicemesh.agility.api.Task;
import com.servicemesh.agility.api.Variable;

/**
 * Exposes operations on a virtual machine instance.
 */
public interface IImage {
	
	/**

	/**
	 * Returns all images matching the specified imageId.
   	 * @param	imageId	The imageId
   	 * @return				Array of matching images
	 * @throws Exception
	 */
	public Image[] lookupByImageId(Cloud cloud, String imageId) throws Exception;
	
}
