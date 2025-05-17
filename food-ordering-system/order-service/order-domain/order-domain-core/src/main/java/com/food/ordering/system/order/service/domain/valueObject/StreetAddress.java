package com.food.ordering.system.order.service.domain.valueObject;

import java.util.Objects;
import java.util.UUID;

public class StreetAddress {
    private final UUID id;
    private final String street;
    private final String postalCode;
    private final String city;

    public StreetAddress(UUID id, String street, String postalCode, String city) {
        this.id = id;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    public UUID getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public static StreetAddressBuilder builder(){
        return new StreetAddressBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreetAddress that = (StreetAddress) o;
        return Objects.equals(street, that.street) && Objects.equals(postalCode, that.postalCode) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, postalCode, city);
    }

    public static final class StreetAddressBuilder {
        private UUID id;
        private String street;
        private String postalCode;
        private String city;

        private StreetAddressBuilder() {
        }

        public static StreetAddressBuilder aStreetAddress() {
            return new StreetAddressBuilder();
        }

        public StreetAddressBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public StreetAddressBuilder street(String street) {
            this.street = street;
            return this;
        }

        public StreetAddressBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public StreetAddressBuilder city(String city) {
            this.city = city;
            return this;
        }

        public StreetAddress build() {
            return new StreetAddress(id, street, postalCode, city);
        }
    }
}
