package ssafy.horong.domain.education.command;

import org.springframework.web.multipart.MultipartFile;

public record SaveEducationRecordCommand(
        String word,
        MultipartFile audio
) {

}
