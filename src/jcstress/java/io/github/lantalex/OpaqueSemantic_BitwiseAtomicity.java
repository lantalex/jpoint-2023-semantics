package io.github.lantalex;


import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.L_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Outcome(id = "0", expect = ACCEPTABLE, desc = "Default value")
@Outcome(id = "4294967295", expect = ACCEPTABLE, desc = "Value from actor 1")
@Outcome(id = "-4294967296", expect = ACCEPTABLE, desc = "Value from actor 2")
@Outcome(expect = FORBIDDEN, desc = "Bitwise atomicity")
@State
public class OpaqueSemantic_BitwiseAtomicity {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "OpaqueSemantic_BitwiseAtomicity"

        Results(x86):
        ------------------------------------------------------------
               RESULT      SAMPLES     FREQ      EXPECT  DESCRIPTION
          -4294967296  758,224,146   40.14%  Acceptable  Value from actor 2
                    0  360,476,799   19.08%  Acceptable  Default value
           4294967295  770,411,119   40.78%  Acceptable  Value from actor 1

     */

    static final VarHandle A;

    static {
        try {
            A = MethodHandles.lookup().findVarHandle(OpaqueSemantic_BitwiseAtomicity.class, "a", long.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    long a;

    @Actor
    public void actor1() {
        // 4294967295 == 0x0000_0000_ffff_ffff
        A.setOpaque(this, 0x0000_0000_ffff_ffffL);
    }

    @Actor
    public void actor2() {
        // -4294967296 == 0xffff_ffff_0000_0000
        A.setOpaque(this, 0xffff_ffff_0000_0000L);
    }

    @Actor
    public void actor3(L_Result r) {
        r.r1 = (long) A.getOpaque(this);
    }
}