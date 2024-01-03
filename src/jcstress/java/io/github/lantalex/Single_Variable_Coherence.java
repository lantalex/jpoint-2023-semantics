package io.github.lantalex;


import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.IIII_Result;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Outcome(expect = ACCEPTABLE, desc = "Expected boring case")
@Outcome(id = "1, 2, 2, 1", expect = FORBIDDEN, desc = "Violation of coherence")
@Outcome(id = "2, 1, 1, 2", expect = FORBIDDEN, desc = "Violation of coherence")
@State
public class Single_Variable_Coherence {

    /*
        How to run:
        $ ./gradlew jcstress --tests  "Single_Variable_Coherence"

        Results(x86):
        -----------------------------------------------------------------
              RESULT      SAMPLES     FREQ      EXPECT  DESCRIPTION
          0, 0, 0, 0  194,853,033   17.25%  Acceptable  Expected boring case
          0, 0, 0, 1       90,864   <0.01%  Acceptable  Expected boring case
          0, 0, 0, 2       88,606   <0.01%  Acceptable  Expected boring case
          0, 0, 1, 1   40,603,702    3.59%  Acceptable  Expected boring case
          0, 0, 1, 2       91,984   <0.01%  Acceptable  Expected boring case
          0, 0, 2, 1       81,609   <0.01%  Acceptable  Expected boring case
          0, 0, 2, 2   36,928,973    3.27%  Acceptable  Expected boring case
          0, 1, 0, 0      133,799    0.01%  Acceptable  Expected boring case
          0, 1, 0, 1       12,542   <0.01%  Acceptable  Expected boring case
          0, 1, 0, 2        3,210   <0.01%  Acceptable  Expected boring case
          0, 1, 1, 1       44,502   <0.01%  Acceptable  Expected boring case
          0, 1, 1, 2        1,160   <0.01%  Acceptable  Expected boring case
          0, 1, 2, 1        1,441   <0.01%  Acceptable  Expected boring case
          0, 1, 2, 2       69,953   <0.01%  Acceptable  Expected boring case
          0, 2, 0, 0      130,729    0.01%  Acceptable  Expected boring case
          0, 2, 0, 1        3,284   <0.01%  Acceptable  Expected boring case
          0, 2, 0, 2       11,629   <0.01%  Acceptable  Expected boring case
          0, 2, 1, 1       64,460   <0.01%  Acceptable  Expected boring case
          0, 2, 1, 2        1,277   <0.01%  Acceptable  Expected boring case
          0, 2, 2, 1        1,199   <0.01%  Acceptable  Expected boring case
          0, 2, 2, 2       45,466   <0.01%  Acceptable  Expected boring case
          1, 1, 0, 0   52,513,544    4.65%  Acceptable  Expected boring case
          1, 1, 0, 1       58,853   <0.01%  Acceptable  Expected boring case
          1, 1, 0, 2      113,564    0.01%  Acceptable  Expected boring case
          1, 1, 1, 1  326,740,733   28.93%  Acceptable  Expected boring case
          1, 1, 1, 2       32,104   <0.01%  Acceptable  Expected boring case
          1, 1, 2, 1      208,235    0.02%  Acceptable  Expected boring case
          1, 1, 2, 2   40,301,393    3.57%  Acceptable  Expected boring case
          1, 2, 0, 0      135,600    0.01%  Acceptable  Expected boring case
          1, 2, 0, 1        1,241   <0.01%  Acceptable  Expected boring case
          1, 2, 0, 2        1,486   <0.01%  Acceptable  Expected boring case
          1, 2, 1, 1       33,119   <0.01%  Acceptable  Expected boring case
          1, 2, 1, 2       11,847   <0.01%  Acceptable  Expected boring case
   ---->  1, 2, 2, 1            0    0.00%   Forbidden  Violation of coherence
          1, 2, 2, 2      140,862    0.01%  Acceptable  Expected boring case
          2, 1, 0, 0      126,504    0.01%  Acceptable  Expected boring case
          2, 1, 0, 1        1,634   <0.01%  Acceptable  Expected boring case
          2, 1, 0, 2        1,389   <0.01%  Acceptable  Expected boring case
          2, 1, 1, 1      127,919    0.01%  Acceptable  Expected boring case
   ---->  2, 1, 1, 2            0    0.00%   Forbidden  Violation of coherence
          2, 1, 2, 1       12,003   <0.01%  Acceptable  Expected boring case
          2, 1, 2, 2       30,716   <0.01%  Acceptable  Expected boring case
          2, 2, 0, 0   48,844,307    4.32%  Acceptable  Expected boring case
          2, 2, 0, 1      117,552    0.01%  Acceptable  Expected boring case
          2, 2, 0, 2       60,980   <0.01%  Acceptable  Expected boring case
          2, 2, 1, 1   40,056,529    3.55%  Acceptable  Expected boring case
          2, 2, 1, 2      219,179    0.02%  Acceptable  Expected boring case
          2, 2, 2, 1       28,286   <0.01%  Acceptable  Expected boring case
          2, 2, 2, 2  346,407,783   30.67%  Acceptable  Expected boring case


        Results(arm_v8):
        ----------------------------------------------------------------
              RESULT     SAMPLES     FREQ      EXPECT  DESCRIPTION
          0, 0, 0, 0     191,653    0.49%  Acceptable  Expected boring case
          0, 0, 0, 1       1,139   <0.01%  Acceptable  Expected boring case
          0, 0, 0, 2       1,015   <0.01%  Acceptable  Expected boring case
          0, 0, 1, 1      64,824    0.16%  Acceptable  Expected boring case
          0, 0, 1, 2          60   <0.01%  Acceptable  Expected boring case
          0, 0, 2, 1         115   <0.01%  Acceptable  Expected boring case
          0, 0, 2, 2      25,634    0.07%  Acceptable  Expected boring case
          0, 1, 0, 0         296   <0.01%  Acceptable  Expected boring case
          0, 1, 0, 1         738   <0.01%  Acceptable  Expected boring case
          0, 1, 0, 2         205   <0.01%  Acceptable  Expected boring case
          0, 1, 1, 1       2,154   <0.01%  Acceptable  Expected boring case
          0, 1, 1, 2         211   <0.01%  Acceptable  Expected boring case
          0, 1, 2, 1         502   <0.01%  Acceptable  Expected boring case
          0, 1, 2, 2         704   <0.01%  Acceptable  Expected boring case
          0, 2, 0, 0         269   <0.01%  Acceptable  Expected boring case
          0, 2, 0, 1         215   <0.01%  Acceptable  Expected boring case
          0, 2, 0, 2         724   <0.01%  Acceptable  Expected boring case
          0, 2, 1, 1         599   <0.01%  Acceptable  Expected boring case
          0, 2, 1, 2         415   <0.01%  Acceptable  Expected boring case
          0, 2, 2, 1         299   <0.01%  Acceptable  Expected boring case
          0, 2, 2, 2       2,172   <0.01%  Acceptable  Expected boring case
          1, 1, 0, 0      43,963    0.11%  Acceptable  Expected boring case
          1, 1, 0, 1       1,035   <0.01%  Acceptable  Expected boring case
          1, 1, 0, 2         298   <0.01%  Acceptable  Expected boring case
          1, 1, 1, 1  25,235,395   64.18%  Acceptable  Expected boring case
          1, 1, 1, 2       1,103   <0.01%  Acceptable  Expected boring case
          1, 1, 2, 1       4,853    0.01%  Acceptable  Expected boring case
          1, 1, 2, 2     183,377    0.47%  Acceptable  Expected boring case
          1, 2, 0, 0          25   <0.01%  Acceptable  Expected boring case
          1, 2, 0, 1         109   <0.01%  Acceptable  Expected boring case
          1, 2, 0, 2         192   <0.01%  Acceptable  Expected boring case
          1, 2, 1, 1         772   <0.01%  Acceptable  Expected boring case
          1, 2, 1, 2       4,290    0.01%  Acceptable  Expected boring case
   ---->  1, 2, 2, 1           0    0.00%   Forbidden  Violation of coherence
          1, 2, 2, 2       4,399    0.01%  Acceptable  Expected boring case
          2, 1, 0, 0          79   <0.01%  Acceptable  Expected boring case
          2, 1, 0, 1         271   <0.01%  Acceptable  Expected boring case
          2, 1, 0, 2         134   <0.01%  Acceptable  Expected boring case
          2, 1, 1, 1       6,588    0.02%  Acceptable  Expected boring case
   ---->  2, 1, 1, 2           0    0.00%   Forbidden  Violation of coherence
          2, 1, 2, 1       5,704    0.01%  Acceptable  Expected boring case
          2, 1, 2, 2         981   <0.01%  Acceptable  Expected boring case
          2, 2, 0, 0      21,129    0.05%  Acceptable  Expected boring case
          2, 2, 0, 1         260   <0.01%  Acceptable  Expected boring case
          2, 2, 0, 2       1,098   <0.01%  Acceptable  Expected boring case
          2, 2, 1, 1     171,119    0.44%  Acceptable  Expected boring case
          2, 2, 1, 2       3,630   <0.01%  Acceptable  Expected boring case
          2, 2, 2, 1       1,433   <0.01%  Acceptable  Expected boring case
          2, 2, 2, 2  13,331,324   33.91%  Acceptable  Expected boring case

     */

    static final VarHandle X;

    static {
        try {
            X = MethodHandles.lookup().findVarHandle(Single_Variable_Coherence.class, "x", int.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    int x;

    @Actor
    public void actorW1() {
        X.setOpaque(this, 1);
    }

    @Actor
    public void actorR2(IIII_Result r) {
        r.r1 = (int) X.getOpaque(this);
        r.r2 = (int) X.getOpaque(this);
    }

    @Actor
    public void actorR3(IIII_Result r) {
        r.r3 = (int) X.getOpaque(this);
        r.r4 = (int) X.getOpaque(this);
    }

    @Actor
    public void actorW4() {
        X.setOpaque(this, 2);
    }
}