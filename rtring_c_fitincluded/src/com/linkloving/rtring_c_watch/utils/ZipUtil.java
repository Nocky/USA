package com.linkloving.rtring_c_watch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ZipUtil {

    public static void zipFolder(String srcFilePath, String zipFilePath) throws Exception {

// 创建Zip包

        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFilePath));

// 打开要输出的文件

        File file = new File(srcFilePath);

// 压缩

        zipFiles(file.getParent() + File.separator, file.getName(), outZip);

// 完成,关闭

        outZip.finish();

        outZip.close();

    }

    private static void zipFiles(String folderPath, String filePath, ZipOutputStream zipOut)

            throws Exception {

        if (zipOut == null) {

            return;

        }

        File file = new File(folderPath + filePath);


// 判断是不是文件

        if (file.isFile()) {

            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(filePath);

            java.io.FileInputStream inputStream = new java.io.FileInputStream(file);

            zipOut.putNextEntry(zipEntry);


            int len;

            byte[] buffer = new byte[100000];


            while ((len = inputStream.read(buffer)) != -1) {

                zipOut.write(buffer, 0, len);

            }

            inputStream.close();

            zipOut.closeEntry();

        } else {

// 文件夹的方式,获取文件夹下的子文件

            String fileList[] = file.list();

// 如果没有子文件, 则添加进去即可

            if (fileList.length <= 0) {

                java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(filePath + java.io.File.separator);

                zipOut.putNextEntry(zipEntry);

                zipOut.closeEntry();

            }

// 如果有子文件, 遍历子文件

            for (int i = 0; i < fileList.length; i++) {

                zipFiles(folderPath, filePath + java.io.File.separator + fileList[i], zipOut);

            }

        }

    }
}
