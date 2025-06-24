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
package io.github.future0923.debug.tools.base.hutool.core.map.multi;

import io.github.future0923.debug.tools.base.hutool.core.collection.IterUtil;
import io.github.future0923.debug.tools.base.hutool.core.collection.TransIter;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 抽象{@link Table}接口实现<br>
 * 默认实现了：
 * <ul>
 *     <li>{@link #equals(Object)}</li>
 *     <li>{@link #hashCode()}</li>
 *     <li>{@link #toString()}</li>
 *     <li>{@link #values()}</li>
 *     <li>{@link #cellSet()}</li>
 *     <li>{@link #iterator()}</li>
 * </ul>
 *
 * @param <R> 行类型
 * @param <C> 列类型
 * @param <V> 值类型
 * @author Guava, Looly
 * @since 5.7.23
 */
public abstract class AbsTable<R, C, V> implements Table<R, C, V> {

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Table) {
			final Table<?, ?, ?> that = (Table<?, ?, ?>) obj;
			return this.cellSet().equals(that.cellSet());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return cellSet().hashCode();
	}

	@Override
	public String toString() {
		return rowMap().toString();
	}

	//region values
	@Override
	public Collection<V> values() {
		Collection<V> result = values;
		return (result == null) ? values = new Values() : result;
	}

	private Collection<V> values;
	private class Values extends AbstractCollection<V> {
		@Override
		public Iterator<V> iterator() {
			return new TransIter<>(cellSet().iterator(), Cell::getValue);
		}

		@Override
		public boolean contains(Object o) {
			//noinspection unchecked
			return containsValue((V) o);
		}

		@Override
		public void clear() {
			AbsTable.this.clear();
		}

		@Override
		public int size() {
			return AbsTable.this.size();
		}
	}
	//endregion

	//region cellSet
	@Override
	public Set<Cell<R, C, V>> cellSet() {
		Set<Cell<R, C, V>> result = cellSet;
		return (result == null) ? cellSet = new CellSet() : result;
	}

	private Set<Cell<R, C, V>> cellSet;

	private class CellSet extends AbstractSet<Cell<R, C, V>> {
		@Override
		public boolean contains(Object o) {
			if (o instanceof Cell) {
				@SuppressWarnings("unchecked") final Cell<R, C, V> cell = (Cell<R, C, V>) o;
				Map<C, V> row = getRow(cell.getRowKey());
				if (null != row) {
					return ObjectUtil.equals(row.get(cell.getColumnKey()), cell.getValue());
				}
			}
			return false;
		}

		@Override
		public boolean remove(Object o) {
			if (contains(o)) {
				@SuppressWarnings("unchecked") final Cell<R, C, V> cell = (Cell<R, C, V>) o;
				AbsTable.this.remove(cell.getRowKey(), cell.getColumnKey());
			}
			return false;
		}

		@Override
		public void clear() {
			AbsTable.this.clear();
		}

		@Override
		public Iterator<Cell<R, C, V>> iterator() {
			return new CellIterator();
		}

		@Override
		public int size() {
			return AbsTable.this.size();
		}
	}
	//endregion

	//region iterator
	@Override
	public Iterator<Cell<R, C, V>> iterator() {
		return new CellIterator();
	}

	/**
	 * 基于{@link Cell}的{@link Iterator}实现
	 */
	private class CellIterator implements Iterator<Cell<R, C, V>> {
		final Iterator<Map.Entry<R, Map<C, V>>> rowIterator = rowMap().entrySet().iterator();
		Map.Entry<R, Map<C, V>> rowEntry;
		Iterator<Map.Entry<C, V>> columnIterator = IterUtil.empty();

		@Override
		public boolean hasNext() {
			return rowIterator.hasNext() || columnIterator.hasNext();
		}

		@Override
		public Cell<R, C, V> next() {
			if (false == columnIterator.hasNext()) {
				rowEntry = rowIterator.next();
				columnIterator = rowEntry.getValue().entrySet().iterator();
			}
			final Map.Entry<C, V> columnEntry = columnIterator.next();
			return new SimpleCell<>(rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
		}

		@Override
		public void remove() {
			columnIterator.remove();
			if (rowEntry.getValue().isEmpty()) {
				rowIterator.remove();
			}
		}
	}
	//endregion

	/**
	 * 简单{@link Cell} 实现
	 *
	 * @param <R> 行类型
	 * @param <C> 列类型
	 * @param <V> 值类型
	 */
	private static class SimpleCell<R, C, V> implements Cell<R, C, V>, Serializable {
		private static final long serialVersionUID = 1L;

		private final R rowKey;
		private final C columnKey;
		private final V value;

		SimpleCell(R rowKey, C columnKey, V value) {
			this.rowKey = rowKey;
			this.columnKey = columnKey;
			this.value = value;
		}

		@Override
		public R getRowKey() {
			return rowKey;
		}

		@Override
		public C getColumnKey() {
			return columnKey;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof Cell) {
				Cell<?, ?, ?> other = (Cell<?, ?, ?>) obj;
				return ObjectUtil.equal(rowKey, other.getRowKey())
						&& ObjectUtil.equal(columnKey, other.getColumnKey())
						&& ObjectUtil.equal(value, other.getValue());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(rowKey, columnKey, value);
		}

		@Override
		public String toString() {
			return "(" + rowKey + "," + columnKey + ")=" + value;
		}
	}
}
