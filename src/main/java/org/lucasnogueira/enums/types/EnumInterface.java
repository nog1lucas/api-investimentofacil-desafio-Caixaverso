package org.lucasnogueira.enums.types;

import java.io.Serializable;

public interface EnumInterface<T extends Serializable> {
    T getId();
}