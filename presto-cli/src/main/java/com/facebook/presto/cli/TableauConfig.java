/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.cli;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

public class TableauConfig
{
    private String extractName;
    private boolean append;

    public String getExtractName()
    {
        return extractName;
    }

    @Config("com.tableausoftware.server.ServerConnection.extractName")
    @ConfigDescription("Set local extract name")
    public TableauConfig setExtractName(String extractName)
    {
        this.extractName = extractName;
        return this;
    }

    public boolean getAppend()
    {
        return append;
    }

    @Config("com.tableausoftware.server.ServerConnection.append")
    @ConfigDescription("Append data to the extract")
    public TableauConfig setAppend(boolean append)
    {
        this.append = append;
        return this;
    }
}
