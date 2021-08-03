package pers.wuyou.robot.utils;

import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.Arrays;

public class FileUtil {

    private static final byte[] JAR_MAGIC = {'P', 'K', 3, 4};

    public static File saveTempFile(InputStream is, String fileName) {
        File temp = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp))) {
            int len;
            byte[] buf = new byte[10240];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static boolean isJar(File file) {
        return isJar(file, new byte[JAR_MAGIC.length]);
    }

    private static boolean isJar(File file, byte[] buffer) {
        try (InputStream is = new FileInputStream(file)) {
            if (is.read(buffer, 0, JAR_MAGIC.length) > 0 && Arrays.equals(buffer, JAR_MAGIC)) {//判断开始字节是不是jar包
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File getTempFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        File tempFile = FileUtil.saveTempFile(is, file.getName());
        is.close();
        return tempFile;
    }

    public static String getTempFileString(String path) throws IOException {
        File file = new File(path);
        FileInputStream is = new FileInputStream(file);
        File tempFile = FileUtil.saveTempFile(is, file.getName());
        is.close();
        byte[] bytes = FileCopyUtils.copyToByteArray(new FileInputStream(tempFile));
        return new String(bytes);
    }
}
