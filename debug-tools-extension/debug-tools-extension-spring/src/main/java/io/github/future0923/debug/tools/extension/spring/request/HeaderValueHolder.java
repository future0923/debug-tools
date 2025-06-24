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
package io.github.future0923.debug.tools.extension.spring.request;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author future0923
 */
class HeaderValueHolder {

    private final List<Object> values = new LinkedList<>();


    void setValue(@Nullable Object value) {
        this.values.clear();
        if (value != null) {
            this.values.add(value);
        }
    }

    void addValue(Object value) {
        this.values.add(value);
    }

    void addValues(Collection<?> values) {
        this.values.addAll(values);
    }

    void addValueArray(Object values) {
        CollectionUtils.mergeArrayIntoCollection(values, this.values);
    }

    List<Object> getValues() {
        return Collections.unmodifiableList(this.values);
    }

    List<String> getStringValues() {
        List<String> stringList = new ArrayList<>(this.values.size());
        for (Object value : this.values) {
            stringList.add(value.toString());
        }
        return Collections.unmodifiableList(stringList);
    }

    @Nullable
    Object getValue() {
        return (!this.values.isEmpty() ? this.values.get(0) : null);
    }

    @Nullable
    String getStringValue() {
        return (!this.values.isEmpty() ? String.valueOf(this.values.get(0)) : null);
    }

    @Override
    public String toString() {
        return this.values.toString();
    }

}
