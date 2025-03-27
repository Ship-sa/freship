package me.yeon.freship.orders.infrastructure;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OrderPolicyStringGenerator {

    // 1-9 숫자(0 제외), 알파벳 대소문자(o,i,j,u,v 제외)
    private static final String CANDIDATE_STRING = "123456789abcdefghklmnpqrstwxyzABCDEFGHKLMNPQRSTWXYZ";
    private static final int RAND_LENGTH = 4;

    private final Random random = new Random();

    public String create() {
        int candLength = CANDIDATE_STRING.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < RAND_LENGTH; i++) {
            int currentIdx = random.nextInt(candLength);
            sb.append(CANDIDATE_STRING.charAt(currentIdx));
        }

        return sb.toString();
    }
}
