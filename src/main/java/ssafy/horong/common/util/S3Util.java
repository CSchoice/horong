package ssafy.horong.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import ssafy.horong.common.constant.global.S3_IMAGE;
import ssafy.horong.common.exception.s3.ExtensionNotAllowedException;
import ssafy.horong.common.exception.s3.PresignedUrlGenerationFailException;
import ssafy.horong.common.exception.s3.S3UploadFailedException;
import ssafy.horong.common.properties.S3Properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".PNG", ".JPG", ".JPEG", ".GIF", ".mp3", ".MP3", ".wav", ".WAV");

    private final AmazonS3 amazonS3Client;
    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;

    private static void validateFileExtension(String extension) {
        log.info("확장자: {}", extension);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ExtensionNotAllowedException();
        }
    }

    private static String getS3FileName(MultipartFile image, String fileName, String location) {
        String originalFilename = image.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        validateFileExtension(extension);

        return location + fileName + extension;
    }

    public String uploadToS3(MultipartFile imageFile, String fileName, String location) {
        String s3FileName = getS3FileName(imageFile, fileName, location); // S3에 업로드할 파일명 생성
        try (InputStream inputStream = imageFile.getInputStream()) { // try-with-resources 사용
            PutObjectRequest putObjectRequest = new PutObjectRequest(s3Properties.s3().bucket(), s3FileName, inputStream, null);

            // 스트림의 최대 읽기 한도 설정 (예: 10MB)
            putObjectRequest.getRequestClientOptions().setReadLimit(10 * 1024 * 1024); // 10MB

            // S3에 파일 업로드
            amazonS3Client.putObject(putObjectRequest);

            return s3FileName; // 객체 키만 반환
        } catch (IOException e) {
            log.error("S3 업로드 실패: {}", e.getMessage());
            throw new S3UploadFailedException();
        }
    }

    public String uploadUserImageToS3(MultipartFile imageFile, Long userId, String location, String existingImageUrl) {
        if (imageFile == null || imageFile.isEmpty()) {
            return existingImageUrl != null && !existingImageUrl.isEmpty() ? existingImageUrl : S3_IMAGE.DEFAULT_URL;
        }

        try {
            String fileName = getS3FileName(imageFile, userId.toString(), location);
            amazonS3Client.putObject(new PutObjectRequest(s3Properties.s3().bucket(), fileName, imageFile.getInputStream(), null));
            return fileName; // 객체 키만 반환
        } catch (IOException e) {
            throw new S3UploadFailedException();
        }
    }

    public List<String> uploardBoardImageToS3(MultipartFile[] images, Long postId) {
        int count = 0;
        String location = "Board/";
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originalFilename = image.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            validateFileExtension(extension);
            String fileName = location + count+ "of" + postId + extension;
            try {
                amazonS3Client.putObject(new PutObjectRequest(s3Properties.s3().bucket(), fileName, image.getInputStream(), null));
                log.info("S3에 이미지 업로드 성공: {}", fileName);
                imageUrls.add(fileName); // 객체 키만 저장
            } catch (IOException e) {
                log.error("S3 이미지 업로드 실패: {}", e.getMessage());
                throw new S3UploadFailedException();
            }
        }
        return imageUrls;
    }

    public String getPresignedUrlFromS3(String imagePath) {
        try {
            String objectKey = extractObjectKey(imagePath);

            GetObjectRequest getObjectRequest = createGetObjectRequest(objectKey);
            GetObjectPresignRequest getObjectPresignRequest = createGetObjectPresignRequest(getObjectRequest);

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            URL presignedUrl = presignedRequest.url();

            log.info("{} 이미지에 대한 presigned URL 생성 성공", objectKey);
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 오류 발생: {}", e.getMessage());
            throw new PresignedUrlGenerationFailException();
        }
    }

    public String getProfilePresignedUrlFromS3(String number) {
        try {
            String imagePath = "profileImg/" + number + ".png";
            String objectKey = extractObjectKey(imagePath);

            GetObjectRequest getObjectRequest = createGetObjectRequest(objectKey);
            GetObjectPresignRequest getObjectPresignRequest = createGetObjectPresignRequest(getObjectRequest);

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            URL presignedUrl = presignedRequest.url();

            log.info("{} 이미지에 대한 presigned URL 생성 성공", objectKey);
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 오류 발생: {}", e.getMessage());
            throw new PresignedUrlGenerationFailException();
        }
    }

//    public URI getS3UrlFromS3(String objectKey) {
//        // S3 버킷의 기본 URL을 앞에 붙여서 URI 객체로 반환합니다.
//        String baseUrl = "https://horong-service.s3.ap-northeast-2.amazonaws.com/";
//        String fullUrl = baseUrl + objectKey;
//
//        log.info("생성된 S3 URI: {}", fullUrl);
//
//        // String을 URI로 변환하여 반환
//        return URI.create(fullUrl);
//    }

    public URI getS3UrlFromS3(String imagePath) {
        try {
            // imagePath에서 S3 객체 키 추출
            String objectKey = extractObjectKey(imagePath);
            log.info("Presigned URL을 생성할 객체 키: {}", objectKey);

            // GetObjectRequest 생성
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.s3().bucket())
                    .key(objectKey)
                    .build();

            // Presigned URL 요청 생성 (유효 기간 10분 설정)
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(10))  // Presigned URL의 유효 기간 설정
                    .build();

            // Presigned URL 생성
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            URL presignedUrl = presignedRequest.url();

            log.info("생성된 Presigned URL: {}", presignedUrl.toString());

            // URL을 URI로 변환하여 반환
            return presignedUrl.toURI();
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 오류 발생: {}", e.getMessage());
            throw new PresignedUrlGenerationFailException();
        }
    }

    private String extractObjectKey(String imagePath) {
        return imagePath.replace("https://sera-image.s3.ap-northeast-2.amazonaws.com/", "");
    }

    public String getFullS3ImageUrl(String objectKey) {
        // 객체 키에 S3 URL을 붙여서 반환
        return "https://sera-image.s3.ap-northeast-2.amazonaws.com/" + objectKey;
    }

    private GetObjectRequest createGetObjectRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(s3Properties.s3().bucket())
                .key(objectKey)
                .build();
    }

    private GetObjectPresignRequest createGetObjectPresignRequest(GetObjectRequest getObjectRequest) {
        return GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
                .build();
    }
}
