package io.github.lantalex;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.IIII_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Outcome(id = "0, .*", expect = ACCEPTABLE, desc = "Absence of causality")
@Outcome(id = "1, 20, 13, 42", expect = ACCEPTABLE_INTERESTING, desc = "Causality due to release write/acquire read")
@Outcome(expect = FORBIDDEN, desc = "Violation of causality")
@State
public class AcquireReleaseSemantic_Causality {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "AcquireReleaseSemantic_Causality"

        Results(x86):
        ----------------------------------------------------------------------------------------------
                RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
         0,  0,  0,  0  152,022,439   16.92%   Acceptable  Absence of causality
         0,  0,  0, 42        2,231   <0.01%   Acceptable  Absence of causality
         0,  0, 13,  0      132,837    0.01%   Acceptable  Absence of causality
         0,  0, 13, 42       10,224   <0.01%   Acceptable  Absence of causality
         0, 20,  0,  0      490,592    0.05%   Acceptable  Absence of causality
         0, 20,  0, 42       73,028   <0.01%   Acceptable  Absence of causality
         0, 20, 13,  0      489,940    0.05%   Acceptable  Absence of causality
         0, 20, 13, 42      537,862    0.06%   Acceptable  Absence of causality
         1, 20, 13, 42  744,479,311   82.88%  Interesting  Causality due to release write/acquire read

    */

    static final VarHandle READY;

    static {
        try {
            READY = MethodHandles.lookup().findVarHandle(AcquireReleaseSemantic_Causality.class, "ready", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int x, y, z;
    int ready;

    @Actor
    public void actor1() {
        x = 20;
        y = 13;
        z = 42;
        READY.setRelease(this, 1);
    }

    @Actor
    public void actor2(IIII_Result r) {
        r.r1 = (int) READY.getAcquire(this);
        r.r2 = x;
        r.r3 = y;
        r.r4 = z;
    }
}
