package jk.aoc.util;

import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class InfiniteIterator<T> implements Iterator<T> {
    private final @NonNull Iterable<T> iterable;
    private Iterator<T> currentIterator;

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        if (currentIterator == null || !currentIterator.hasNext()) {
            currentIterator = iterable.iterator();
        }

        return currentIterator.next();
    }

}
