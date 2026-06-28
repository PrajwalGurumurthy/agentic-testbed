package com.stockservices.common.bridge;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import java.util.concurrent.Executor;

public class VirtualReactorBridge {

    // Shared virtual thread executor to avoid repeated instantiation
    private static final Executor VIRTUAL_THREAD_EXECUTOR =
        Executors.newVirtualThreadPerTaskExecutor();

    // Scheduler that executes tasks on a new virtual thread for each task
    private static final Scheduler VIRTUAL_THREAD_SCHEDULER =
        Schedulers.fromExecutor(VIRTUAL_THREAD_EXECUTOR);

    /**
     * Offloads a blocking task to a Virtual Thread and returns a Reactive Mono.
     * This solves Reactive -> Virtual Thread handoff.
     *
     * @param blockingTask The blocking task to execute.
     * @param <T>          The type of the result.
     * @return A Mono that emits the result of the blocking task.
     */
    public static <T> Mono<T> offloadToVirtual(Callable<T> blockingTask) {
        return Mono.fromCallable(blockingTask)
                   .subscribeOn(VIRTUAL_THREAD_SCHEDULER);
    }

    /**
     * Offloads a blocking task returning an Iterable to a Virtual Thread and returns a Reactive Flux.
     *
     * @param blockingTask The blocking task to execute.
     * @param <T>          The type of the elements in the Iterable.
     * @return A Flux that emits the elements of the Iterable.
     */
    public static <T> Flux<T> offloadIterableToVirtual(Callable<Iterable<T>> blockingTask) {
        return Mono.fromCallable(blockingTask)
                   .subscribeOn(VIRTUAL_THREAD_SCHEDULER)
                   .flatMapMany(Flux::fromIterable);
    }

    /**
     * Executes a Reactive Mono and returns a CompletableFuture.
     * If called from a platform thread, the reactive pipeline subscription and execution
     * are offloaded to a virtual thread. The caller can decide whether to block (join)
     * or handle the result asynchronously.
     *
     * @param reactivePublisher The Mono to execute.
     * @param <T>               The type of the result.
     * @return A CompletableFuture containing the result.
     */
    public static <T> CompletableFuture<T> offloadToReactive(Mono<T> reactivePublisher) {
        // If we are already on a virtual thread, we can just use the publisher directly.
        // However, to ensure safety and consistent behavior (offloading the reactive
        // machinery to a VT if on a platform thread), we publishOn/subscribeOn the VT scheduler.
        return reactivePublisher
                .subscribeOn(VIRTUAL_THREAD_SCHEDULER)
                .toFuture();
    }

    /**
     * Executes a Reactive Flux and returns a CompletableFuture containing a List of all emitted items.
     * If called from a platform thread, the reactive pipeline subscription and execution
     * are offloaded to a virtual thread.
     *
     * @param reactivePublisher The Flux to execute.
     * @param <T>               The type of the result elements.
     * @return A CompletableFuture containing the collected List of results.
     */
    public static <T> CompletableFuture<List<T>> offloadToReactive(Flux<T> reactivePublisher) {
        return reactivePublisher
                .collectList()
                .subscribeOn(VIRTUAL_THREAD_SCHEDULER)
                .toFuture();
    }

    /**
     * Executes a Reactive Mono, blocking the current thread until the result is available.
     * This will block efficiently if the current thread is a Virtual Thread.
     *
     * @param reactivePublisher The Mono to execute.
     * @param <T>               The type of the result.
     * @return The result of the Mono.
     */
    public static <T> T blockReactive(Mono<T> reactivePublisher) {
        return offloadToReactive(reactivePublisher).join();
    }

    /**
     * Executes a Reactive Flux, blocking the current thread until the result is available
     * as a collected List. This will block efficiently if the current thread is a Virtual Thread.
     *
     * @param reactivePublisher The Flux to execute.
     * @param <T>               The type of the result elements.
     * @return A List of all emitted items.
     */
    public static <T> List<T> blockReactive(Flux<T> reactivePublisher) {
        return offloadToReactive(reactivePublisher).join();
    }

    /**
     * Executes a Reactive Mono asynchronously on a virtual thread, and invokes
     * the appropriate callback upon completion or error.
     *
     * @param reactivePublisher The Mono to execute.
     * @param successCallback   Callback invoked with the result on success.
     * @param errorCallback     Callback invoked with the exception on error.
     * @param <T>               The type of the result.
     */
    public static <T> void offloadToReactive(
            Mono<T> reactivePublisher,
            Consumer<T> successCallback,
            Consumer<Throwable> errorCallback) {

        offloadToReactive(reactivePublisher).whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                if (errorCallback != null) {
                    errorCallback.accept(throwable);
                }
            } else {
                if (successCallback != null) {
                    successCallback.accept(result);
                }
            }
        }, VIRTUAL_THREAD_EXECUTOR);
    }

    /**
     * Executes a Reactive Flux asynchronously on a virtual thread, collects the items into a List,
     * and invokes the appropriate callback upon completion or error.
     *
     * @param reactivePublisher The Flux to execute.
     * @param successCallback   Callback invoked with the collected List on success.
     * @param errorCallback     Callback invoked with the exception on error.
     * @param <T>               The type of the result elements.
     */
    public static <T> void offloadToReactive(
            Flux<T> reactivePublisher,
            Consumer<List<T>> successCallback,
            Consumer<Throwable> errorCallback) {

        offloadToReactive(reactivePublisher).whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                if (errorCallback != null) {
                    errorCallback.accept(throwable);
                }
            } else {
                if (successCallback != null) {
                    successCallback.accept(result);
                }
            }
        }, VIRTUAL_THREAD_EXECUTOR);
    }
}
