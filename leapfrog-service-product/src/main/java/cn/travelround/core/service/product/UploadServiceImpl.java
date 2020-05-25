package cn.travelround.core.service.product;

import cn.itcast.common.page.Pagination;
import cn.travelround.common.fdfs.FastDFSUtils;
import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.bean.product.BrandQuery;
import cn.travelround.core.dao.product.BrandDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by travelround on 2019/4/10.
 */
@Service("uploadService")
@Transactional
public class UploadServiceImpl implements UploadService {

    @Override
    public String uploadPic(byte[] pic, String name, long size) {
        return FastDFSUtils.uploadPic(pic, name, size);
    }
}
