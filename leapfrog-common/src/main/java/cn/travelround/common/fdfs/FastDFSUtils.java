package cn.travelround.common.fdfs;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by travelround on 2019/4/11.
 */
public class FastDFSUtils {

    // 上传文件,并返回图片网络路径
    public static String uploadPic(byte[] pic, String name, long size) {

        String path = null;

        ClassPathResource resource = new ClassPathResource("fdfs_client.conf");
        try {
            // ClientGlobal.init - FastDFS初始化配置文件
            // 通过配置文件名字找到对应的路径
            ClientGlobal.init(resource.getClassLoader().getResource("fdfs_client.conf").getPath());

            // 创建tracker(老大)并连接
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            // 创建storage(小弟)
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);

            // 截取扩展名
            String ext = FilenameUtils.getExtension(name);

            // org.csource.common.NameValuePair
            NameValuePair[] meta_list = new NameValuePair[3];
            meta_list[0] = new NameValuePair("fileName", name);
            meta_list[1] = new NameValuePair("fileExt", ext);
            meta_list[2] = new NameValuePair("fileSize", String.valueOf(size));

            // 核心功能方法 - 文件上传并返回地址
            path = storageClient1.upload_file1(pic, ext, meta_list);
            System.out.println("图片路径:" + path);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}
