package dev.sprikers.moustacheshop.exceptions;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class ApiException extends Exception {
    private final List<String> messages;

    public ApiException(List<String> messages) {
        super(messages.size() == 1 ? messages.getFirst() : "Algunas excepciones ocurrieron");
        this.messages = messages;
    }

    public ApiException(String message) {
        super(message);
        this.messages = Collections.singletonList(message);
    }

    @Override
    public String getMessage() {
        return messages.isEmpty() ? "" : "- " + String.join("\n- ", messages);
    }

}
