package com.sun.monitorclient.protocol;

import com.sun.monitorclient.util.ComputerUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcm
 */
public class TransProtocol {

    public static Map<String,Object> getMsg(int flag){
        switch (flag) {
            case 0:
                return getIpInfo();
            case 1:
                return getBaseInfo();
            default:
                return null;
        }
    }

    private static Map<String,Object> getIpInfo(){
        Map<String,Object> map = new HashMap<String, Object>(10);
        map = ComputerUtil.getOnlyIp();
        return map;
    }

    private static Map<String,Object> getBaseInfo(){
        Map<String,Object> map = new HashMap<String, Object>(10);
        map = ComputerUtil.getIp();
        Map<String,Object> diskMap = new HashMap<String, Object>(10);
        diskMap = ComputerUtil.getDisk();
        Map<String,Object> memMap = new HashMap<>(10);
        memMap = ComputerUtil.getMemory();
        Map<String,Object> cpuMap = new HashMap<>(10);
        cpuMap = ComputerUtil.getCpuInfo();
        map.putAll(diskMap);
        map.putAll(memMap);
        map.putAll(cpuMap);
        map.put("info","");
        return map;
    }
}
