package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class PaymentService {

    private PaymentRepository paymentRepository;
    private DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie malejąco
    zrobione
     */
    List<Payment> findPaymentsSortedByDateDesc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności dla aktualnego miesiąca
    zrobione
     */
    List<Payment> findPaymentsForCurrentMonth() {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().isAfter(dateTimeProvider.zonedDateTimeNow().minusDays(10)))
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności dla wskazanego miesiąca
    zrobione
     */
    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getMonth().equals(yearMonth.getMonth()))
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności dla ostatnich X dzni
    zrobione
     */
    List<Payment> findPaymentsForGivenLastDays(int days) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().isAfter(dateTimeProvider.zonedDateTimeNow().minusDays(days)))
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności z jednym elementem
    zrobione
     */
    Set<Payment> findPaymentsWithOnePaymentItem() {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentItems().size() <= 1)
                .collect(Collectors.toSet());

    }

    /*
    Znajdź i zwróć nazwy produktów sprzedanych w aktualnym miesiącu
    zrobione
     */
    Set<String> findProductsSoldInCurrentMonth() {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().isAfter(dateTimeProvider.zonedDateTimeNow().minusDays(10)))
                .flatMap(payment -> payment.getPaymentItems().stream().map(PaymentItem::getName))
                .collect(Collectors.toSet());
    }

    /*
    Policz i zwróć sumę sprzedaży dla wskazanego miesiąca
     */
    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getMonth().equals(yearMonth.getMonth()))
                .flatMap(payment -> payment.getPaymentItems().stream())
                .map(PaymentItem::getFinalPrice)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

    }

    /*
    Policz i zwróć sumę przeyznanaych rabatów dla wskazanego miesiąca
    zrobione
     */
    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getMonth().equals(yearMonth.getMonth()))
                .flatMap(payment -> payment.getPaymentItems().stream())
                .map(paymentItem -> paymentItem.getRegularPrice().subtract(paymentItem.getFinalPrice()))
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    /*
    Znajdź i zwróć płatności dla użytkownika z podanym mailem
    zrobione
     */
    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getUser().getEmail().equals(userEmail))
                .flatMap(payment -> payment.getPaymentItems().stream())
                .collect(Collectors.toList());

    }

    /*
    Znajdź i zwróć płatności, których wartość przekracza wskazaną granicę
     */
    Set<Payment> findPaymentsWithValueOver(int value) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentItems().stream()
                        .filter(paymentItem -> paymentItem.getFinalPrice().compareTo(BigDecimal.valueOf(value)) < 0).isParallel())
                .collect(Collectors.toSet());
    }

}
