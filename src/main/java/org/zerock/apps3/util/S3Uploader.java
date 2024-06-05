package org.zerock.apps3.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
@Log4j2
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    // upload S3 file
    public String upload(String filePath) throws RuntimeException {

        // call file for using the whole route of saved files at UploadLocal
        File targetFile = new File(filePath);

        // save the file in to S3 storage using putS3 method
        String uploadImageUrl = putS3(targetFile, targetFile.getName());
        // delete the saved files in C:\\upload
        removeOriginalFile(targetFile);

        return uploadImageUrl;
    }

    // upload S3
    private String putS3(File uploadFile, String fileName) throws RuntimeException {
        // save the files into S2 storage using putObject method
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        // return the address can call the saved files
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // delete the original after upload S3
    private void removeOriginalFile(File targetFile) {
        // confirm the file is existed in targetFile Object
        // && after delete the file, if the file deleted = true, if error id happened = false
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("fail to remove");
    }

    public void removeS3File(String fileName) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);

        amazonS3Client.deleteObject(deleteObjectRequest);
    }

}
