package io.github.lantalex;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static io.github.lantalex.VarHandleApi.MyClass.X_HANDLE;

public class VarHandleApi {

    public static void main(String[] args) {
        MyClass m = new MyClass();
        int tmp;


        //---Plain semantic---
        //Write_plain(m.x) <- 42
        X_HANDLE.set(m, 42);

        //Read_plain(m.x) -> 42
        //Write_plain(tmp) <- 42
        tmp = (int) X_HANDLE.get(m);

        //Read_plain(tmp) <- 42
        System.out.println("Plain semantic: " + tmp);


        //---Opaque semantic---
        //Write_opaque(m.x) <- 33
        X_HANDLE.setOpaque(m, 33);

        //Read_opaque(m.x) -> 33
        //Write_plain(tmp) <- 33
        tmp = (int) X_HANDLE.getOpaque(m);

        //Read_plain(tmp) <- 33
        System.out.println("Opaque semantic: " + tmp);


        //---Acquire-release semantic---
        //Write_release(m.x) <- 22
        X_HANDLE.setRelease(m, 22);

        //Read_acquire(m.x) -> 22
        //Write_plain(tmp) <- 22
        tmp = (int) X_HANDLE.getAcquire(m);

        //Read_plain(tmp) <- 22
        System.out.println("Acquire-release semantic: " + tmp);


        //---Volatile semantic---
        //Write_volatile(m.x) <- 11
        X_HANDLE.setVolatile(m, 11);

        //Read_volatile(m.x) -> 11
        //Write_plain(tmp) <- 11
        tmp = (int) X_HANDLE.getVolatile(m);

        //Read_plain(tmp) <- 11
        System.out.println("Volatile semantic: " + tmp);
    }

    static class MyClass {
        public static final VarHandle X_HANDLE;

        static {
            try {
                X_HANDLE = MethodHandles.lookup().findVarHandle(MyClass.class, "x", int.class);
            } catch (ReflectiveOperationException e) {
                throw new Error(e);
            }
        }

        int x;
    }
}
