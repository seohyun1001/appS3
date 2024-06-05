package org.zerock.apps3.util;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class LocalUploader {
    @Value("${org.zerock.upload.path}") // folder of C drive
    private String uploadPath;

    // method for saving the files in C:\\upload folder
    public List<String> uploadLocal(MultipartFile multipartFile) {

        // confirm the file is existed
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // create UUID not to duplicate file names
        String uuid = UUID.randomUUID().toString();
        // create the file name formed by UUID+filename
        String saveFileName = uuid + "_" + multipartFile.getOriginalFilename();

        // setting of location to save file
        Path savePath = Paths.get(uploadPath, saveFileName);
        // setting for returning the original file and thumbnail file as setting List
        List<String> savePathList = new ArrayList<>();

        try {
            // using transferTo Method, save the file into savePath
            multipartFile.transferTo(savePath);
            // save the whole route of original file in 'savePathList'
            savePathList.add(savePath.toFile().getAbsolutePath());

            // create thumbnail if the original file is image
            if (Files.probeContentType(savePath).startsWith("image")) {
                // create the location of thumbnail file
                File thumbFile = new File(uploadPath, "s_" + saveFileName);
                // save the whole route of the thumbnail file in savePathList
                savePathList.add(thumbFile.getAbsolutePath());
                // create the thumbnail file and save them
                Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
            }
        } catch (Exception e) {
            log.error("ERROR : " + e.getMessage());
            e.printStackTrace();
        }
        return savePathList;
    }
}
