package bj.inforepair.config;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component("formatUtils")
public class FormatUtils {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String fcfa(BigDecimal montant) {
        if (montant == null) {
            return "0 FCFA";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRENCH);
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,##0", symbols);
        format.setGroupingUsed(true);
        return format.format(montant.setScale(0, RoundingMode.HALF_UP)) + " FCFA";
    }

    public String date(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "-";
    }

    public String dateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FMT) : "-";
    }
}
