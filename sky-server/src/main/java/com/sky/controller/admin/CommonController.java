package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传:{}",file);

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名后缀 xxx.jpg
            String substring = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uuid = UUID.randomUUID().toString();
            //构造新文件名
            String name=uuid+substring;

            String filePath = aliOssUtil.upload(file.getBytes(), name);//返回值为文件请求路径

                return Result.success(filePath);

        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
        }


        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}

