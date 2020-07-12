package com.sun.monitorclient.util;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ComputerUtil
 * @Description: 机器配置类
 * @Author zcm
 * @Date 2019-12-23
 * @Version V1.0
 **/
public class ComputerUtil {

    private static SystemInfo si = null;
    private static HardwareAbstractionLayer hal = null;
    private static OperatingSystem os = null;


    static {
        si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();
    }


    /**
     * 获取ip地址和机器名城
     * @return
     */
    public static Map<String,Object> getOnlyIp(){
        Map<String,Object> map = new HashMap<>();
        String localip = "";
        InetAddress ia=null;
        try {
            ia= InetAddress.getLocalHost();
            localip=ia.getHostAddress();
            map.put("ipaddr",localip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取ip地址和机器名城
     * @return
     */
    public static Map<String,Object> getIp(){
        Map<String,Object> map = new HashMap<>();
        String localip = "";
        InetAddress ia=null;
        try {
            ia=ia.getLocalHost();
            String localname=ia.getHostName();
            localip=ia.getHostAddress();
            //System.out.println("本机名称是："+ localname);
            //System.out.println("本机的ip是 ："+localip);
            map.put("ip",localip);
            map.put("pc",localname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取硬盘信息
     * @return
     */
    public static Map<String,Object> getDisk(){
        List<Map<String,Object>> list = new ArrayList<>();
        // 磁盘使用情况
        File[] files = File.listRoots();
        for (File file : files) {
            Map<String,Object> map = new HashMap<>();
            //盘符
            String path = file.getPath();
            //存储空间
            //1。总空间
            String total = new DecimalFormat("#.#").format(file.getTotalSpace() * 1.0 / 1024 / 1024 / 1024);
            String free = new DecimalFormat("#.#").format(file.getFreeSpace() * 1.0 / 1024 / 1024 / 1024);

            String rate = new DecimalFormat("#.###").format((1-Double.parseDouble(free)/Double.parseDouble(total))*100);

            map.put("diskName",path);
            map.put("total",total+"G");
            map.put("free",free+"G");
            map.put("rate",Double.valueOf(rate));
            list.add(map);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("disk",list);
        return map;
    }


    public static Map<String,Object> getMemory() {

        GlobalMemory memory = hal.getMemory();
        Map<String,Object> map = new HashMap<>();
        //Memory: 812.8 MiB/4 GiB
        map.put("available", FormatUtil.formatBytes(memory.getAvailable()));
        map.put("total",FormatUtil.formatBytes(memory.getTotal()));
        //使用率
        long used = memory.getTotal() - memory.getAvailable();
        double use = used*1.0/memory.getTotal() * 100;
        BigDecimal bigDecimal = new BigDecimal(use);
        map.put("used",bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

        Map<String,Object> memMap = new HashMap<>();
        memMap.put("memory",map);
        return memMap;
    }

    public static Map<String,Object> getCpuInfo(){
        CentralProcessor processor = hal.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        Map<String,Object> map = new HashMap<>();
        map.put("used",new DecimalFormat("#.##").format((1.0-(idle * 1.0 / totalCpu))*100));
        map.put("total","100%");
        map.put("available",new DecimalFormat("#.##%").format((idle * 1.0 / totalCpu)));
        Map<String,Object> cpuMap = new HashMap<>();
        cpuMap.put("cpu",map);
        return cpuMap;
    }


    public static void main(String[] args) {
        System.out.println(getCpuInfo());
    }
}
