package io.github.lantalex;


import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.II_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;

@JCStressTest
@Outcome(id = "1, 0", expect = ACCEPTABLE_INTERESTING, desc = "Bad CPU(?)")
@Outcome(expect = ACCEPTABLE, desc = "Expected boring case")
@State
public class OpaqueSemantic_Coherence {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "OpaqueSemantic_Coherence"

        Results(x86):
        -----------------------------------------------------------------
          RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
            0, 0  521,346,140   36.26%   Acceptable  Expected boring case
            0, 1    1,613,997    0.11%   Acceptable  Expected boring case
            1, 0            0    0.00%  Interesting  Bad CPU(?)
            1, 1  914,977,527   63.63%   Acceptable  Expected boring case


        Results(arm_v8):
        ----------------------------------------------------------------
          RESULT     SAMPLES     FREQ       EXPECT  DESCRIPTION
            0, 0     858,498    2.24%   Acceptable  Expected boring case
            0, 1      62,616    0.16%   Acceptable  Expected boring case
            1, 0         136   <0.01%  Interesting  Bad CPU(?)
            1, 1  37,382,494   97.59%   Acceptable  Expected boring case

     */

    static final VarHandle A;
    static final VarHandle B;

    static {
        try {
            A = MethodHandles.lookup().findVarHandle(OpaqueSemantic_Coherence.class, "a", int.class);
            B = MethodHandles.lookup().findVarHandle(OpaqueSemantic_Coherence.class, "b", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int a;
    int b;

    @Actor
    public void actor1() {
        A.setOpaque(this, 1);
        B.setOpaque(this, 1);
    }

    @Actor
    public void actor2(II_Result r) {
        r.r1 = (int) B.getOpaque(this);
        r.r2 = (int) A.getOpaque(this);
    }
}