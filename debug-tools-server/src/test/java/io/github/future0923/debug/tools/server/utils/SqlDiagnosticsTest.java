/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.server.utils;
import io.github.future0923.debug.tools.sql.SqlRecentBuffer;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SqlDiagnosticsTest {
    @Test void normalizesLiteralsButPreservesIdentifiersAndQuotedNames() {
        assertEquals("select col12, `column5`, \"a1\" from t where id=? and text=?", SqlDiagnostics.normalize("select col12, `column5`, \"a1\" from t /*note*/ where id=12 and text='O''Reilly'"));
        assertEquals("select ?", SqlDiagnostics.normalize("select 12 -- ignored"));
    }
    @Test void aggregatesFullWindowAndRespectsTimeAndRanking() {
        List<SqlRecentBuffer.Record> rows = Arrays.asList(new SqlRecentBuffer.Record(100,"select 1",10,"mysql","app"),
            new SqlRecentBuffer.Record(101,"select 2",30,"mysql","app"), new SqlRecentBuffer.Record(102,"update t set x=1",70,"mysql","app"),
            new SqlRecentBuffer.Record(1,"delete from t",999,"mysql","app"));
        Map<String,Object> result = SqlDiagnostics.aggregate(rows, 100, 1, "TOTAL");
        assertEquals(3, result.get("sampleCount")); assertEquals(2, result.get("totalGroups"));
        Map<?,?> group = (Map<?,?>)((List<?>)result.get("groups")).get(0);
        assertEquals("update t set x=?", group.get("normalizedSql")); assertEquals(70L, group.get("totalMillis"));
        group = (Map<?,?>)((List<?>)SqlDiagnostics.aggregate(rows,100,1,"COUNT").get("groups")).get(0);
        assertEquals(2L, group.get("count")); assertEquals(20d, group.get("averageMillis"));
    }
}
