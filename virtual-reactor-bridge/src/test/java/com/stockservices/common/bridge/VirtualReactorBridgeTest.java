package com.stockservices.common.bridge;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class VirtualReactorBridgeTest {

    @Test
    void testOffloadToVirtual_executesOnVirtualThread() {
        Mono<String> resultMono = VirtualReactorBridge.offloadToVirtual(() -> {
            Thread.sleep(100); // Simulate blocking I/O
            Thread current = Thread.currentThread();
            assertTrue(current.isVirtual(), "Task should run on a virtual thread");
            return "virtual-result";
        });

        String result = resultMono.block();
        assertEquals("virtual-result", result);
    }

    @Test
    void testOffloadToReactive_returnsFuture() throws Exception {
        Mono<String> reactiveMono = Mono.just("reactive-result")
            .map(s -> {
                assertTrue(Thread.currentThread().isVirtual(), "Reactive execution should be on a virtual thread");
                return s.toUpperCase();
            });

        CompletableFuture<String> future = VirtualReactorBridge.offloadToReactive(reactiveMono);
        String result = future.get(2, TimeUnit.SECONDS);

        assertEquals("REACTIVE-RESULT", result);
    }

    @Test
    void testBlockReactive_blocksSuccessfully() {
        Mono<String> reactiveMono = Mono.just("blocking-result")
            .map(String::toUpperCase);

        String result = VirtualReactorBridge.blockReactive(reactiveMono);

        assertEquals("BLOCKING-RESULT", result);
    }

    @Test
    void testOffloadToReactive_withCallbacks_onSuccess() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> resultRef = new AtomicReference<>();
        AtomicBoolean isVirtualRef = new AtomicBoolean(false);

        Mono<String> reactiveMono = Mono.just("success-result");

        VirtualReactorBridge.offloadToReactive(
            reactiveMono,
            result -> {
                resultRef.set(result);
                isVirtualRef.set(Thread.currentThread().isVirtual());
                latch.countDown();
            },
            error -> {
                fail("Should not hit error callback");
            }
        );

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Callback did not execute in time");
        assertEquals("success-result", resultRef.get());
        assertTrue(isVirtualRef.get(), "Callback should execute on a virtual thread");
    }

    @Test
    void testOffloadToReactive_withCallbacks_onError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        AtomicBoolean isVirtualRef = new AtomicBoolean(false);

        Mono<String> reactiveMono = Mono.error(new RuntimeException("Simulated failure"));

        VirtualReactorBridge.offloadToReactive(
            reactiveMono,
            result -> {
                fail("Should not hit success callback");
            },
            error -> {
                errorRef.set(error);
                isVirtualRef.set(Thread.currentThread().isVirtual());
                latch.countDown();
            }
        );

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Callback did not execute in time");
        assertNotNull(errorRef.get());
        assertEquals("Simulated failure", errorRef.get().getMessage());
        assertTrue(isVirtualRef.get(), "Error callback should execute on a virtual thread");
    }

    @Test
    void testOffloadIterableToVirtual_executesOnVirtualThread() {
        Flux<String> resultFlux = VirtualReactorBridge.offloadIterableToVirtual(() -> {
            Thread.sleep(100); // Simulate blocking I/O
            Thread current = Thread.currentThread();
            assertTrue(current.isVirtual(), "Task should run on a virtual thread");
            return Arrays.asList("item1", "item2");
        });

        List<String> result = resultFlux.collectList().block();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("item1"));
        assertTrue(result.contains("item2"));
    }

    @Test
    void testOffloadToReactive_fluxReturnsFutureList() throws Exception {
        Flux<String> reactiveFlux = Flux.just("item1", "item2")
            .map(s -> {
                assertTrue(Thread.currentThread().isVirtual(), "Reactive execution should be on a virtual thread");
                return s.toUpperCase();
            });

        CompletableFuture<List<String>> future = VirtualReactorBridge.offloadToReactive(reactiveFlux);
        List<String> result = future.get(2, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ITEM1", result.get(0));
        assertEquals("ITEM2", result.get(1));
    }

    @Test
    void testBlockReactive_fluxBlocksSuccessfullyAndReturnsList() {
        Flux<String> reactiveFlux = Flux.just("blocking1", "blocking2")
            .map(String::toUpperCase);

        List<String> result = VirtualReactorBridge.blockReactive(reactiveFlux);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BLOCKING1", result.get(0));
        assertEquals("BLOCKING2", result.get(1));
    }

    @Test
    void testOffloadToReactive_fluxWithCallbacks_onSuccess() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<List<String>> resultRef = new AtomicReference<>();
        AtomicBoolean isVirtualRef = new AtomicBoolean(false);

        Flux<String> reactiveFlux = Flux.just("success1", "success2");

        VirtualReactorBridge.offloadToReactive(
            reactiveFlux,
            result -> {
                resultRef.set(result);
                isVirtualRef.set(Thread.currentThread().isVirtual());
                latch.countDown();
            },
            error -> {
                fail("Should not hit error callback");
            }
        );

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Callback did not execute in time");
        assertNotNull(resultRef.get());
        assertEquals(2, resultRef.get().size());
        assertTrue(isVirtualRef.get(), "Callback should execute on a virtual thread");
    }

    @Test
    void testOffloadToReactive_fluxWithCallbacks_onError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        AtomicBoolean isVirtualRef = new AtomicBoolean(false);

        Flux<String> reactiveFlux = Flux.error(new RuntimeException("Simulated flux failure"));

        VirtualReactorBridge.offloadToReactive(
            reactiveFlux,
            result -> {
                fail("Should not hit success callback");
            },
            error -> {
                errorRef.set(error);
                isVirtualRef.set(Thread.currentThread().isVirtual());
                latch.countDown();
            }
        );

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Callback did not execute in time");
        assertNotNull(errorRef.get());
        assertEquals("Simulated flux failure", errorRef.get().getMessage());
        assertTrue(isVirtualRef.get(), "Error callback should execute on a virtual thread");
    }
}
