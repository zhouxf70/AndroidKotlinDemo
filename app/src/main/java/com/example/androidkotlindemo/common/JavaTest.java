package com.example.androidkotlindemo.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Created by zxf on 2021/3/22
 */
class JavaTest {

//    final int age;
//
//    public JavaTest() {
//        this.age = 10;
//    }
//
//    public JavaTest(JavaTest test) {
//        age = test.age;
//        try {
//            Object clone = test.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//    }

//    private void write

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) {
        JavaTest test = new JavaTest();
        new Thread(() -> {
            test.test("111");
        }).start();
        new Thread(() -> {
            test.test2("222");
        }).start();
    }

    final Object o = new Object();

    public void test(String name) {
        synchronized (this) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(i);
            }
            System.out.println(name);
        }
    }

    public void test2(String name) {
        synchronized (o) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(i);
            }
            System.out.println(name);
        }
    }


}
