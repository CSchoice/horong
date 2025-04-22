package ssafy.horong.api.health;

import org.springframework.web.multipart.MultipartFile;

public record Mp3TestRequest(
        MultipartFile mp3
){
}
