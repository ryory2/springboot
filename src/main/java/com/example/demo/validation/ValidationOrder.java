package com.example.demo.validation;

import jakarta.validation.GroupSequence;

@GroupSequence({First.class, Second.class, Third.class})
public interface ValidationOrder {
}
