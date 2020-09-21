package com.app.service.validator.generic;

import java.util.Map;

public interface Validator<T> {
    Map<String, String> validate(T t);
}
