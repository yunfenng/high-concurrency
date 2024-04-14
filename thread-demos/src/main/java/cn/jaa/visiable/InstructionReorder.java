package cn.jaa.visiable;

/**
 * @Author: Jaa
 * @Description: 有序性问题
 * @Date 2024/4/14
 *
 * 指令重排序（Reordering）:
 * CPU为了提高程序运行效率，可能会对输入代码进行优化，它不保证程序中各个语句的执行顺序同代码中的先后顺序一致，
 * 但是它会保证程序最终的执行结果和代码顺序执行的结果是一致的。
 */
public class InstructionReorder {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for (; ; ) {
            i++;
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            Thread one = new Thread(new Runnable() {
                public void run() {
                    a = 1;    // ①
                    x = b;    // ②
                }
            });

            Thread other = new Thread(new Runnable() {
                public void run() {
                    b = 1;  // ③
                    y = a;  // ④
                }
            });
            one.start();
            other.start();
            one.join();
            other.join();
            String result = "第" + i + "次 (" + x + "," + y + "）";
            if (x == 0 && y == 0) {
                System.err.println(result);
            }
        }
    }
}
