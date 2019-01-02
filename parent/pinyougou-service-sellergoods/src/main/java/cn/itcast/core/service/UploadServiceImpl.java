package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UploadServiceImpl implements UploadService {

    @Autowired
    private BrandDao brandDao;
    @Transactional(readOnly = false,rollbackFor = Exception.class)
    @Override
    public void upload(List<Brand> brandList) throws Exception {
//        List<Brand> brandList = new ArrayList<Brand>();
////        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
////            throw new RuntimeException("上传文件格式不正确");
////        }
//        boolean isExcel2003 = true;
//        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            isExcel2003 = false;
//        }
//        InputStream is = file.getInputStream();
//        Workbook wb = null;
//        if (isExcel2003) {
//            wb = new HSSFWorkbook(is);
//        } else {
//            wb = new XSSFWorkbook(is);
//        }
//        Sheet sheet = wb.getSheetAt(0);
//
//        Brand brand=null;
//
//        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
//            Row row = sheet.getRow(r);
//            if (row == null){
//                continue;
//            }
//
//            brand = new Brand();
//
//            if( row.getCell(0).getCellType() !=1){
//                throw new RuntimeException("导入失败(第"+(r+1)+"行,姓名请设为文本格式)");
//            }
//            String name = row.getCell(1).getStringCellValue();
//
//            if(name == null || name.isEmpty()){
//                throw new RuntimeException("导入失败(第"+(r+1)+"行,姓名未填写)");
//            }
//
//         //   row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
//            String firstChar = row.getCell(2).getStringCellValue();
//            if(firstChar==null || firstChar.isEmpty()){
//                throw new RuntimeException("导入失败(第"+(r+1)+"行,首字母未填写)");
//            }
////            String add = row.getCell(2).getStringCellValue();
////            if(add==null){
////                throw new RuntimeException("导入失败(第"+(r+1)+"行,不存在此单位或单位未填写)");
////            }
////
////            Date date;
////            if(row.getCell(3).getCellType() !=0){
////                throw new RuntimeException("导入失败(第"+(r+1)+"行,入职日期格式不正确或未填写)");
////            }else{
////                date = row.getCell(3).getDateCellValue();
////            }
////
////            String des = row.getCell(4).getStringCellValue();
//
//            brand.setName(name);
//            brand.setFirstChar(firstChar);
////            brand.setAddress(add);
////            brand.setEnrolDate(date);
////            brand.setDes(des);
//
//            brandList.add(brand);
//        }
        for (Brand brand1 : brandList) {
            brandDao.insertSelective(brand1);
        }

    }
}
