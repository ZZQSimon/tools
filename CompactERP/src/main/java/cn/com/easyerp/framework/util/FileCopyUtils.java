package cn.com.easyerp.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 文件拷贝工具类
 * 
 * @author Simon Zhang
 */
public class FileCopyUtils {

    /**
     * 使用FileChannel复制
     * </p>
     * Java NIO包括transferFrom方法,根据文档应该比文件流复制的速度更快。
     * <p>
     * Time taken by FileChannels Copy = 10449963
     * <p>
     * 
     * @param source
     * @param dest
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        String sFileName = source.getAbsolutePath();
        String dFileName = dest.getAbsolutePath();
        System.out.println("源文件路径：" + sFileName);
        System.out.println("备份文件路径" + dFileName);
        if (sFileName.equals(dFileName)) {
            System.out.println("目标文件和源文件相同, 不予复制! ");
            return;
        }
        createNewFile(dest);
        try (FileChannel inputChannel = new FileInputStream(source).getChannel()) {
            try (FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            }
        }
    }

    /**
     * 
     * @param dest
     * @throws IOException
     */
    public static void createNewFile(File dest) throws IOException {
        if (!dest.exists()) {
            File filepath = dest.getParentFile();
            if (!filepath.exists()) {
                filepath.mkdirs();
            }
            dest.createNewFile();
        }
    }
}
