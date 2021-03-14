package com.github.simkuenzi.jaz;

@SuppressWarnings("unused")
public interface SelectItem<T> {
    String getText();
    T getValue();
    boolean isSelected();
}
