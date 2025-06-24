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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools.rmi;

/**
 * Remote reference.  This class is internally used for sending a remote
 * reference through a network stream.
 */
public class RemoteRef implements java.io.Serializable {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    public int oid;
    public String classname;

    public RemoteRef(int i) {
        oid = i;
        classname = null;
    }

    public RemoteRef(int i, String name) {
        oid = i;
        classname = name;
    }
}
