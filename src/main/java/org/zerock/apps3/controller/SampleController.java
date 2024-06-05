package org.zerock.apps3.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.apps3.dto.SampleDTO;
import org.zerock.apps3.util.LocalUploader;
import org.zerock.apps3.util.S3Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/sample")
public class SampleController {

    private final LocalUploader localUploader;

    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public List<String> upload(SampleDTO sampleDTO) {

        // 여러 개의 파일을 받아온 후 저장하는 변수
        // The variable for saving after receiving several files
        MultipartFile[] files = sampleDTO.getFiles();

        // 파일이 존재하는지 확인하고 존재하지 않으면 null 반환
        // confirm the file is existed, if not exist then return null
        if (files == null || files.length <= 0) {
            return null;
        }

        // the variable for saving the path of file
        List<String> uploadedFilePaths = new ArrayList<>();

        // process of dividing the files into one by one
        for (MultipartFile file : files) {
            uploadedFilePaths.addAll(localUploader.uploadLocal(file));
        }

        log.info("--------------------------------------");
        log.info(uploadedFilePaths);

        // save the files into S3 storage
        List<String> s3Paths =
                uploadedFilePaths.stream().map(fileName -> s3Uploader.
                        upload(fileName)).collect(Collectors.toList());

        return s3Paths;
    }
}
