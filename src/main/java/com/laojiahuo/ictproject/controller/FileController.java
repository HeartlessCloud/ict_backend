package com.laojiahuo.ictproject.controller;


import com.laojiahuo.ictproject.AO.UploadResponse;
import com.laojiahuo.ictproject.utils.JsonResult;
import com.laojiahuo.ictproject.utils.MinioUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class FileController {

    @Autowired
    @Lazy
    private MinioUtil minioUtil;

    /**
     * 设置主页的链接分享
     */
    @ApiOperation("图片上传，返回图片链接")
    @PostMapping("/uploadPicture")
    public JsonResult minioUpload(@RequestParam(value = "file") MultipartFile file) {
        JsonResult jsonResult = new JsonResult(200);
        UploadResponse response = null;
        try {
            response = minioUtil.uploadFile(file);
            jsonResult.setData(response);
            jsonResult.setMessage("上传成功");
            System.out.println(jsonResult);
        } catch (Exception e) {
            jsonResult.setMessage("上传失败");
            jsonResult.setCode(400);
            return jsonResult;
        }
        return jsonResult;
    }

}
