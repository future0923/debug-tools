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
package io.github.future0923.debug.tools.sql;

import io.github.future0923.debug.tools.base.hutool.sql.SqlCompressor;
import org.junit.jupiter.api.Test;

/**
 * @author future0923
 */
class SqlCompressorTest {

    @Test
    void compressSql() {
        String sql = "SELECT * \n" +
                "FROM user  -- 用户表\n" +
                "WHERE name = '张 三'  \n" +
                "  AND age > 18 \n" +
                "  /* 这里是块注释，描述条件 */\n" +
                "  AND remark = \"备注 换行\"\n";

        String compressed = SqlCompressor.compressSql(sql);
        System.out.println(compressed);
    }
}