//    This file is part of Penn TotalRecall <http://memory.psych.upenn.edu/TotalRecall>.
//
//    TotalRecall is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, version 3 only.
//
//    TotalRecall is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with TotalRecall.  If not, see <http://www.gnu.org/licenses/>.

package util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that visits each element of an array, in either increasing or decreasing order, 
 * resolving boundary limitations by "looping" to the other side of the array.
 * 
 * In the increasing order case, traversal starts at index x, ends at index x - 1, and the first element of the array is visited after the last.
 * In the decreasing order case, traversal starts at index x, ends at index x + 1, and the last element of the array is visited after the first.
 * 
 * Does not support remove().
 * 
 * @author Yuvi Masory
 *
 * @param <T> The type of the array to be iterated over
 */
public class LoopIterator<T> implements Iterator<T> {

	private boolean hasMore;
	private boolean forward; //direction of traversal

	private int originalIndex;
	private int curIndex;

	private T[] arr;

	/**
	 * Constructs an iterator with the provided input.
	 * 
	 * The first element to be returned will be arr[index].
	 * 
	 * @param arr The array to be iterated over
	 * @param index The index at which to begin iteration
	 * @param forward Whether iteration will take place in increasing order, decreasing order otherwise
	 * @throws IllegalArgumentException if the array is null or if index is not in bounds
	 */
	public LoopIterator(T[] arr, int index, boolean forward) {
		if(arr == null) {
			throw new IllegalArgumentException("array cannot be null");
		}
		if(index < 0 || index > (arr.length - 1)) {
			throw new IllegalArgumentException("provided index not in bounds");
		}
		this.forward = forward;
		this.arr = arr;
		this.originalIndex = index;
		this.curIndex = index;
		this.hasMore = true;
	}

	/**
	 * Determines if there are more elements to return.
	 * If <tt>false</tt> is returned, calling next() will lead to a NoSuchElementException.
	 * 
	 * @return <tt>true</tt> if the iteration has more elements
	 */
	public boolean hasNext() {
		return hasMore;
	}

	/**
	 * Returns the next element in the iteration, if available.
	 * 
	 * @return T The next element
	 * @throws NoSuchElementException If there are no more elements to return
	 */
	public T next() {
		if(hasMore == false) {
			throw new NoSuchElementException("there are no more elements");
		}
		int tmp = curIndex;
		if(forward) {
			if(curIndex >= (arr.length - 1)) {
				curIndex = 0;
			}
			else {
				curIndex++;
			}
		}
		else {
			if(curIndex == 0) {
				curIndex = arr.length - 1;
			}
			else {
				curIndex--;
			}
		}
		if(curIndex == originalIndex) {
			hasMore = false;
		}
		return arr[tmp];
	}

	/**
	 * Unsupported.
	 * 
	 * @throws UnsupportedOperationException In all cases
	 */
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
	}
}