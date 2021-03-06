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
package com.facebook.presto.benchmark.framework;

import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class BenchmarkQuery
{
    private final String querySet;
    private final String name;
    private final String query;
    private final String catalog;
    private final String schema;

    @JdbiConstructor
    public BenchmarkQuery(
            @ColumnName("query_set") String querySet,
            @ColumnName("name") String name,
            @ColumnName("query") String query,
            @ColumnName("catalog") String catalog,
            @ColumnName("schema") String schema)
    {
        this.querySet = requireNonNull(querySet, "querySet is null");
        this.name = requireNonNull(name, "name is null");
        this.query = clean(query);
        this.catalog = requireNonNull(catalog, "catalog is null");
        this.schema = requireNonNull(schema, "schema is null");
    }

    public String getQuerySet()
    {
        return querySet;
    }

    public String getName()
    {
        return name;
    }

    public String getQuery()
    {
        return query;
    }

    public String getCatalog()
    {
        return catalog;
    }

    public String getSchema()
    {
        return schema;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BenchmarkQuery o = (BenchmarkQuery) obj;
        return Objects.equals(querySet, o.querySet) &&
                Objects.equals(name, o.name) &&
                Objects.equals(query, o.query) &&
                Objects.equals(catalog, o.catalog) &&
                Objects.equals(schema, o.schema);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(querySet, name, query, catalog, schema);
    }

    private static String clean(String sql)
    {
        sql = sql.replaceAll("\t", "  ");
        sql = sql.replaceAll("\n+", "\n");
        sql = sql.trim();
        while (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }
        return sql;
    }
}
