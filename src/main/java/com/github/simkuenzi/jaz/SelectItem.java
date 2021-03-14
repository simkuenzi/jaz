package com.github.simkuenzi.jaz;

public interface SelectItem<T> {
    String getText();
    T getValue();
    boolean isSelected();
}
