package io.github.lantalex.queue;

import io.github.lantalex.PaddedAtomicInteger;

import java.util.Arrays;
import java.util.function.Consumer;

public class SPSC_VolatileQueue<E> implements SPSC_BoundedQueue<E> {

    private final PaddedAtomicInteger consumerIdx = new PaddedAtomicInteger(0);
    private final PaddedAtomicInteger producerIdx = new PaddedAtomicInteger(0);
    private final E[] buffer;

    public SPSC_VolatileQueue(int capacity) {
        //noinspection unchecked
        this.buffer = (E[]) new Object[capacity + 1];
    }

    @Override
    public boolean tryProduce(E value) {
        int pIdx = producerIdx.getPlain();
        int cIdx = consumerIdx.getVolatile();

        if (isFull(cIdx, pIdx)) {
            return false;
        }

        buffer[pIdx] = value;

        producerIdx.setVolatile(next(pIdx));
        return true;
    }

    @Override
    public boolean tryConsume(Consumer<E> consumer) {
        int cIdx = consumerIdx.getPlain();
        int pIdx = producerIdx.getVolatile();

        if (isEmpty(cIdx, pIdx)) {
            return false;
        }

        consumer.accept(buffer[cIdx]);

        consumerIdx.setVolatile(next(cIdx));
        return true;
    }

    @Override
    public void clear() {
        Arrays.fill(buffer, null);
    }

    private int next(int idx) {
        return idx + 1 == buffer.length ? 0 : idx + 1;
    }

    private boolean isFull(int consumerIdx, int producerIdx) {
        return next(producerIdx) == consumerIdx;
    }

    private boolean isEmpty(int consumerIdx, int producerIdx) {
        return producerIdx == consumerIdx;
    }
}
