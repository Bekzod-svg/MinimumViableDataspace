/*
 *  Copyright (c) 2024 Materna Information & Communications SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Materna Information & Communications SE - Initial Implementation
 *
 */

package org.eclipse.edc.extension.fc;

import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.extension.fc.directory.InMemoryNodeDirectory;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;


/**
 * Provides default service implementations for fallback
 * Omitted {link org.eclipse.edc.runtime.metamodel.annotation.Extension since there this module already contains {link FederatedCatalogCacheExtension} }
 */
public class FederatedCatalogServicesExtension implements ServiceExtension {

    @Inject
    protected Monitor monitor;

    @Setting
    private static final String NODES_FILE_PATH = "edc.nodes.file.path";

    @Setting
    private static final String NODES_URL = "edc.nodes.url";
    
    @Provider
    public TargetNodeDirectory defaultNodeDirectory(ServiceExtensionContext context) {
        var nodesFilePath = context.getConfig().getString(NODES_FILE_PATH, null);
        var nodesUrl = context.getConfig().getString(NODES_URL, null);

        return new InMemoryNodeDirectory(nodesFilePath, nodesUrl, monitor);
    }
} 
