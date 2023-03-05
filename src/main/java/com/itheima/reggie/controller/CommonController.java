package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传，下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
//        参数名保持一致
//        file是一个临时文件，需要转存到一个目录，本次请求完成后删除
//        log.error("SSS");
//        获取原始文件名
        String originalFilename = file.getOriginalFilename();

//        获得后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        使用UUID生成文件名，防止文件名重复
        String fileName = UUID.randomUUID().toString()+suffix;
        log.info(file.toString());
//        判断存不存在目录
        File dir = new File(basePath);
        if(!dir.exists()){
//            目录不存在，创建
            dir.mkdirs();
        }

        try {
//            log.error("进来");
//            log.error(basePath+"emm.jpg");
//            log.error("C:\\emm.jpg");
            file.transferTo(new File(basePath+fileName));
//            log.error("完成");
        } catch (IOException e) {
//            log.error("失败");
            throw new RuntimeException(e);
        }
        return  R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
//        输入流，通过输入流读取文件内容

        try {
            FileInputStream fileInputStream =  new FileInputStream(new File(basePath+name));
//        输出流，通过输出流谢辉浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
