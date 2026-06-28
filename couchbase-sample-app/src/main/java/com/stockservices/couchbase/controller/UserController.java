package com.stockservices.couchbase.controller;

import com.stockservices.common.bridge.VirtualReactorBridge;
import com.stockservices.couchbase.domain.User;
import com.stockservices.couchbase.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- MONO EXAMPLES ---

    /**
     * Showcases blocking a Mono inside a Virtual Thread endpoint.
     */
    @PostMapping("/block")
    public ResponseEntity<User> saveUserBlocking(@RequestBody User user) {
        Mono<User> reactiveSave = userService.saveUser(user);
        // Blocks the virtual thread safely while reactive pipeline executes
        User savedUser = VirtualReactorBridge.blockReactive(reactiveSave);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Showcases offloading a Mono to reactive and returning a CompletableFuture.
     */
    @GetMapping("/{id}/future")
    public CompletableFuture<User> getUserFuture(@PathVariable String id) {
        Mono<User> reactiveGet = userService.getUser(id);
        // Offloads execution to virtual thread context and bridges to Future
        return VirtualReactorBridge.offloadToReactive(reactiveGet);
    }

    /**
     * Showcases offloading a Mono with callbacks.
     */
    @PostMapping("/callback")
    public DeferredResult<ResponseEntity<User>> saveUserCallback(@RequestBody User user) {
        DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();
        Mono<User> reactiveSave = userService.saveUser(user);

        VirtualReactorBridge.offloadToReactive(
                reactiveSave,
                savedUser -> deferredResult.setResult(ResponseEntity.ok(savedUser)),
                error -> deferredResult.setErrorResult(ResponseEntity.internalServerError().build())
        );

        return deferredResult;
    }


    // --- FLUX EXAMPLES ---

    /**
     * Showcases blocking a Flux and returning the collected List.
     */
    @GetMapping("/flux/block")
    public ResponseEntity<List<User>> getAllUsersBlocking() {
        Flux<User> reactiveGetAll = userService.getAllUsers();
        // Safely blocks the virtual thread, aggregates Flux into a List
        List<User> users = VirtualReactorBridge.blockReactive(reactiveGetAll);
        return ResponseEntity.ok(users);
    }

    /**
     * Showcases offloading a Flux and returning a CompletableFuture of a List.
     */
    @GetMapping("/flux/future")
    public CompletableFuture<List<User>> getAllUsersFuture() {
        Flux<User> reactiveGetAll = userService.getAllUsers();
        return VirtualReactorBridge.offloadToReactive(reactiveGetAll);
    }

    /**
     * Showcases offloading a Flux with callbacks returning a List.
     */
    @GetMapping("/flux/callback")
    public DeferredResult<ResponseEntity<List<User>>> getAllUsersCallback() {
        DeferredResult<ResponseEntity<List<User>>> deferredResult = new DeferredResult<>();
        Flux<User> reactiveGetAll = userService.getAllUsers();

        VirtualReactorBridge.offloadToReactive(
                reactiveGetAll,
                users -> deferredResult.setResult(ResponseEntity.ok(users)),
                error -> deferredResult.setErrorResult(ResponseEntity.internalServerError().build())
        );

        return deferredResult;
    }


    // --- LEGACY OFFLOAD SIMULATION ---

    /**
     * Simulates a legacy blocking call that we want to turn into a Reactive Mono
     * by executing it on a Virtual Thread.
     */
    @GetMapping("/legacy-simulate/mono")
    public Mono<String> simulateLegacyMono() {
        return VirtualReactorBridge.offloadToVirtual(() -> {
            // Simulate blocking IO
            Thread.sleep(100);
            return "legacy-mono-result";
        });
    }

    /**
     * Simulates a legacy blocking call returning an Iterable that we want to turn
     * into a Reactive Flux by executing it on a Virtual Thread.
     */
    @GetMapping("/legacy-simulate/flux")
    public Flux<String> simulateLegacyFlux() {
        return VirtualReactorBridge.offloadIterableToVirtual(() -> {
            // Simulate blocking IO
            Thread.sleep(100);
            return Arrays.asList("legacy-flux-1", "legacy-flux-2");
        });
    }
}
