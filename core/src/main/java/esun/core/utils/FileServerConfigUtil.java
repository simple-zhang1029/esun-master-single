package esun.core.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by will on 1/16/19.
 */
@ConfigurationProperties(prefix = "file-server")
@Component
public class FileServerConfigUtil {
    private String imageDiskPath;
    private String excelDiskPath;

    public String getImageDiskPath() {
        return imageDiskPath;
    }

    public void setImageDiskPath(String imageDiskPath) {
        this.imageDiskPath = imageDiskPath;
    }

    public String getExcelDiskPath() {
        return excelDiskPath;
    }

    public void setExcelDiskPath(String excelDiskPath) {
        this.excelDiskPath = excelDiskPath;
    }

}
