package com.example.new_final_project.service;

import com.example.new_final_project.dto.AddressForm;
import com.example.new_final_project.model.Address;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.Repository.AddressRepository;
import com.example.new_final_project.Repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressService(AddressRepository addressRepository,
                          CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Address> findByCustomer(Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    // Only returns the address if it belongs to this customer
    @Transactional(readOnly = true)
    public Address getOwned(Long customerId, Long addressId) {
        return addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new NotFoundException("Address not found"));
    }

    @Transactional(readOnly = true)
    public AddressForm toForm(Address address) {
        AddressForm form = new AddressForm();
        form.setCity(address.getCity());
        form.setStreet(address.getStreet());
        form.setPostalCode(address.getPostalCode());
        return form;
    }

    @Transactional
    public Address create(Long customerId, AddressForm form) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        Address address = new Address(form.getCity().trim(), form.getStreet().trim(),
                form.getPostalCode().trim(), customer);
        return addressRepository.save(address);
    }

    @Transactional
    public Address update(Long customerId, Long addressId, AddressForm form) {
        Address address = getOwned(customerId, addressId);
        address.setCity(form.getCity().trim());
        address.setStreet(form.getStreet().trim());
        address.setPostalCode(form.getPostalCode().trim());
        return addressRepository.save(address);
    }

    @Transactional
    public void delete(Long customerId, Long addressId) {
        addressRepository.delete(getOwned(customerId, addressId));
    }
}