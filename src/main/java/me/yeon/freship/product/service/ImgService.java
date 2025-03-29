package me.yeon.freship.product.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// S3 작업은 DB 트랜잭션과 무관함
public class ImgService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile imgFile) {
        if (imgFile == null || imgFile.isEmpty()) {
            throw new ClientException(ErrorCode.IMAGE_FILE_EMPTY);
        }

        String originalFilename = imgFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imgFile.getSize());
            metadata.setContentType(imgFile.getContentType());

            amazonS3.putObject(bucket, uniqueFilename, imgFile.getInputStream(), metadata);
            String imgUrl = amazonS3.getUrl(bucket, uniqueFilename).toString();

            return imgUrl;
        } catch (IOException e) {
            throw new ClientException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }


    public void deleteImage(String imgFile) {
        try {
            String decodedKey = URLDecoder.decode(imgFile, StandardCharsets.UTF_8.name());
            if (amazonS3.doesObjectExist(bucket, decodedKey)) {
                amazonS3.deleteObject(bucket, decodedKey);
            }
        } catch (Exception e) {
            throw new ClientException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}
