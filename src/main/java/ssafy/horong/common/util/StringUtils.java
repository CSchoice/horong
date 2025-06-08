package ssafy.horong.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 문자열 처리를 위한 유틸리티 클래스
 * 엔티티 및 서비스 레이어에서 공통으로 사용되는 문자열 관련 메서드 제공
 */
public class StringUtils {

    private StringUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
        throw new IllegalStateException("Utility class");
    }

    /**
     * 문자열이 null이거나 빈 문자열인지 확인
     * @param str 확인할 문자열
     * @return null이거나 빈 문자열이면 true, 그렇지 않으면 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 문자열이 null이거나 빈 문자열이거나 공백 문자로만 이루어져 있는지 확인
     * @param str 확인할 문자열
     * @return null이거나 빈 문자열이거나 공백 문자로만 이루어져 있으면 true, 그렇지 않으면 false
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * null 안전 문자열 반환
     * 문자열이 null인 경우 빈 문자열 반환
     * @param str 처리할 문자열
     * @return null이 아닌 문자열
     */
    public static String nullSafeString(String str) {
        return str == null ? "" : str;
    }

    /**
     * 문자열 잘라내기
     * 최대 길이를 초과하는 경우 잘라내고 "..." 추가
     * @param str 처리할 문자열
     * @param maxLength 최대 길이
     * @return 잘라낸 문자열
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    /**
     * 랜덤 UUID 생성
     * @return UUID 문자열
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 문자열 목록을 구분자로 결합
     * @param strings 결합할 문자열 목록
     * @param delimiter 구분자
     * @return 결합된 문자열
     */
    public static String join(List<String> strings, String delimiter) {
        if (strings == null || strings.isEmpty()) {
            return "";
        }
        return String.join(delimiter, strings);
    }

    /**
     * 문자열을 구분자로 분리
     * @param str 분리할 문자열
     * @param delimiter 구분자
     * @return 분리된 문자열 목록
     */
    public static List<String> split(String str, String delimiter) {
        if (isEmpty(str)) {
            return List.of();
        }
        return Arrays.stream(str.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 문자열에서 HTML 태그 제거
     * @param html HTML 태그가 포함된 문자열
     * @return HTML 태그가 제거된 문자열
     */
    public static String stripHtmlTags(String html) {
        if (isEmpty(html)) {
            return "";
        }
        return html.replaceAll("<[^>]*>", "");
    }

    /**
     * 문자열이 이메일 형식인지 확인
     * @param email 확인할 이메일 문자열
     * @return 이메일 형식이면 true, 그렇지 않으면 false
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * 문자열을 카멜 케이스로 변환
     * @param str 변환할 문자열
     * @return 카멜 케이스로 변환된 문자열
     */
    public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        
        for (char c : str.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }

    /**
     * 문자열을 스네이크 케이스로 변환
     * @param str 변환할 문자열
     * @return 스네이크 케이스로 변환된 문자열
     */
    public static String toSnakeCase(String str) {
        if (isEmpty(str)) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else if (c == ' ' || c == '-') {
                result.append('_');
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
}
