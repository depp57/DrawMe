package fr.depp.drawme.models;

import javax.annotation.Nullable;

public interface OnCustomEventListener<T> {

    public void onSuccess(@Nullable T success);

    public void onFailure(@Nullable T error);
}
