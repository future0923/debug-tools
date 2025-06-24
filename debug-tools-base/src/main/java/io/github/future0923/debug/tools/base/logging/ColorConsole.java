/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.base.logging;

/**
 * @author future0923
 */
public class ColorConsole {

    public static void black() { System.out.print("\33[30;48;1m"); }
    public static void red() { System.out.print("\33[31;49;1m"); }
    public static void green() { System.out.print("\33[32;50;1m"); }
    public static void yellow() { System.out.print("\33[33;50;1m"); }
    public static void blue() { System.out.print("\33[34;50;1m"); }
    public static void purple() { System.out.print("\33[35;50;1m"); }
    public static void blueGreen() { System.out.print("\33[36;50;1m"); }
    public static void gray() { System.out.print("\33[37;50;1m"); }
    public static void recover() { System.out.print("\33[0m"); }
    public static void black(String s) { System.out.print("\33[30;48;1m" + s + "\33[0m"); }
    public static void red(String s) { System.out.print("\33[31;48;1m" + s + "\33[0m");}
    public static void green(String s) { System.out.print("\33[32;48;1m" + s + "\33[0m"); }
    public static void yellow(String s) { System.out.print("\33[33;48;1m" + s + "\33[0m"); }
    public static void blue(String s) { System.out.print("\33[34;48;1m" + s + "\33[0m"); }
    public static void purple(String s) { System.out.print("\33[35;48;1m" + s + "\33[0m");}
    public static void blueGreen(String s) { System.out.print("\33[36;48;1m" + s + "\33[0m"); }
    public static void gray(String s) { System.out.print("\33[37;48;1m" + s + "\33[0m"); }
    public static void recover(String s) { System.out.print("\33[38;48;1m" + s + "\33[0m"); }
    public static String getBlack(String s) { return ("\33[30;48;1m" + s + "\33[0m"); }
    public static String getRed(String s) { return ("\33[31;48;1m" + s + "\33[0m"); }
    public static String getGreen(String s) { return ("\33[32;48;1m" + s + "\33[0m"); }
    public static String getYellow(String s) { return ("\33[33;48;1m" + s + "\33[0m"); }
    public static String getBlue(String s) { return ("\33[34;48;1m" + s + "\33[0m"); }
    public static String getPurple(String s) { return ("\33[35;48;1m" + s + "\33[0m");}
    public static String getBlueGreen(String s) { return ("\33[36;48;1m" + s + "\33[0m"); }
    public static String getGray(String s) { return ("\33[37;48;1m" + s + "\33[0m"); }
    public static String getRecover(String s) { return ("\33[38;48;1m" + s + "\33[0m"); }

    public static void main(String[] args) {
        //\33[%d;%d;%dm可以更改输出样式，改变的内容包括字体颜色、背景颜色、样式（粗体、斜体、下划线）
        //\33[%d;%d;%dm的第一个数字是前景色（字体颜色），范围30-38；第二个数字是背景色，范围40-47；第三个数字是样式，取值1，3，4
        //使用\33[%d;%d;%dm更改样式后，以后的输出会按照更改的样式进行输出。再输出一个"\33[0m"恢复默认的样式
        System.err.print("err");//打印红色
        System.out.println("right");
        //输出绿底行
        System.out.print("\33[40;1m"+"文字1"+"\33[0m");
        System.out.print("\33[41;1m"+"文字2"+"\33[0m");
        System.out.print("\33[42;1m"+"文字3"+"\33[0m");
        System.out.print("\33[43;1m"+"文字4"+"\33[0m");
        System.out.print("\33[44;1m"+"文字5"+"\33[0m");
        System.out.print("\33[45;1m"+"文字6"+"\33[0m");
        System.out.print("\33[46;1m"+"文字7"+"\n\33[0m");
        System.out.print("\33[47;1m"+"文字8"+"\n\33[0m");//斜杠n可以维持状态到本行结尾
        System.out.println("\33[47;1m"+"文字test1");
        System.out.println("文字test2");
        // 背景色      +"\n\33[0m"
        System.out.print("\33[30;48;1m"+"文字11"+"\n\33[0m");
        System.out.print(""+"\33[31;49;1m"+"文字12"+"\n\33[0m"+"");
        System.out.print("\33[32;50;1m"+"文字13"+"\n\33[0m");
        System.out.print(""+"\33[33;50;1m"+"文字14"+"\n\33[0m");
        System.out.print(""+"\33[34;50;1m"+"文字15"+"\n\33[0m"+"");
        System.out.print("\33[35;50;1m"+"文字16"+"\n\33[0m");
        System.out.print(""+"\33[36;50;1m"+"文字17"+"\33[0m"+"");
        System.out.println("\33[37;50;1m"+"文字18"+"\n\33[0m");
        System.out.println("\33[37;50;2m"+"文字19"+"\n\33[0m");
        System.out.println("\33[37;50;3m"+"文字20"+"\33[0m");
        System.out.println("\33[38;50;4m"+"文字21"+"\33[0m");
        //字体颜色
        System.out.println("\33[31;42;4m"+"test文字21"+"\33[0m");
    }
}
