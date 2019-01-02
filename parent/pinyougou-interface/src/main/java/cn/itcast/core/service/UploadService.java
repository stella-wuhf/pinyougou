package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Brand;

import java.util.List;

public interface UploadService {
    void upload(List<Brand> list) throws Exception;
}
