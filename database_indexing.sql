-- HORONG 프로젝트 성능 최적화를 위한 데이터베이스 인덱스 추가 스크립트
-- 날짜: 2025-04-25

-- 게시글 타입 및 생성일 기준 인덱스 (게시글 목록 조회 성능 향상)
CREATE INDEX idx_post_type_created_at ON post(type, created_at DESC);

-- 댓글의 게시글 참조 인덱스 (댓글 조회 성능 향상)
CREATE INDEX idx_comment_post_id ON comment(board_id);

-- 메시지룸의 사용자 참조 인덱스 (메시지 조회 성능 향상)
CREATE INDEX idx_message_room_host_guest ON message_room(host_id, guest_id);

-- 알림 인덱스 (읽지 않은 알림 조회 성능 향상)
CREATE INDEX idx_notification_receiver_read ON notification(receiver_id, is_read);

-- 삭제되지 않은 게시글 쿼리 최적화
CREATE INDEX idx_post_deleted_at ON post(deleted_at);

-- 콘텐츠 언어 검색 최적화
CREATE INDEX idx_content_language ON content_by_country(language);

-- 댓글 소팅 인덱스
CREATE INDEX idx_comment_created_at ON comment(created_at DESC);

-- 메시지 읽음 여부 인덱스
CREATE INDEX idx_message_is_read ON message(is_read);
