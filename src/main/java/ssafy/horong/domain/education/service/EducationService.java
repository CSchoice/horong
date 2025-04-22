package ssafy.horong.domain.education.service;

import ssafy.horong.api.education.response.*;
import ssafy.horong.domain.education.command.SaveEducationRecordCommand;

import java.time.LocalDate;
import java.util.List;

public interface EducationService {
    TodayWordsResponse getTodayWords();
    GetAllEducationRecordResponse getAllEducationRecord();
    EducationRecordResponse saveEducationRecord(SaveEducationRecordCommand command);
    List<LocalDate> getStampDates();
    GetEducationRecordByWordResponse getEducationRecordDetail(Long wordId);
}
