package io.github.lantalex;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaddedAtomicIntegerTest {

    private static final int MIN_PADDING = 128;

    @Test
    void testPadding() {
        ClassLayout layout = ClassLayout.parseClass(PaddedAtomicInteger.class);

        AtomicLong currentPaddingSize = new AtomicLong();
        List<String> interestingFields = new ArrayList<>();
        List<Long> paddingSizes = new ArrayList<>();

        layout.fields().forEach(field -> {
            if (field.name().startsWith("pad")) {
                currentPaddingSize.addAndGet(field.size());
            } else {
                if (currentPaddingSize.get() > 0) {
                    interestingFields.add("[padding]");
                    paddingSizes.add(currentPaddingSize.getAndSet(0));
                }
                interestingFields.add(field.name());
            }
        });
        if (currentPaddingSize.get() > 0) {
            interestingFields.add("[padding]");
            paddingSizes.add(currentPaddingSize.getAndSet(0));
        }

        assertEquals(List.of("[padding]", "value", "[padding]"), interestingFields);
        assertTrue(paddingSizes.stream().allMatch(ps -> ps >= MIN_PADDING));
    }
}