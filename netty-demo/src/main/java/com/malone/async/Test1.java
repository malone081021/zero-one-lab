//package com.malone.async;
//
//import com.google.common.util.concurrent.Futures;
//import com.google.common.util.concurrent.ListeningExecutorService;
//import com.google.common.util.concurrent.MoreExecutors;
//
//import java.util.concurrent.Executors;
//
//public class Test1 {
//    public static void main(String[] args) {
//
//        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
//        ListenableFuture<Explosion> explosion = service.submit(new Callable<Explosion>() {
//            public Explosion call() {
//                return pushBigRedButton();
//            }
//        });
//        Futures.addCallback(explosion, new FutureCallback<Explosion>() {
//            // we want this handler to run immediately after we push the big red button!
//            public void onSuccess(Explosion explosion) {
//                walkAwayFrom(explosion);
//            }
//            public void onFailure(Throwable thrown) {
//                battleArchNemesis(); // escaped the explosion!
//            }
//        });
//    }
//}
