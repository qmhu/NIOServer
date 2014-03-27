package com.rex.process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * Created by QQ on 14-1-16.
 */
public class RunShell {
    public static void main(String[] args){
        try {
            String shpath="timeout /t 500";
            Process ps = Runtime.getRuntime().exec(shpath);
            System.out.println(getPid(ps));
            //Thread.sleep(1000);
            //ps.destroy();
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getPid(Process pro)
    {
        try {
            Field f=pro.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            return f.getInt(pro);
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;

    }
}