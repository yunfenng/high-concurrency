package cn.jaa.visiable;

/**
 * @Author: Jaa
 * @Description: 原子性
 * @Date 2024/4/14
 */
class CounterSample {
    int sum = 0;

    public void increase() {
        sum++;
    }
    /*
        dos命令：javap -c CounterSample.class

        Compiled from "CounterSample.java"
        class cn.jaa.visiable.CounterSample {
          int sum;

          cn.jaa.visiable.CounterSample();
            Code:
               0: aload_0
               1: invokespecial #1                  // Method java/lang/Object."<init>":()V
               4: aload_0
               5: iconst_0
               6: putfield      #2                  // Field sum:I
               9: return

          public void increase();
            Code:
               0: aload_0
               1: dup
               2: getfield      #2                  // Field sum:I
               5: iconst_1
               6: iadd
               7: putfield      #2                  // Field sum:I
              10: return
        }
     */
}
