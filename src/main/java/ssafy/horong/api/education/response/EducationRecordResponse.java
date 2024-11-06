package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public record EducationRecordResponse(
        Long id,
        Education education,
        float cer,
        List<Integer> gtIdx,
        List<Integer> hypIdx,
        LocalDate date,
        URI audio
) {
}
