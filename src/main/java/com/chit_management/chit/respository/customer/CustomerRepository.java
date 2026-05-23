package com.chit_management.chit.respository.customer;

import com.chit_management.chit.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByUuid(String uuid);         // ✅ UUID lookup

    boolean existsByPhone(String phone);
    boolean existsByPhoneAndUuidNot(String phone,
                                    String uuid);        // ✅ for update check

    List<Customer> findByActiveTrue();

    // ✅ Server-side pagination + search
    @Query("SELECT c FROM Customer c WHERE c.active = true AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
            "OR c.phone LIKE CONCAT('%',:keyword,'%'))")
    Page<Customer> searchByKeyword(@Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT c FROM Customer c")
    Page<Customer> findAllCustomer(Pageable pageable);
}