package cn.jaa.util;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/5
 */
public class ReflectionUtil {

    /**
     * 获得调用方法的类名+方法名
     *
     * @return 方法名称
     */
    public static String getNakeCallClassMethod() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 获取调用方法名
        String[] className = stack[3].getClassName().split("\\.");
        String fullName = className[className.length - 1] + "." + stack[3].getMethodName();
        return fullName;
    }

}
