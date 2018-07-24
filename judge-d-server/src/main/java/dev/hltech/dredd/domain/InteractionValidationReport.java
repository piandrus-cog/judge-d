package dev.hltech.dredd.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InteractionValidationReport {

    private final String name;
    private final InteractionValidationResult status;
    private final List<String> errors;

    public enum InteractionValidationResult {

        OK,
        FAILED

    }
}