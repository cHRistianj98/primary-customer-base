package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for {@link AddressRepository}
 */
@DataJpaTest
public class AddressRepositoryTest {
    private static final String CLARK_STREET = "Clark";
    private static final String NY_CITY = "NY City";

    @Autowired
    private AddressRepository addressRepository;

    private Address address;

    @BeforeEach
    public void init() {
        address = addressRepository.save(createAddress(STREET, CITY));
    }

    @Test
    public void whenFindAll_ThenReturnAllAddresses() {
        // when
        final List<Address> addresses = addressRepository.findAll();

        // then
        assertThat(addresses).hasSize(1);
        assertThat(addresses).extracting(Address::getStreet).containsOnly(address.getStreet());
        assertThat(addresses).extracting(Address::getCity).containsOnly(address.getCity());
    }

    @Test
    public void whenExistsByStreetAndCity_ThenReturnTrueIfAddressExist() {
        // when
        final boolean addressExist = addressRepository.existsByStreetAndCity(STREET, CITY);

        // then
        assertThat(addressExist).isTrue();
    }

    @Test
    public void whenExistsByStreetAndCity_ThenFalseTrueIfAddressNotExist() {
        // when
        final boolean addressExist = addressRepository.existsByStreetAndCity(CLARK_STREET, CITY);

        // then
        assertThat(addressExist).isFalse();
    }
    
    @Test
    public void whenSave_thenAddressIsCreated() {
        // when
        final Address createdAddress = addressRepository.save(createAddress(CLARK_STREET, NY_CITY));

        // then
        assertThat(createdAddress).isNotNull();
        assertThat(createdAddress.getStreet()).isEqualTo(CLARK_STREET);
        assertThat(createdAddress.getCity()).isEqualTo(NY_CITY);
    }

    @Test
    public void whenFindById_thenReturnExistingAddress() {
        // when
        final Optional<Address> optionalAddress = addressRepository.findById(address.getId());

        // then
        assertThat(optionalAddress).isNotEmpty();
        assertThat(optionalAddress.get()).extracting(Address::getStreet).isEqualTo(STREET);
        assertThat(optionalAddress.get()).extracting(Address::getCity).isEqualTo(CITY);
        assertThat(optionalAddress.get()).extracting(Address::getId).isEqualTo(address.getId());
        assertThat(optionalAddress.get()).extracting(Address::getCustomer).isNull();
    }

    @Test
    public void whenFindById_thenThrowAddressNotFound() {
        // when
        final Optional<Address> optionalAddress = addressRepository.findById(999);

        // then
        assertThat(optionalAddress).isEmpty();
    }
}
