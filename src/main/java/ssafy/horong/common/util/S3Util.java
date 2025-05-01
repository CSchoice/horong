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
import ssafy.horong.common.constant.global.S3Image;
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

    // 허용된 파일 확장자 상수
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".PNG", ".JPG", ".JPEG", ".GIF", ".mp3", ".MP3", ".wav", ".WAV");
    
    // 위치, 경로 관련 상수
    private static final String LOCATION_BOARD = "Board/";
    private static final String LOCATION_PROFILE_IMAGE = "profileImg/";
    private static final String FILE_EXTENSION_PNG = ".png";
    private static final String COMMUNITY_PATH = "community/";
    
    // S3 URL 관련 상수
    private static final String S3_BASE_URL = "https://sera-image.s3.ap-northeast-2.amazonaws.com/";
    
    // 설정 관련 상수
    private static final int MAX_READ_LIMIT_BYTES = 10 * 1024 * 1024; // 10MB
    private static final Duration URL_EXPIRATION = Duration.ofMinutes(10);

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

            // 스트림의 최대 읽기 한도 설정
            putObjectRequest.getRequestClientOptions().setReadLimit(MAX_READ_LIMIT_BYTES);

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
            return existingImageUrl != null && !existingImageUrl.isEmpty() ? existingImageUrl : S3Image.DEFAULT_URL;
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
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originalFilename = image.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            validateFileExtension(extension);
            String fileName = LOCATION_BOARD + count+ "of" + postId + extension;
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

    /**
     * S3 객체에 대한 Presigned URL을 생성하는 공통 메서드
     * @param objectKey S3 객체 키
     * @return 생성된 Presigned URL
     */
    private String generatePresignedUrl(String objectKey) {
        try {
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

    public String getPresignedUrlFromS3(String imagePath) {
        String objectKey = extractObjectKey(imagePath);
        return generatePresignedUrl(objectKey);
    }

    public String getProfilePresignedUrlFromS3(String number) {
        String imagePath = LOCATION_PROFILE_IMAGE + number + FILE_EXTENSION_PNG;
        String objectKey = extractObjectKey(imagePath);
        return generatePresignedUrl(objectKey);
    }



    public URI getS3UrlFromS3(String imagePath) {
        try {
            // imagePath에서 S3 객체 키 추출
            String objectKey = extractObjectKey(imagePath);
            log.info("Presigned URL을 생성할 객체 키: {}", objectKey);
            
            // 생성된 Presigned URL을 URI로 변환하여 반환
            URL presignedUrl = new URL(generatePresignedUrl(objectKey));
            return presignedUrl.toURI();
        } catch (Exception e) {
            log.error("S3 URL 생성 중 오류 발생: {}", e.getMessage());
            throw new PresignedUrlGenerationFailException();
        }
    }

    private String extractObjectKey(String imagePath) {
        return imagePath.replace(S3_BASE_URL, "");
    }

    public String getFullS3ImageUrl(String objectKey) {
        return S3_BASE_URL + objectKey;
    }

    private GetObjectRequest createGetObjectRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(s3Properties.s3().bucket())
                .key(objectKey)
                .build();
    }

    private GetObjectPresignRequest createGetObjectPresignRequest(GetObjectRequest getObjectRequest) {
        final Duration URL_EXPIRATION = Duration.ofMinutes(10);
        return GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(URL_EXPIRATION)
                .build();
    }
}
