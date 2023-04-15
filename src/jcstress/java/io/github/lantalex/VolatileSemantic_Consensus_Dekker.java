package io.github.lantalex;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.II_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "No winner, let's race again")
@Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "Red panda is winner")
@Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "Blue cat is winner")
@Outcome(id = "1, 1", expect = FORBIDDEN, desc = "Conflict")
@State
public class VolatileSemantic_Consensus_Dekker {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "VolatileSemantic_Consensus_Dekker"

        Results(x86):
        --------------------------------------------------------------------------------------------
         RESULT      SAMPLES     FREQ      EXPECT  DESCRIPTION
           0, 0   85,724,733   10.52%  Acceptable  No winner, let's race again
           0, 1  374,476,879   45.97%  Acceptable  Blue cat is winner
           1, 0  354,392,436   43.51%  Acceptable  Red panda is winner
           1, 1            0    0.00%   Forbidden  Conflict

    */

    static final VarHandle RED;
    static final VarHandle BLUE;

    static {
        try {
            RED = MethodHandles.lookup().findVarHandle(VolatileSemantic_Consensus_Dekker.class, "redPanda", int.class);
            BLUE = MethodHandles.lookup().findVarHandle(VolatileSemantic_Consensus_Dekker.class, "blueCat", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int redPanda;
    int blueCat;

    @Actor
    public void panda(II_Result r) {
        RED.setVolatile(this, 1);

        //logic of Referee#1 here
        if ((int) RED.getVolatile(this) == 1 && (int) BLUE.getVolatile(this) == 0) {
            //red panda is the winner
            r.r1 = 1;
        }
    }

    @Actor
    public void cat(II_Result r) {
        BLUE.setVolatile(this, 1);

        //logic of Referee#2 here
        if ((int) BLUE.getVolatile(this) == 1 && (int) RED.getVolatile(this) == 0) {
            //blue cat is the winner
            r.r2 = 1;
        }
    }
}
