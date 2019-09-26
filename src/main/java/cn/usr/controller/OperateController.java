package cn.usr.controller;

import cn.usr.service.CloudService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Package: cn.usr.controller
 * @Description: 透传云TCP服务内部操作Http接口
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2019-01-03 9:47
 */
@RestController
@Slf4j
@EnableSwagger2
@Api(tags = "OperateController", description = "TCP服务器内部操作")
@RequestMapping("operates")
public class OperateController {

    @Autowired
    private CloudService cloudService;

    @DeleteMapping("/deleteDeviceCache")
    @ApiOperation(value = "删除TCP服务器的设备内置缓存")
    public Object deleteDeviceCache(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        log.info("[API操作删除TCP服务器的设备内置缓存] 设备ID为：{}", deviceId);
        return cloudService.deleteDeviceCache(deviceId);
    }

    @DeleteMapping("/deleteDeviceCacheAndCloseConnection")
    @ApiOperation(value = "删除设备缓存并且关闭设备连接")
    public Object deleteDeviceCacheAndCloseConnection(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        log.info("[API操作删除设备缓存并且关闭设备连接] 设备ID为：{}", deviceId);
        return cloudService.deleteDeviceCacheAndCloseConnection(deviceId);
    }

    @GetMapping("/getDeviceConnectNumberForRedis")
    @ApiOperation(value = "从Redis中获取TCP设备连接数量")
    public Object getDeviceConnectNumberForRedis() {
        log.info("[从Redis中获取TCP设备连接数量]");
        return cloudService.getDeviceConnectNumberForRedis();
    }


    @GetMapping("/getDeviceConnectNumberForHashMap")
    @ApiOperation(value = "从内存中获取TCP设备连接数量")
    public Object getDeviceConnectNumberForHashMap() {
        log.info("[从内存中获取TCP设备连接数量]");
        return cloudService.getDeviceConnectNumberForHashMap();
    }

    @GetMapping("/getDeviceInfoForRedis")
    @ApiOperation(value = "从Redis中获取设备缓存信息")
    public Object getDeviceInfoForRedis(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        log.info("[API操作从Redis中获取设备缓存信息] 设备ID为：{}", deviceId);
        return cloudService.getDeviceInfoForRedis(deviceId);
    }


}
