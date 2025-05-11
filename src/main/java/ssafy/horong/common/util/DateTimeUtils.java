package ssafy.horong.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 날짜 및 시간 처리를 위한 유틸리티 클래스
 * 엔티티 및 서비스 레이어에서 공통으로 사용되는 날짜 관련 메서드 제공
 */
public class DateTimeUtils {

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    private DateTimeUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
        throw new IllegalStateException("Utility class");
    }

    /**
     * LocalDateTime을 기본 포맷의 문자열로 변환
     * @param dateTime 변환할 LocalDateTime
     * @return 포맷된 문자열, null인 경우 빈 문자열
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * LocalDateTime을 지정된 포맷의 문자열로 변환
     * @param dateTime 변환할 LocalDateTime
     * @param pattern 날짜 포맷 패턴
     * @return 포맷된 문자열, null인 경우 빈 문자열
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * LocalDate를 기본 포맷의 문자열로 변환
     * @param date 변환할 LocalDate
     * @return 포맷된 문자열, null인 경우 빈 문자열
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DEFAULT_DATE_FORMATTER);
    }

    /**
     * 문자열을 LocalDateTime으로 파싱
     * @param dateTimeStr 파싱할 문자열
     * @return 파싱된 LocalDateTime, 파싱 실패 시 null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 문자열을 LocalDate로 파싱
     * @param dateStr 파싱할 문자열
     * @return 파싱된 LocalDate, 파싱 실패 시 null
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DEFAULT_DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Date를 LocalDateTime으로 변환
     * @param date 변환할 Date
     * @return 변환된 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * LocalDateTime을 Date로 변환
     * @param localDateTime 변환할 LocalDateTime
     * @return 변환된 Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 현재 날짜와 시간 반환
     * @return 현재 LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 현재 날짜 반환
     * @return 현재 LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 두 날짜 사이의 일수 계산
     * @param start 시작 날짜
     * @param end 종료 날짜
     * @return 두 날짜 사이의 일수
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates must not be null");
        }
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
}
