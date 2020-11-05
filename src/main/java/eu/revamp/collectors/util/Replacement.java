package eu.revamp.collectors.util;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Replacement {
    private Map<Object, Object> replacements;
    private String message;

    public Replacement(String message) {
        setReplacements(new HashMap<>());
        setMessage(message);
    }

    public Replacement add(Object current, Object replacement) {
        getReplacements().put(current, replacement);
        return this;
    }

    @Override
    public String toString() {
        getReplacements().keySet().forEach(current -> setMessage(getMessage().replace(String.valueOf(current), String.valueOf(getReplacements().get(current)))));
        return CC.translate(getMessage());
    }
}