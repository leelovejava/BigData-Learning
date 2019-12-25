package com.leelovejava.demo.excep;

/**
 * java异常处理
 *
 * Java异常处理的注意点.
 * java语言按照try—catch-catch...—finally的方式来处理异常
 * 不管有没有异常捕获，都会执行finally, 因此通常可以在finally代码块中释放资源
 * 可以有多个catch，分别捕获对应的异常，这时需要把范围小的异常类写在前面，把范围大的异常类写在后面，否则编译错误。会提示 "Exception 'java.lang.xxxxxx' has already been caught"
 *
 * @author leelovejava
 * @date 2019/6/10
 */
public class JavaException {
    public static void main(String[] args) {
        try {
            // 可疑代码
            int i = 0;
            int b = 10;
            // 执行代码时，会抛出ArithmeticException异常
            int c = b / i;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 最终要执行的代码
            System.out.println("java finally");
        }
    }
}
