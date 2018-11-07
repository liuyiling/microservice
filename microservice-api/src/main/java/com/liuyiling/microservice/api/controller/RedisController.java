package com.liuyiling.microservice.api.controller;

import com.liuyiling.microservice.api.apiversion.ApiVersion;
import com.liuyiling.microservice.core.readlock.RedisRedLock;
import com.liuyiling.microservice.core.util.KeyUtil;
import com.liuyiling.microservice.core.util.ResultBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
@RestController
@ApiVersion(1)
@RequestMapping(value = "/redis")
@Api(value = "缓存接口", description = "缓存接口示例")
public class RedisController {

    @Autowired
    private RedissonClient redissonClient;

    @ApiOperation(value = "KV操作")
    @GetMapping(value = "/kvOp")
    public ResultBean<Integer> kvOp() {
        RBucket<Object> existKey = redissonClient.getBucket(KeyUtil.getUserKey("liuyiling"));
        if (!existKey.isExists()) {
            existKey.set("610");
        }
        return new ResultBean<>();
    }

    @ApiOperation(value = "Map操作")
    @GetMapping(value = "/mapOp")
    public ResultBean<Integer> mapOp() {
        RMap<Object, Object> existKey = redissonClient.getMap("EmployeeData");
        if (existKey.isEmpty()) {
            existKey.put("liuyiling", "610");
        }
        return new ResultBean<>();
    }

    /**
     * 在不同进程需要互斥地访问共享资源时，分布式锁是一种非常有用的技术手段。实现高效的分布式锁有三个属性需要考虑：
     * 安全属性：互斥，不管什么时候，只有一个客户端持有锁
     * 效率属性：不会死锁，容错，只要大多数redis节点能够正常工作，客户端都能获取和释放锁。
     *
     * @return
     */
    @ApiOperation(value = "秒杀场景，模拟全局redis互斥锁")
    @GetMapping(value = "/lock")
    @RedisRedLock
    public ResultBean<Integer> lock() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                RMap<Object, Object> existKey = redissonClient.getMap("EmployeeData");
                existKey.addAndGet("count", 1);
            }).start();
        }

        return new ResultBean<>();
    }

}
