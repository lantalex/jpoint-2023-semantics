package io.github.lantalex;


import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Mode;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.Signal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@SuppressWarnings("StatementWithEmptyBody")
@JCStressTest(Mode.Termination)
@Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE, desc = "Expected behavior")
@Outcome(id = "STALE", expect = Expect.FORBIDDEN, desc = "Hung up forever")
public class OpaqueSemantic_Progress {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "OpaqueSemantic_Progress"

        Results(x86):
        -------------------------------------------------------------
              RESULT  SAMPLES     FREQ      EXPECT   DESCRIPTION
               STALE        0    0.00%   Forbidden   Hung up forever
          TERMINATED   36,881  100.00%  Acceptable  Expected behavior

    */

    static final VarHandle DONE;

    static {
        try {
            DONE = MethodHandles.lookup().findVarHandle(OpaqueSemantic_Progress.class, "done", boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    boolean done;

    @Actor
    public void actor1() {
        while (!((boolean) DONE.getOpaque(this))) {
            //doSomeWork();
        }
    }

    @Signal
    public void signal() {
        done = true;
    }
}