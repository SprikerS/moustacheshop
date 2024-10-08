package dev.sprikers.moustacheshop.interfaces;

@FunctionalInterface
public interface ApiCallable<T> {
    T call() throws Exception;
}
