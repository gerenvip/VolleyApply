package com.gerenvip.volley.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wangwei-ps on 2014/9/5.
 */
public class FileHelper {
    /**
     * 下载文件 <b>请务必在子线程访问<b/>
     *
     * @param url      文件地址
     * @param destFile 保存的文件目录
     * @return true:下载成功，false：下载失败
     */
    public static boolean downloadFile(String url, File destFile) {
        FileOutputStream out = null;
        InputStream in = null;
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.connect();
            in = conn.getInputStream();
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.getFD().sync();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
