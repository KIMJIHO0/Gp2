package util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 단순 비밀번호 해시 유틸.
 * 주의: 데모용 수준. 고정 salt + 단일 SHA-256은 실무 보안 기준 미달.
 */
public final class Passwords {

    /**
     * 해시 생성
     * 입력: 평문 pw
     * 과정:
     *  - 고정 salt 바이트를 먼저 업데이트
     *  - SHA-256으로 digest
     *  - 결과를 Base64로 인코딩
     */
    public static String hash(String pw){
        try {
            byte[] salt = "fixed-salt-for-class".getBytes(); // 고정 salt(취약)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);                                 // salt 반영
            return Base64.getEncoder()
                    .encodeToString(md.digest(pw.getBytes())); // 해시→Base64
        } catch(Exception e){
            throw new RuntimeException(e); // 체크 예외 단순 포장
        }
    }

    /**
     * 평문과 저장된 해시 비교
     * 원리: 평문을 같은 방식으로 hash() 후 equals 비교
     * 주의: equals는 타이밍 공격 방어 불가.
     */
    public static boolean matches(String raw, String encoded) {
        return hash(raw).equals(encoded);
    }
}
