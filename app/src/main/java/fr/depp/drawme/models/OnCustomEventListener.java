package fr.depp.drawme.models;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface OnCustomEventListener<T> {

    void onSuccess(@Nullable T success);

    void onFailure(@Nonnull T error);
}
