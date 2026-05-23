package com.chit_management.chit.service.customer;

import com.chit_management.chit.dto.customer.CustomerDTO;
import com.chit_management.chit.dto.customer.CustomerResponseDTO;
import org.springframework.data.domain.Page;

public interface CustomerService {

    Page<CustomerResponseDTO> getCustomers(String search, int page, int size);
    CustomerResponseDTO getCustomerByUuid(String uuid);
    CustomerResponseDTO saveCustomer(CustomerDTO dto);
    CustomerResponseDTO updateCustomer(String uuid, CustomerDTO dto);
    void deactivateCustomer(String uuid);
    long getTotalCustomers();
    void activateCustomer(String uuid);

}