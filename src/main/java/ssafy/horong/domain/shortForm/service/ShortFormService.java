package ssafy.horong.domain.shortForm.service;

import ssafy.horong.api.shortForm.response.ShortFormListResponse;
import ssafy.horong.api.shortForm.response.ShortFormResponse;
import ssafy.horong.domain.shortForm.command.ModifyIsSavedCommand;
import ssafy.horong.domain.shortForm.command.SaveShortFormLogCommand;
import ssafy.horong.domain.shortForm.command.ModifyPreferenceCommand;

import java.util.List;

public interface ShortFormService {
    List<ShortFormResponse> getShortFormList();
    List<ShortFormResponse> getPreferenceList();
    List<ShortFormResponse> getLikedList();
    ShortFormListResponse getShortFormDetail(Long shortFormId);
    String saveShortFormLog(SaveShortFormLogCommand command);
    String modifyPreference(ModifyPreferenceCommand command);
    String modifyIsSaved(ModifyIsSavedCommand command);
}