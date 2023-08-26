package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {//这种操作redis都用不到service和dao

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("设置店铺营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态为:{}",status==1 ? "营业中" : "打烊中");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY,status);
        return Result.success();
    }
    @ApiOperation("查询店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        //之前用Integer存进去的,所以取的时候也用Integer
        Integer status = (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺营业状态为:{}",status==1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
