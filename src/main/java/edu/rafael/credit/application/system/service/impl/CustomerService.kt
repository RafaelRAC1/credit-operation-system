package edu.rafael.credit.application.system.service.impl
import edu.rafael.credit.application.system.entity.Customer
import edu.rafael.credit.application.system.exception.BusinessException
import edu.rafael.credit.application.system.repository.CustomerRepository
import  edu.rafael.credit.application.system.service.CustomerService
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): CustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow {
            throw BusinessException("Id $id not found")
        }

    override fun delete(id: Long) {
        val customer: Customer = this.findById(id)
        this.customerRepository.delete(customer)
    }

}