package com.food.ordering.system.domain.valueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private final BigDecimal amount;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    //the fields being final and not having setters makes the objects immutable
    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isGreaterThanZero(){
        // 0.00.compareTo(BigDecimal.ZERO) > 0 returns true
        // equals returns false
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money){
        return this.amount!= null && this.amount.compareTo(money.amount) > 0;
    }

    public Money add(Money money){
        return new Money(
                setScale(
                        this.amount.add(money.amount)
                )
        );
    }

    public Money subtract(Money money){
        return new Money(
                setScale(
                        this.amount.subtract(money.amount)
                )
        );
    }

    public Money multiply(int multiplier){
        return new Money(
                setScale(
                        this.amount.multiply(new BigDecimal(multiplier))
                )
        );
    }

    private BigDecimal setScale(BigDecimal input){
        return input.setScale(2, RoundingMode.HALF_EVEN); // allows only two digits after decimal
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}
