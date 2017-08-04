package fr.ebiz.computerdatabase.ui.web.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public final class LocalDatePropertyEditorSupport extends PropertyEditorSupport {

    private final MessageSource messageSource;

    /**
     * Constructor.
     *
     * @param messageSource The messageSource source
     */
    @Autowired
    public LocalDatePropertyEditorSupport(@Qualifier("messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getAsText() {
        LocalDate value = (LocalDate) getValue();
        if (value != null) {
            return getDateTimeFormatter().format(value);
        }
        return "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.isEmpty()) {
            this.setValue(null);
        } else {
            this.setValue(LocalDate.parse(text, getDateTimeFormatter()));
        }
    }

    /**
     * Get the date fime formatter for the current locale.
     *
     * @return The date time formatter
     */
    private DateTimeFormatter getDateTimeFormatter() {
        Locale locale = LocaleContextHolder.getLocale();
        return DateTimeFormatter.ofPattern(messageSource.getMessage("date.format", null, locale));
    }
}