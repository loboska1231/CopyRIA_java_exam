package org.copyria.orderapp.enums;

public enum Currency {
    USD("USD"), EUR("EUR"), UAH("UAH");
    private String value;
    Currency(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static Currency fromValue(String value) {
        for (Currency currency : Currency.values()) {
            if (currency.value.equals(value)) {
                return currency;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid currency value");
    }
}
