package com.just1t.dm;

import com.just1t.dm.util.WebUtil;

import java.util.Scanner;

/**
 * @author just1t
 * @date 2023/3/3 18:30
 * @introduce 通过名称进行下在音乐
 */
public class Application {
    public static void main(String[] args) {
        System.out.println("请输入歌名：");
        Scanner scanner=new Scanner(System.in);
        String s = scanner.nextLine();
        String name = WebUtil.getMUByName(s);
        System.out.println("下载位置为："+name);
        WebUtil.shutdown();
    }
}
