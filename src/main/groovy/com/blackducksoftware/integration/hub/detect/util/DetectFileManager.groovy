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
package com.blackducksoftware.integration.hub.detect.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.BomToolType

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DetectFileManager {
    private final Logger logger = LoggerFactory.getLogger(DetectFileManager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    FileFinder fileFinder

    private Set<File> directoriesToCleanup = new LinkedHashSet<>()

    public void cleanupDirectories() {
        if (directoriesToCleanup) {
            for (File directory : directoriesToCleanup) {
                FileUtils.deleteQuietly(directory)
            }
        }
    }

    File createDirectory(BomToolType bomToolType) {
        createDirectory(bomToolType.toString().toLowerCase(), true)
    }

    File createDirectory(String directoryName) {
        createDirectory(detectConfiguration.outputDirectory, directoryName, true)
    }

    File createDirectory(File directory, String newDirectoryName) {
        createDirectory(directory, newDirectoryName, true)
    }

    File createDirectory(String directoryName, boolean allowDelete) {
        createDirectory(detectConfiguration.outputDirectory, directoryName, allowDelete)
    }

    File createDirectory(File directory, String newDirectoryName, boolean allowDelete) {
        def newDirectory = new File(directory, newDirectoryName)
        newDirectory.mkdir()
        if (detectConfiguration.cleanupBomToolFiles && allowDelete) {
            directoriesToCleanup.add(newDirectory)
        }

        newDirectory
    }

    File createFile(File directory, String filename) {
        def newFile = new File(directory, filename)
        if (detectConfiguration.cleanupBomToolFiles) {
            newFile.deleteOnExit()
        }

        newFile
    }

    File createFile(BomToolType bomToolType, String filename) {
        File directory = createDirectory(bomToolType)
        createFile(directory, filename)
    }

    File writeToFile(File file, String contents) {
        writeToFile(file, contents, true)
    }

    File writeToFile(File file, String contents, boolean overwrite) {
        if (!file) {
            return null
        }
        if (overwrite) {
            file.delete()
        }
        if (file.exists()) {
            logger.info("${file.getAbsolutePath()} exists and not being overwritten")
        } else {
            file << contents
        }

        file
    }

    public String extractFinalPieceFromPath(String path) {
        if (path == null || path.length() == 0) {
            return ''
        }
        String normalizedPath = FilenameUtils.normalizeNoEndSeparator(path, true)
        normalizedPath[normalizedPath.lastIndexOf('/') + 1..-1]
    }

    boolean directoryExists(final String sourcePath, final String relativePath) {
        final File sourceDirectory = new File(sourcePath)
        final File relativeDirectory = new File(sourceDirectory, relativePath)
        return relativeDirectory.isDirectory()
    }

    public boolean containsAllFiles(String sourcePath, String... filenamePatterns) {
        return fileFinder.containsAllFiles(sourcePath, filenamePatterns)
    }

    public boolean containsAllFilesToDepth(String sourcePath, int maxDepth, String... filenamePatterns) {
        return fileFinder.containsAllFilesToDepth(sourcePath, maxDepth, filenamePatterns)
    }

    public File findFile(String sourcePath, String filenamePattern) {
        return fileFinder.findFile(sourcePath, filenamePattern)
    }

    public File findFile(File sourceDirectory, String filenamePattern) {
        return fileFinder.findFile(sourceDirectory, filenamePattern)
    }

    public File[] findFiles(File sourceDirectory, String filenamePattern) {
        return fileFinder.findFiles(sourceDirectory, filenamePattern)
    }

    public File[] findFilesToDepth(String sourceDirectory, String filenamePattern, int maxDepth) {
        return findFilesToDepth(new File(sourceDirectory), filenamePattern, maxDepth)
    }

    public File[] findFilesToDepth(File sourceDirectory, String filenamePattern, int maxDepth) {
        return fileFinder.findFilesToDepth(sourceDirectory, filenamePattern, maxDepth)
    }

    public File[] findDirectoriesContainingDirectoriesToDepth(String sourceDirectory, String filenamePattern, int maxDepth) {
        return fileFinder.findDirectoriesContainingDirectoriesToDepth(new File(sourceDirectory), filenamePattern, maxDepth)
    }

    public File[] findDirectoriesContainingDirectoriesToDepth(File sourceDirectory, String filenamePattern, int maxDepth) {
        return fileFinder.findDirectoriesContainingDirectoriesToDepth(sourceDirectory, filenamePattern, maxDepth)
    }

    public File[] findDirectoriesContainingFilesToDepth(File sourceDirectory, String filenamePattern, int maxDepth) {
        return fileFinder.findDirectoriesContainingFilesToDepth(sourceDirectory, filenamePattern, maxDepth)
    }
}
