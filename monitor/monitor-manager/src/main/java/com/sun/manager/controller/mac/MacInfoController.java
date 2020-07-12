package com.sun.manager.controller.mac;

import com.alibaba.fastjson.JSONObject;
import com.sun.manager.entity.mac.MacInfo;
import com.sun.manager.service.mac.IMacInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zcm
 */
@RestController
@RequestMapping("/mac")
public class MacInfoController {

    @Autowired
    IMacInfoService macInfoService;

    @GetMapping("/list")
    public String getMacInfoList(){
        List<MacInfo> list = macInfoService.list();
        return JSONObject.toJSONString(list);
    }

    @PutMapping("/save")
    public String saveMacInfo(MacInfo macInfo){
        boolean flag = macInfoService.save(macInfo);
        return flag ? "success" : "failure";
    }


    @PutMapping("/update")
    public String updateMacInfo(MacInfo macInfo){
        boolean flag = macInfoService.updateById(macInfo);
        return flag ? "success" : "failure";
    }
}
