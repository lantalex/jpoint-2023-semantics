package io.github.lantalex;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.IIII_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Outcome(id = "0, .*", expect = ACCEPTABLE, desc = "Absence of happens-before relation")
@Outcome(id = "1, 20, 13, 42", expect = ACCEPTABLE_INTERESTING, desc = "Happens-before due to volatile write/read")
@Outcome(expect = FORBIDDEN, desc = "Violation of happens-before")
@State
public class VolatileSemantic_HappensBefore {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "VolatileSemantic_HappensBefore"

        Results(x86):
        --------------------------------------------------------------------------------------------
                RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
         0,  0,  0,  0  296,284,079   41.11%   Acceptable  Absence of happens-before relation
         0,  0,  0, 42        6,924   <0.01%   Acceptable  Absence of happens-before relation
         0,  0, 13,  0       33,382   <0.01%   Acceptable  Absence of happens-before relation
         0,  0, 13, 42        5,047   <0.01%   Acceptable  Absence of happens-before relation
         0, 20,  0,  0      247,625    0.03%   Acceptable  Absence of happens-before relation
         0, 20,  0, 42       21,335   <0.01%   Acceptable  Absence of happens-before relation
         0, 20, 13,  0      215,919    0.03%   Acceptable  Absence of happens-before relation
         0, 20, 13, 42      219,500    0.03%   Acceptable  Absence of happens-before relation
         1, 20, 13, 42  423,745,453   58.79%  Interesting  Happens-before due to volatile write/read


        Results(arm_v8):
        --------------------------------------------------------------------------------------------
         RESULT     SAMPLES     FREQ       EXPECT  DESCRIPTION
         0,  0,  0,  0     774,879    3.29%   Acceptable  Absence of happens-before relation
         0,  0,  0, 42          37   <0.01%   Acceptable  Absence of happens-before relation
         0,  0, 13,  0         285   <0.01%   Acceptable  Absence of happens-before relation
         0,  0, 13, 42         682   <0.01%   Acceptable  Absence of happens-before relation
         0, 20,  0,  0      34,969    0.15%   Acceptable  Absence of happens-before relation
         0, 20,  0, 42         115   <0.01%   Acceptable  Absence of happens-before relation
         0, 20, 13,  0      18,662    0.08%   Acceptable  Absence of happens-before relation
         0, 20, 13, 42     153,163    0.65%   Acceptable  Absence of happens-before relation
         1, 20, 13, 42  22,544,632   95.82%  Interesting  Happens-before due to volatile write/read

    */

    int x, y, z;
    volatile int ready;

    @Actor
    public void actor1() {
        x = 20;
        y = 13;
        z = 42;
        ready = 1;
    }

    @Actor
    public void actor2(IIII_Result r) {
        r.r1 = ready;
        r.r2 = x;
        r.r3 = y;
        r.r4 = z;
    }
}
