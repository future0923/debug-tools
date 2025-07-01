--
-- Copyright (C) 2024-2025 the original author or authors.
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.
--

-- mysql
CREATE TABLE dp_user
(
    id      int NOT NULL,
    name    varchar(255) DEFAULT NULL,
    age     int          DEFAULT NULL,
    version int          DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;


-- postgresql
CREATE TABLE dp_user
(
    id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255),
    age     INTEGER,
    version INTEGER
);


-- sqlserver
CREATE TABLE dp_user
(
    id      INT          NOT NULL PRIMARY KEY,
    name    VARCHAR(255) NULL,
    age     INT          NULL,
    version INT          NULL
);


-- oracle
CREATE TABLE SYSTEM.dp_user
(
    id NUMBER PRIMARY KEY,
    name VARCHAR2 (255),
    age NUMBER,
    version NUMBER
);