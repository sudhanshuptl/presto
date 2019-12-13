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

import com.tableau.hyperapi.Connection;
import com.tableau.hyperapi.CreateMode;
import com.tableau.hyperapi.HyperException;
import com.tableau.hyperapi.HyperProcess;
import com.tableau.hyperapi.Inserter;
import com.tableau.hyperapi.SchemaName;
import com.tableau.hyperapi.SqlType;
import com.tableau.hyperapi.TableDefinition;
import com.tableau.hyperapi.TableName;
import com.tableau.hyperapi.Telemetry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static io.airlift.units.Duration.nanosSince;
import static java.util.Objects.requireNonNull;

public class HyperPrinter
        implements OutputPrinter
{
    private final List<String> fieldNames;
    private final List<String> fieldTypes;
    private int totalRows;
    private String hyperFile;
    private long hyperStart;
    private HyperProcess hyperProcess;
    private Connection hyperServerConnection;
    private TableDefinition tableDef;
    private Inserter inserter;

    public HyperPrinter(List<String> fieldNames, List<String> fieldTypes, TableauConfig tableauConfig) throws HyperException
    {
        requireNonNull(fieldNames, "fieldNames is null");
        requireNonNull(fieldTypes, "fieldTypes is null");
        this.fieldNames = com.google.common.collect.ImmutableList.copyOf(fieldNames);
        this.fieldTypes = com.google.common.collect.ImmutableList.copyOf(fieldTypes);
        this.hyperFile = tableauConfig.getExtractName();

        java.io.File hyperFilePath = new java.io.File(hyperFile);
        if (hyperFilePath.exists()) {
            System.out.println("Overwriting existing extract");
            hyperFilePath.delete();
        }

        this.hyperStart = System.nanoTime();
        this.totalRows = 0;

        // Starting HyperServer and getting a Connection

        // Starts the Hyper Process with telemetry enabled to send data to Tableau.
        // To opt out, simply set telemetry=Telemetry.DO_NOT_SEND_USAGE_DATA_TO_TABLEAU.
        hyperProcess = new HyperProcess(Telemetry.DO_NOT_SEND_USAGE_DATA_TO_TABLEAU, "Hyper_Share");

        // Creates new Hyper file
        // Replaces file with CreateMode.CREATE_AND_REPLACE if it already exists
        hyperServerConnection = new Connection(hyperProcess.getEndpoint(), hyperFile, CreateMode.CREATE_AND_REPLACE);
        // Adding Schema Extract
        hyperServerConnection.getCatalog().createSchema(new SchemaName("Extract"));
        // Create TableDef
        tableDef = makeTableDefinition();
        hyperServerConnection.getCatalog().createTable(tableDef);

        //Create cursor of type Inserter
        inserter = new Inserter(hyperServerConnection, this.tableDef);
    }

    public TableDefinition makeTableDefinition() throws HyperException
    {
        // Create the table definition
        System.out.println("Table Definition:");
        ArrayList<TableDefinition.Column> columnTypes = new ArrayList<TableDefinition.Column>(){};
        for (int i = 0; i < fieldNames.size(); i++) {
            switch (fieldTypes.get(i)){
                case "varchar":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.text()));
                    System.out.println("[String] " + fieldNames.get(i));
                    break;
                case "double":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.doublePrecision()));
                    System.out.println("[Double] " + fieldNames.get(i));
                    break;
                case "bigint":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.bigInt()));
                    System.out.println("[Integer] " + fieldNames.get(i));
                    break;
                case "integer":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.integer()));
                    System.out.println("[Integer] " + fieldNames.get(i));
                    break;
                case "boolean":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.bool()));
                    System.out.println("[Boolean] " + fieldNames.get(i));
                    break;
                case "date":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.date()));
                    System.out.println("[Date] " + fieldNames.get(i));
                    break;
                case "timestamp":
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.timestamp()));
                    System.out.println("[Datetime] " + fieldNames.get(i));
                    break;
                default:
                    columnTypes.add(new TableDefinition.Column(fieldNames.get(i), SqlType.text()));
                    System.out.println("[" + fieldTypes.get(i) + " -> String] " + fieldNames.get(i));
                    break;
            }
        }
        return new TableDefinition(new TableName("Extract", "Extract"), columnTypes);
    }

    @Override
    public void printRows(List<List<?>> rows, boolean complete) throws HyperException
    {
        try {
            for (List<?> row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    Object curVal = row.get(i);
                    if (curVal == null) {
                        inserter.addNull();
                    }
                    else {
                        switch (fieldTypes.get(i)){
                            case "double":
                                inserter.add((double) curVal);
                                break;
                            case "bigint":
                                inserter.add((long) curVal);
                                break;
                            case "integer":
                                inserter.add((int) curVal);
                                break;
                            case "boolean":
                                inserter.add((boolean) curVal);
                                break;
                            case "date":
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date parsedDate = dateFormat.parse((String) curVal);
                                Calendar calDate = Calendar.getInstance();
                                calDate.setTime(parsedDate);
                                inserter.addDate(calDate.get(Calendar.YEAR), calDate.get(Calendar.MONTH) + 1, calDate.get(Calendar.DAY_OF_MONTH));
                                break;
                            case "timestamp":
                                SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                Date parsedDatetime = datetimeFormat.parse((String) curVal);
                                Calendar calDatetime = Calendar.getInstance();
                                calDatetime.setTime(parsedDatetime);
                                inserter.addTimestamp(calDatetime.get(Calendar.YEAR), calDatetime.get(Calendar.MONTH) + 1, calDatetime.get(Calendar.DAY_OF_MONTH), calDatetime.get(Calendar.HOUR_OF_DAY), calDatetime.get(Calendar.MINUTE), calDatetime.get(Calendar.SECOND), calDatetime.get(Calendar.MILLISECOND) * 10);
                                break;
                            default:
                                inserter.add(curVal.toString());
                                break;
                        }
                    }
                }
                inserter.endRow();
            }
            totalRows += rows.size();
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    public void finish()
            throws HyperException
    {
        //commit insert & close inserter
        inserter.execute();
        System.out.println(totalRows + " rows inserted in " + nanosSince(hyperStart));
        inserter.close();
        System.out.println("Inserter Is closed");

        // close connection
        hyperServerConnection.close();
        System.out.println("The connection to the Hyper file has been closed");

        // Stop HyperProcess Server
        System.out.println("The Hyper process has been shut down");
    }
}
