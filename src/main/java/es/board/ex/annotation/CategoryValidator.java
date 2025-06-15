package es.board.ex.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class CategoryValidator implements ConstraintValidator<ValidCategory, String> {
    private static final Set<String> VALID_CATEGORIES = Set.of("자유", "자격증", "문제", "기술", "취업", "Q/A", "자료");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && VALID_CATEGORIES.contains(value);
    }
}
