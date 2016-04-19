package org.github.rubenqba.maven.ddlutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.io.DatabaseDataIO;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.github.rubenqba.maven.ddlutils.PlatformBuilder.DBType;

/**
 * Generate database ddl schema
 *
 * @goal generate-ddl
 * 
 * @phase process-sources
 */
public class GenerateDdlMojo extends AbstractMojo {
    /**
     * Location of the schema file.
     * 
     * @parameter
     * @required
     */
    private String schemaFile;

    /**
     * Location of the data file.
     * 
     * @parameter
     * @required
     */
    private String dataFile;

    /**
     * Platform type
     * 
     * @parameter
     * @required
     */
    private String platformType;

    /**
     * Database name
     * 
     * @parameter
     * @required
     */
    private String database;

    /**
     * Datasource username
     * 
     * @parameter
     * @required
     */
    private String username;

    /**
     * Datasource password
     * 
     * @parameter
     * @required
     */
    private String password;

    @Override
    public void execute() throws MojoExecutionException {
        Platform pf = PlatformBuilder.builder()
                .type("firebird".equalsIgnoreCase(platformType) ? DBType.FIREBIRD : DBType.ORACLE)
                .databaseName(database)
                .user(username)
                .password(password)
                .build();
        Database db = pf.readModelFromDatabase(null);

        try {
            Files.createDirectories(Paths.get(schemaFile).getParent());
            new DatabaseIO().write(db, schemaFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating schema file", e);
        }

        try {
            if (StringUtils.isNotBlank(dataFile)) {
                Files.createDirectories(Paths.get(dataFile).getParent());
                new DatabaseDataIO().writeDataToXML(pf, db, dataFile, "UTF-8");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating data file", e);
        }

    }

}
