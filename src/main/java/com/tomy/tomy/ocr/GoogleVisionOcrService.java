package com.tomy.tomy.ocr;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class GoogleVisionOcrService implements OcrService {

    // application.yml: gcp.vision.credentials: classpath:keys/vision-sa.json
    @Value("${gcp.vision.credentials}")
    private Resource credentialsResource;

    @Override
    public OcrResult extractText(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 없음");
        if (file.getSize() > 5L * 1024 * 1024)
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "파일 최대 5MB");

        try (InputStream in = credentialsResource.getInputStream()) {
            GoogleCredentials cred = GoogleCredentials.fromStream(in);
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(cred))
                    .build();

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
                Image img = Image.newBuilder().setContent(ByteString.copyFrom(file.getBytes())).build();
                Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
                AnnotateImageRequest req = AnnotateImageRequest.newBuilder()
                        .addFeatures(feat).setImage(img).build();

                BatchAnnotateImagesResponse resp = client.batchAnnotateImages(List.of(req));
                AnnotateImageResponse r = resp.getResponses(0);
                if (r.hasError())
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Vision 오류: " + r.getError().getMessage());

                String text = r.getFullTextAnnotation() == null ? "" : r.getFullTextAnnotation().getText();
                if (text.isBlank())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OCR 결과 없음");

                String meta = "pages=" + (r.getFullTextAnnotation() == null ? 0 : r.getFullTextAnnotation().getPagesCount());
                return new OcrResult(text, meta);
            }
        } catch (com.google.api.gax.rpc.UnauthenticatedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vision 인증 실패: 키/프로젝트 확인");
        } catch (com.google.api.gax.rpc.PermissionDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vision 권한 부족(roles/vision.aiUser 필요)");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Vision 클라이언트 실패: " + e.getClass().getSimpleName());
        }
    }
}
