package edu.rafael.credit.application.system.repository

import edu.rafael.credit.application.system.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository //optional
interface CustomerRepository: JpaRepository<Customer, Long> {
}