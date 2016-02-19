/*
 * Copyright (C) 2016 Computer Science Corporation
 * All rights reserved.
 *
 */
package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Project;
import com.servicemesh.agility.api.ProjectCreateRequest;
import com.servicemesh.agility.api.ProjectTemplate;

/**
 * @author akofman
 */
public interface IProjectTemplate
{
    /**
     * Use to mark project template as ready for consumption
     *
     * @param projectTemplate
     *            to mark ready for consumption
     * @return Project template marked as ready for consumption
     * @throws Exception
     */
    public ProjectTemplate setReadyForUse(ProjectTemplate projectTemplate) throws Exception;

    /**
     * Use to mark project template as not ready for consumption
     *
     * @param projectTemplate
     *            to mark as not ready for consumption
     * @return Project template marked as not ready for consumption
     * @throws Exception
     */
    public ProjectTemplate unsetReadyForUse(ProjectTemplate projectTemplate) throws Exception;

    /**
     * Create project based off project template. Project template attributes, structure and assets are copied to the project
     * created.
     *
     * @param request
     *            contains project name, description, project template reference, location and properties
     * @return Project created off specified project create request.
     * @throws Exception
     */
    public Project createProject(ProjectCreateRequest request) throws Exception;

}
