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
@Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "no winner")
@Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "x not y")
@Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "y not x")
@Outcome(id = "1, 1", expect = ACCEPTABLE_INTERESTING, desc = "conflict")
@State
public class Barriers_NoConsensus_Dekker {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "Barriers_NoConsensus_Dekker"

        Results(x86):
        --------------------------------------------------------------------------------------------
          RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
            0, 0    2,933,532    0.23%   Acceptable  no winner
            0, 1  389,564,098   29.91%   Acceptable  y not x
            1, 0  451,513,601   34.66%   Acceptable  x not y
            1, 1  458,615,073   35.21%  Interesting  conflict

    */

    static final VarHandle X;
    static final VarHandle Y;

    static {
        try {
            X = MethodHandles.lookup().findVarHandle(Barriers_NoConsensus_Dekker.class, "x", int.class);
            Y = MethodHandles.lookup().findVarHandle(Barriers_NoConsensus_Dekker.class, "y", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int x;
    int y;

    @Actor
    public void actor1(II_Result r) {
        X.setOpaque(this, 1);

        VarHandle.releaseFence();

        if ((int) Y.getOpaque(this) == 0) {
            //"x not y"
            r.r1 = 1;
        }
    }

    @Actor
    public void actor2(II_Result r) {
        Y.setOpaque(this, 1);

        VarHandle.acquireFence();

        if ((int) X.getOpaque(this) == 0) {
            //"y not x"
            r.r2 = 1;
        }
    }
}
