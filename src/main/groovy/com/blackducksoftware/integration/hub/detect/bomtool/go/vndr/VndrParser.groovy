/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.go.vndr

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool

import groovy.transform.TypeChecked

@TypeChecked
class VndrParser {

    public ExternalIdFactory externalIdFactory
    public VndrParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory
    }

    public DependencyGraph parseVendorConf(List<String> vendorConfContents) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph()
        //TODO test against moby
        vendorConfContents.each { String line ->
            if (line?.trim() && !line.startsWith('#')) {
                def parts = line.split(' ')

                final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(GoDepBomTool.GOLANG, parts[0], parts[1])
                final Dependency dependency = new Dependency(parts[0], parts[1], dependencyExternalId)
                graph.addChildToRoot(dependency)
            }
        }

        return graph
    }
}
