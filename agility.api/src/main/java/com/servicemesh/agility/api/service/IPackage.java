package com.servicemesh.agility.api.service;

import com.servicemesh.agility.api.Package;
import com.servicemesh.agility.api.Script;
import com.servicemesh.agility.api.Task;

/**
 * Exposes additional operations to manage packages.
 */
public interface IPackage
{

    /**
     * Attach a script to the specified package as an instance script.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public boolean attachInstallScript(Package pkg, Script script) throws Exception;

    /**
     * Attach a script to the specified package as an operational script.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public boolean attachOperationalScript(Package pkg, Script script) throws Exception;

    /**
     * Attach a script to the specified package as a startup script.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public boolean attachStartupScript(Package pkg, Script script) throws Exception;

    /**
     * Attach a script to the specified package as a shutdown script.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public boolean attachShutdownScript(Package pkg, Script script) throws Exception;

    /**
     * Attach a script to the specified package as a reconfigure script.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public boolean attachReconfigureScript(Package pkg, Script script) throws Exception;

    /**
     * Remove a script from the specified packages install scripts.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public Task removeInstallScript(Package pkg, Script script) throws Exception;

    /**
     * Remove a script from the specified packages operational scripts.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public Task removeOperationalScript(Package pkg, Script script) throws Exception;

    /**
     * Remove a script from the specified packages startup scripts.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public Task removeStartupScript(Package pkg, Script script) throws Exception;

    /**
     * Remove a script from the specified packages shutdown scripts.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public Task removeShutdownScript(Package pkg, Script script) throws Exception;

    /**
     * Remove a script from the specified packages reconfigure scripts.
     * 
     * @param pkg
     *            An instance of com.servicemesh.agility.api.Package
     * @param script
     *            An instance of com.servicemesh.agility.api.Script
     * @return Completion status
     * @throws Exception
     */
    public Task removeReconfigureScript(Package pkg, Script script) throws Exception;

}