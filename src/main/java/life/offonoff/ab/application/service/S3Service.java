package life.offonoff.ab.application.service;

import life.offonoff.ab.exception.IllegalImageExtension;
import life.offonoff.ab.exception.S3InvalidFileUrlException;
import life.offonoff.ab.exception.S3InvalidKeyNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class S3Service {
    private final AwsCredentialsProvider credentialsProvider;
    private final String bucket;
    private final String profileImageName;
    private final String baseDir;
    private final Pattern fileNamePattern =
            Pattern.compile("(.*\\.(jpg|jpeg|png|gif|bmp))", Pattern.CASE_INSENSITIVE);
    private final Pattern keynamePattern = Pattern.compile("^https://.+\\.s3\\..+\\.amazonaws.com/(.+)");
    public S3Service(
            @Value("${cloud.aws.credentials.access-key}") String accessKey,
            @Value("${cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.s3.profile-image-name}") String profileImageName,
            @Value("${cloud.aws.s3.dir}") String baseDir) {
        this.credentialsProvider = StaticCredentialsProvider.create(
          AwsBasicCredentials.create(accessKey, secretKey));
        this.bucket = bucket;
        this.profileImageName = profileImageName;
        this.baseDir = baseDir;
    }

    public String createMembersProfileImageSignedUrl(Long memberId, String originalImageName) {
        String fileExtension = getImageFileExtension(originalImageName);
        String contentType = convertToImageContentType(fileExtension);
        String keyName = generateProfileImageKeyName(memberId, fileExtension);

        URL url =  createSignedUrlForImagePut(keyName, contentType);
        log.info("Presigned URL for Profile Image[memberId={} original={}] 발급 완료: [{}] ", memberId, originalImageName, url);
        return url.toString();
    }

    public String createMembersTopicImageSignedUrl(Long memberId, String originalImageName) {
        String fileExtension = getImageFileExtension(originalImageName);
        String contentType = convertToImageContentType(fileExtension);
        String keyName = generateTopicImageKeyName(memberId, fileExtension);

        URL url =  createSignedUrlForImagePut(keyName, contentType);
        log.info("Presigned URL for Topic Image[memberId={} original={}] 발급 완료: [{}] ", memberId, originalImageName, url);
        return url.toString();
    }

    private String generateProfileImageKeyName(Long memberId, String fileExtension) {
        return generateImageKeyName(memberId, "profile/" + profileImageName + "."+fileExtension);
    }

    private String generateTopicImageKeyName(Long memberId, String fileExtension) {
        return generateImageKeyName(memberId, "topics/" + createFileName(fileExtension));
    }

    private String generateImageKeyName(Long memberId, String fileNameWithPath) {
        return baseDir + "/" + memberId + "/" + fileNameWithPath;
    }

    private String convertToImageContentType(String fileExtension) {
        return "image/" + fileExtension;
    }

    private String createFileName(String fileExtension) {
        return UUID.randomUUID() + "."+ fileExtension;
    }

    private String getImageFileExtension(String fileName) {
        Matcher matcher = fileNamePattern.matcher(fileName);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        throw new IllegalImageExtension(fileName);
    }

    private URL createSignedUrlForImagePut(String keyName, String contentType) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(credentialsProvider)
                .build()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyName)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            return presignedRequest.url();
        }
    }

    public void deleteFile(String fileUrl) {
        String keyName = getKeyNameFromFileUrl(fileUrl);
        deleteObject(keyName);
    }

    private String getKeyNameFromFileUrl(String fileUrl) {
        Matcher matcher = keynamePattern.matcher(fileUrl);
        if (!matcher.matches()) {
            log.warn("S3 파일 삭제 요청 URL이 올바르지 않습니다. | URL: {}", fileUrl);
            throw new S3InvalidFileUrlException(fileUrl);
        }
        String keyName = matcher.group(1);
        // 띄어쓰기 포함된 파일 이름의 경우 +를 띄어쓰기로 대체해줘야 삭제됨
        return keyName.replace('+', ' ');
    }

    private void deleteObject(String keyName) {
        try (S3Client s3 = S3Client.builder()
                     .region(Region.AP_NORTHEAST_2)
                     .credentialsProvider(credentialsProvider)
                     .build()) {

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyName)
                    .build();

            DeleteObjectResponse response = s3.deleteObject(request);
            boolean isSuccessful = response.sdkHttpResponse().isSuccessful();
            if (!isSuccessful) {
                // ! 주의할 것
                // keyName이 존재해서 제대로 삭제되든,
                // keyName이 틀려서 존재하지 않는 파일이어도 204 No Content 응답이 온다.
                log.warn("keyName["+keyName+"] 삭제 실패 {} {}", response.sdkHttpResponse().statusCode(), response.sdkHttpResponse().statusText());
            }
        } catch (Exception e) {
            log.warn("S3 삭제 요청 실패 [keyName={}]", keyName, e);
            throw new S3InvalidKeyNameException(keyName);
        }
    }
}
