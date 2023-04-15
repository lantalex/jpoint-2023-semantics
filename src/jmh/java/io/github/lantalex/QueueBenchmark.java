package io.github.lantalex;

import io.github.lantalex.queue.ArrayBlockingQueueWrapper;
import io.github.lantalex.queue.SPSC_AcqRelQueue;
import io.github.lantalex.queue.SPSC_BoundedQueue;
import io.github.lantalex.queue.SPSC_VolatileQueue;
import org.openjdk.jmh.annotations.AuxCounters;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class QueueBenchmark {

        /*
            How to run:
            $ ./gradlew jmh

            For full results please check 'jmh/resources'.


            Results(x86), throughput:
            --------------------------------------------------------------
             SPSC_AcqRelQueue
             consume                          40062.434 ± 1242.892  ops/ms
             produce                          40382.974 ± 1355.826  ops/ms

             SPSC_VolatileQueue
             consume                          15623.528 ±  352.592  ops/ms
             produce                          15610.761 ±  324.206  ops/ms

             ArrayBlockingQueueWrapper
             consume                           9236.839 ±  706.034  ops/ms
             produce                           9006.371 ±  781.984  ops/ms


            Results(arm_v8), throughput:
            ---------------------------------------------------------------
             SPSC_AcqRelQueue
             consume                           14049.188 ±  696.037  ops/ms
             produce                           14072.743 ±  680.217  ops/ms

             SPSC_VolatileQueue
             consume                           10148.363 ±   59.605  ops/ms
             produce                           10148.363 ±   59.605  ops/ms

             ArrayBlockingQueueWrapper
             consume                            2662.944 ±   68.006  ops/ms
             produce                            2663.615 ±   67.897  ops/ms


            Results(arm_v7), throughput:
            ---------------------------------------------------------------
             SPSC_AcqRelQueue
             consume                               475.528 ±  7.898  ops/ms
             produce                               613.398 ±  7.703  ops/ms

             SPSC_VolatileQueue
             consume                               471.160 ± 11.018  ops/ms
             produce                               612.548 ± 11.153  ops/ms

             ArrayBlockingQueueWrapper
             consume                               423.043 ±  6.401  ops/ms
             produce                               547.552 ±  6.584  ops/ms

     */

    @Benchmark
    @Group()
    @GroupThreads()
    public void produce(BenchmarkState state, ProducerCounters counters) {
        if (!state.queue.tryProduce(state.element)) {
            counters.tryProduceFailed++;
            Thread.yield();
        } else {
            counters.tryProduceSucceed++;
            Blackhole.consumeCPU(state.producerTokens);
        }
    }

    @Benchmark
    @Group()
    @GroupThreads()
    public void consume(BenchmarkState state, ConsumerCounters counters) {
        if (!state.queue.tryConsume(state.consumerLogic)) {
            counters.tryConsumeFailed++;
            Thread.yield();
        } else {
            counters.tryConsumeSucceed++;
        }
    }

    @State(Scope.Group)
    public static class BenchmarkState {

        @Param(value = {"1023"})
        private int queueCapacity;

        @Param(value = {"5"})
        private int consumerTokens;

        @Param(value = {"5"})
        private int producerTokens;

        @Param(value = {"SPSC_VolatileQueue", "SPSC_AcqRelQueue", "ArrayBlockingQueueWrapper"})
        private String implementation;

        private SPSC_BoundedQueue<String> queue;
        private Consumer<String> consumerLogic;
        private String element;

        @Setup(Level.Trial)
        public void setup(final Blackhole bh) {
            queue = switch (implementation) {
                case "SPSC_VolatileQueue" -> new SPSC_VolatileQueue<>(queueCapacity);
                case "SPSC_AcqRelQueue" -> new SPSC_AcqRelQueue<>(queueCapacity);
                case "ArrayBlockingQueueWrapper" -> new ArrayBlockingQueueWrapper<>(queueCapacity);
                default -> throw new IllegalArgumentException("Unsupported queue: " + implementation);
            };

            consumerLogic = (element) -> {
                Blackhole.consumeCPU(consumerTokens);
                bh.consume(element);
            };

            element = String.valueOf(System.nanoTime());
        }

        @TearDown(Level.Iteration)
        public synchronized void tearDown() {
            queue.clear();
        }
    }

    @AuxCounters
    @State(Scope.Thread)
    public static class ConsumerCounters {
        public long tryConsumeFailed;
        public long tryConsumeSucceed;
    }

    @AuxCounters
    @State(Scope.Thread)
    public static class ProducerCounters {
        public long tryProduceFailed;
        public long tryProduceSucceed;
    }
}
