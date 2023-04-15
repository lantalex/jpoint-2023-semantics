package io.github.lantalex;


import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Mode;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.Signal;

@SuppressWarnings({"WhileLoopSpinsOnField", "StatementWithEmptyBody"})
@JCStressTest(Mode.Termination)
@Outcome(id = "TERMINATED", expect = Expect.ACCEPTABLE, desc = "Expected behavior")
@Outcome(id = "STALE", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Hung up forever")
public class PlainSemantic {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "PlainSemantic"

        Results(x86):
        --------------------------------------------------------------
              RESULT  SAMPLES     FREQ       EXPECT  DESCRIPTION
               STALE        7    0.15%  Interesting  Hung up forever
          TERMINATED    4,637   99.85%   Acceptable  Expected behavior

    */

    boolean done;

    @Actor
    public void actor1() {
        while (!done) {
            //doSomeWork();
        }
    }

    @Signal
    public void signal() {
        done = true;
    }
}