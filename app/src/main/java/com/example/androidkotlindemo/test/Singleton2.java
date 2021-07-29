package com.example.androidkotlindemo.test;

/**
 * Created by zxf on 2021/4/14
 */
class Singleton2 {

    private final static Object lock = new Object();
    private static Singleton2 singleton2 = null;

    public static Singleton2 getInstance() {
        if (singleton2 == null) {
            synchronized (lock) {
                if (singleton2 == null)
                    singleton2 = new Singleton2();
            }
        }
        return singleton2;
    }


    public static void main(String[] args) {
//        for (int i = 0; i < 1000; i++) {
//            test();
//            System.out.print(".");
//            try {
//                Thread.sleep(100);
//                singleton2 = null;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        for (int i = 0; i < 5000; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    Thread.sleep(10);
                    test2(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println(count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private static int count = 0;
    private final static Object o = new Object();

    private static void test2(Integer lock) {
        if (lock == null) return;
        synchronized (o) {
            count++;
        }
    }

    public static void test() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                Singleton2 s = getInstance();
                if (s == null) {
                    System.out.println("----------------");
                } else {
                    System.out.print("|");
                }
            }).start();
        }
    }
}
