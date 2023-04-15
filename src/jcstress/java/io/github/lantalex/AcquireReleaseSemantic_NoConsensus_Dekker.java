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
public class AcquireReleaseSemantic_NoConsensus_Dekker {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "AcquireReleaseSemantic_NoConsensus_Dekker"

        Results(x86):
        --------------------------------------------------------------------------------------------
         RESULT     SAMPLES     FREQ       EXPECT  DESCRIPTION
           0, 0         684   <0.01%   Acceptable  No winner, let's race again
           0, 1  21,051,823   27.47%   Acceptable  Blue cat is winner
           1, 0  22,109,294   28.85%   Acceptable  Red panda is winner
           1, 1  33,486,647   43.69%  Interesting  Conflict

    */

    static final VarHandle RED;
    static final VarHandle BLUE;

    static {
        try {
            RED = MethodHandles.lookup().findVarHandle(AcquireReleaseSemantic_NoConsensus_Dekker.class, "redPanda", int.class);
            BLUE = MethodHandles.lookup().findVarHandle(AcquireReleaseSemantic_NoConsensus_Dekker.class, "blueCat", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int redPanda;
    int blueCat;

    @Actor
    public void panda(II_Result r) {
        RED.setRelease(this, 1);

        //logic of Referee#1 is here
        if ((int) RED.getAcquire(this) == 1 && (int) BLUE.getAcquire(this) == 0) {
            //red panda is the winner
            r.r1 = 1;
        }
    }

    @Actor
    public void cat(II_Result r) {
        BLUE.setRelease(this, 1);

        //logic of Referee#2 is here
        if ((int) BLUE.getAcquire(this) == 1 && (int) RED.getAcquire(this) == 0) {
            //blue cat is the winner
            r.r2 = 1;
        }
    }
}
