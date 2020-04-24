package fr.depp.drawme.models;

import javax.annotation.Nullable;

public interface OnCustomEventListener<T> {

    void onSuccess(@Nullable T success);

    void onFailure(@Nullable T error);
}
