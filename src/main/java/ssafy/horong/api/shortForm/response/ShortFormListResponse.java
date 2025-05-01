package ssafy.horong.api.shortForm.response;

public record ShortFormListResponse(
        Long id,
        String content,
        String image,
        String audio,
        Boolean is_saved,
        Integer preference
) {
}
