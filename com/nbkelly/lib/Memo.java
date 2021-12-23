package com.nbkelly.lib;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memo<T, U> {
    private Memo() {}
    private final ConcurrentHashMap<T,U> map = new ConcurrentHashMap<T,U>();

    private Function<T,U> apply(final Function<T, U> f) {
	return input -> map.computeIfAbsent(input, f::apply);
    }

    public static <T,U> Function<T,U> memo(final Function<T,U> f) {
	return new Memo<T,U>().apply(f);
    }
}
