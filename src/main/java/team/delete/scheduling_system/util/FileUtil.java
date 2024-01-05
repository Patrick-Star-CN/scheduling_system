package team.delete.scheduling_system.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Patrick_Star
 * @version 1.0
 */
public class FileUtil {
    public static File convertToFile(MultipartFile multipartFile) throws IOException {
        File file = new File("/home/ubuntu/sc/tmp/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }
}
