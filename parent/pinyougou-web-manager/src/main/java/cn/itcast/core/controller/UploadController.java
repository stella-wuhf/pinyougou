package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.UploadService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  文件(图片)上传
 */
@RestController
@RequestMapping("upload")
public class UploadController {

    //获取配置文件中的属性
    @Value("${FILE_SERVER_URL}")
    private String url;

    /*商品图片的上传*/
    //入参:  Springmvc 接收 form 表单中的图片
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            //1:上传图片到分布式文件系统
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //扩展名
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            //上传图片
            String path = fastDFSClient.uploadFile(file.getBytes(), ext);
            return new Result(true,url + path);//硬编码问题
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传图片失败");
        }
    }
    @Reference
    private UploadService uploadService;
    @RequestMapping("uploadFileExcel")
    public Result uploadFileExcel(@RequestParam(value ="file",required = true) MultipartFile file)  {
        String fileName = file.getOriginalFilename();
        try {
            //System.out.println(file);
//            uploadService.upload(file,filename);



            List<Brand> brandList = new ArrayList<Brand>();
//        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            throw new RuntimeException("上传文件格式不正确");
//        }
            boolean isExcel2003 = true;
            if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
                isExcel2003 = false;
            }
            InputStream is = file.getInputStream();
            Workbook wb = null;
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {
                wb = new XSSFWorkbook(is);
            }
            Sheet sheet = wb.getSheetAt(0);

            Brand brand=null;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (!"".equals(row.getCell(1).getStringCellValue())){
                    String s = row.getCell(1).getStringCellValue();
                    brand = new Brand();

                    if( row.getCell(1).getCellType()!=1){
                        throw new RuntimeException("导入失败(第"+(r+1)+"行,姓名请设为文本格式)");
                    }
                    String name = row.getCell(1).getStringCellValue();

                    if(name == null || name.isEmpty()){
                        throw new RuntimeException("导入失败(第"+(r+1)+"行,姓名未填写)");
                    }

                    //   row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    String firstChar = row.getCell(2).getStringCellValue();
                    if(firstChar==null || firstChar.isEmpty()){
                        throw new RuntimeException("导入失败(第"+(r+1)+"行,首字母未填写)");
                    }
//            String add = row.getCell(2).getStringCellValue();
//            if(add==null){
//                throw new RuntimeException("导入失败(第"+(r+1)+"行,不存在此单位或单位未填写)");
//            }
//
//            Date date;
//            if(row.getCell(3).getCellType() !=0){
//                throw new RuntimeException("导入失败(第"+(r+1)+"行,入职日期格式不正确或未填写)");
//            }else{
//                date = row.getCell(3).getDateCellValue();
//            }
//
//            String des = row.getCell(4).getStringCellValue();

                    brand.setName(name);
                    brand.setFirstChar(firstChar);
//            brand.setAddress(add);
//            brand.setEnrolDate(date);
//            brand.setDes(des);

                    brandList.add(brand);
                }

            }
            uploadService.upload(brandList);
            return new Result(true,"上传成功");

        } catch (RuntimeException e) {
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            return new Result(false,"失败");
        }
    }
}
