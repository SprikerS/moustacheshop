package dev.sprikers.moustacheshop.interfaces;

import java.io.IOException;

@FunctionalInterface
public interface ApiCallable<T> {
    T call() throws IOException, InterruptedException;
}
