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
@Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "No winner, let's race again")
@Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "Red panda is winner")
@Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "Blue cat is winner")
@Outcome(id = "1, 1", expect = ACCEPTABLE_INTERESTING, desc = "Conflict")
@State
public class AcquireReleaseSemantic_NoConsensus {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "AcquireReleaseSemantic_NoConsensus"

        Results(x86):
        --------------------------------------------------------------------------------------------
         RESULT        SAMPLES     FREQ       EXPECT  DESCRIPTION
           0, 0  4,251,534,698   69.53%   Acceptable  No winner, let's race again
           0, 1    925,984,848   15.14%   Acceptable  Blue cat is winner
           1, 0    937,132,614   15.33%   Acceptable  Red panda is winner
           1, 1              0    0.00%  Interesting  Conflict


        Results(arm_v8):
        --------------------------------------------------------------------------------------------
         RESULT        SAMPLES     FREQ       EXPECT  DESCRIPTION
           0, 0     72,498,721   92.02%   Acceptable  No winner, let's race again
           0, 1      3,089,344    3.92%   Acceptable  Blue cat is winner
           1, 0      3,194,399    4.05%   Acceptable  Red panda is winner
           1, 1              0    0.00%  Interesting  Conflict

    */

    static final VarHandle RED;
    static final VarHandle BLUE;

    static {
        try {
            RED = MethodHandles.lookup().findVarHandle(AcquireReleaseSemantic_NoConsensus.class, "redPanda", int.class);
            BLUE = MethodHandles.lookup().findVarHandle(AcquireReleaseSemantic_NoConsensus.class, "blueCat", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int redPanda;
    int blueCat;

    @Actor
    public void panda() {
        RED.setRelease(this, 1);
    }

    @Actor
    public void cat() {
        BLUE.setRelease(this, 1);
    }

    @Actor
    public void referee1(II_Result r) {
        if ((int) RED.getAcquire(this) == 1 && (int) BLUE.getAcquire(this) == 0) {
            //red panda is the winner
            r.r1 = 1;
        }
    }

    @Actor
    public void referee2(II_Result r) {
        if ((int) BLUE.getAcquire(this) == 1 && (int) RED.getAcquire(this) == 0) {
            //blue cat is the winner
            r.r2 = 1;
        }
    }
}
