package io.github.lantalex;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@SuppressWarnings("unused")
abstract class PaddedAtomicIntegerPad {
    byte pad000, pad001, pad002, pad003, pad004, pad005, pad006, pad007;// 8b
    byte pad010, pad011, pad012, pad013, pad014, pad015, pad016, pad017;// 16b
    byte pad020, pad021, pad022, pad023, pad024, pad025, pad026, pad027;// 24b
    byte pad030, pad031, pad032, pad033, pad034, pad035, pad036, pad037;// 32b
    byte pad040, pad041, pad042, pad043, pad044, pad045, pad046, pad047;// 40b
    byte pad050, pad051, pad052, pad053, pad054, pad055, pad056, pad057;// 48b
    byte pad060, pad061, pad062, pad063, pad064, pad065, pad066, pad067;// 56b
    byte pad070, pad071, pad072, pad073, pad074, pad075, pad076, pad077;// 64b
    byte pad100, pad101, pad102, pad103, pad104, pad105, pad106, pad107;// 72b
    byte pad110, pad111, pad112, pad113, pad114, pad115, pad116, pad117;// 80b
    byte pad120, pad121, pad122, pad123, pad124, pad125, pad126, pad127;// 88b
    byte pad130, pad131, pad132, pad133, pad134, pad135, pad136, pad137;// 96b
    byte pad140, pad141, pad142, pad143, pad144, pad145, pad146, pad147;//104b
    byte pad150, pad151, pad152, pad153, pad154, pad155, pad156, pad157;//112b
    byte pad160, pad161, pad162, pad163, pad164, pad165, pad166, pad167;//120b
    byte pad170, pad171, pad172, pad173, pad174, pad175, pad176, pad177;//128b
}

class PaddedAtomicIntegerField extends PaddedAtomicIntegerPad {

    int value;

    PaddedAtomicIntegerField(int value) {
        this.value = value;
    }
}

//Just like regular AtomicInteger with additional padding to prevent false sharing
@SuppressWarnings("unused")
public final class PaddedAtomicInteger extends PaddedAtomicIntegerField {

    private static final VarHandle VALUE;

    static {
        try {
            VALUE = MethodHandles.lookup().findVarHandle(PaddedAtomicIntegerField.class, "value", int.class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }

    byte pad000, pad001, pad002, pad003, pad004, pad005, pad006, pad007;// 8b
    byte pad010, pad011, pad012, pad013, pad014, pad015, pad016, pad017;// 16b
    byte pad020, pad021, pad022, pad023, pad024, pad025, pad026, pad027;// 24b
    byte pad030, pad031, pad032, pad033, pad034, pad035, pad036, pad037;// 32b
    byte pad040, pad041, pad042, pad043, pad044, pad045, pad046, pad047;// 40b
    byte pad050, pad051, pad052, pad053, pad054, pad055, pad056, pad057;// 48b
    byte pad060, pad061, pad062, pad063, pad064, pad065, pad066, pad067;// 56b
    byte pad070, pad071, pad072, pad073, pad074, pad075, pad076, pad077;// 64b
    byte pad100, pad101, pad102, pad103, pad104, pad105, pad106, pad107;// 72b
    byte pad110, pad111, pad112, pad113, pad114, pad115, pad116, pad117;// 80b
    byte pad120, pad121, pad122, pad123, pad124, pad125, pad126, pad127;// 88b
    byte pad130, pad131, pad132, pad133, pad134, pad135, pad136, pad137;// 96b
    byte pad140, pad141, pad142, pad143, pad144, pad145, pad146, pad147;//104b
    byte pad150, pad151, pad152, pad153, pad154, pad155, pad156, pad157;//112b
    byte pad160, pad161, pad162, pad163, pad164, pad165, pad166, pad167;//120b
    byte pad170, pad171, pad172, pad173, pad174, pad175, pad176, pad177;//128b

    public PaddedAtomicInteger() {
        super(0);
    }

    public PaddedAtomicInteger(int value) {
        super(value);
    }

    public int getPlain() {
        return (int) VALUE.get(this);
    }

    public void setPlain(int value) {
        VALUE.set(this, value);
    }

    public int getOpaque() {
        return (int) VALUE.getOpaque(this);
    }

    public void setOpaque(int value) {
        VALUE.setOpaque(this, value);
    }

    public int getAcquire() {
        return (int) VALUE.getAcquire(this);
    }

    public void setRelease(int value) {
        VALUE.setRelease(this, value);
    }

    public int getVolatile() {
        return (int) VALUE.getVolatile(this);
    }

    public void setVolatile(int value) {
        VALUE.setVolatile(this, value);
    }
}
