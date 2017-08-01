package fr.ebiz.computerdatabase.ui.web.converter;

import java.beans.PropertyEditorSupport;

public class CaseInsensitiveConverter<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> clazz;

    /**
     * Constructor.
     *
     * @param clazz The class to match insensitively
     */
    public CaseInsensitiveConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        String upper = text.toUpperCase();
        T value = T.valueOf(clazz, upper);
        setValue(value);
    }
}