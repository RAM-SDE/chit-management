package com.chit_management.chit.serviceImpl.customer;

import com.chit_management.chit.dto.customer.CustomerDTO;
import com.chit_management.chit.dto.customer.CustomerResponseDTO;
import com.chit_management.chit.entity.customer.Customer;
import com.chit_management.chit.entity.staff.User;
import com.chit_management.chit.respository.customer.CustomerRepository;
import com.chit_management.chit.respository.staff.UserRepository;
import com.chit_management.chit.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository     userRepository;

    // ── List with pagination + search ─────────
    @Override
    public Page<CustomerResponseDTO> getCustomers(String search,
                                                  int page,
                                                  int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<Customer> result = (search != null && !search.isBlank())
                ? customerRepository.searchByKeyword(search, pageable)
                : customerRepository.findAllCustomer(pageable);

        return result.map(this::toResponse);
    }

    // ── Get by UUID ───────────────────────────
    @Override
    public CustomerResponseDTO getCustomerByUuid(String uuid) {
        return toResponse(findByUuid(uuid));
    }

    // ── Save ──────────────────────────────────
    @Override
    @Transactional
    public CustomerResponseDTO saveCustomer(CustomerDTO dto) {
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException(
                    "Phone number already registered: " + dto.getPhone());
        }

        Customer customer = new Customer();
        mapDtoToEntity(dto, customer);
        customer.setCreatedBy(getCurrentUser());

        return toResponse(customerRepository.save(customer));
    }

    // ── Update ────────────────────────────────
    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(String uuid, CustomerDTO dto) {
        Customer existing = findByUuid(uuid);

        // Phone check — exclude current customer
        if (!existing.getPhone().equals(dto.getPhone())
                && customerRepository.existsByPhoneAndUuidNot(
                dto.getPhone(), uuid)) {
            throw new RuntimeException(
                    "Phone number already in use: " + dto.getPhone());
        }

        mapDtoToEntity(dto, existing);
        return toResponse(customerRepository.save(existing));
    }

    // ── Deactivate ────────────────────────────
    @Override
    @Transactional
    public void deactivateCustomer(String uuid) {
        Customer c = findByUuid(uuid);
        c.setActive(false);
        customerRepository.save(c);
    }

    // ── Total count ───────────────────────────
    @Override
    public long getTotalCustomers() {
        return customerRepository.findByActiveTrue().size();
    }

    // ════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════
    private Customer findByUuid(String uuid) {
        return customerRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found: " + uuid));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private void mapDtoToEntity(CustomerDTO dto, Customer entity) {
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setAadharNo(dto.getAadharNo());
    }

    private CustomerResponseDTO toResponse(Customer c) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setUuid(c.getUuid());
        dto.setName(c.getName());
        dto.setPhone(c.getPhone());
        dto.setEmail(c.getEmail());
        dto.setAddress(c.getAddress());
        dto.setAadharNo(c.getAadharNo());
        dto.setActive(c.isActive());
        dto.setCreatedAt(c.getCreatedAt() != null
                ? c.getCreatedAt().toString() : null);
        return dto;
    }

    @Transactional
    public void activateCustomer(String uuid) {
        Customer c = findByUuid(uuid);
        c.setActive(true);
        customerRepository.save(c);
    }
}