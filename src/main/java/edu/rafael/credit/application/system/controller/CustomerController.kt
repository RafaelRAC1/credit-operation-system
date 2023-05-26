package edu.rafael.credit.application.system.controller

import edu.rafael.credit.application.system.dto.CustomerDto
import edu.rafael.credit.application.system.dto.CustomerUpdateDto
import edu.rafael.credit.application.system.dto.CustomerView
import edu.rafael.credit.application.system.entity.Customer
import edu.rafael.credit.application.system.service.impl.CustomerService
import jakarta.validation.Valid
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController //
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<CustomerView> {
        val savedCustomer = this.customerService.save(customerDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerView(savedCustomer))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customer))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: Long) {
        this.customerService.delete(id)
    }

    @PatchMapping
    fun updateCustomer(@RequestParam(value = "customerId") customerId: Long,
                       @RequestBody @Valid customerUpdateDto: CustomerUpdateDto): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(customerId)
        val toUpdateCustomer: Customer = customerUpdateDto.toEntity(customer)
        val updatedCustomer: Customer = this.customerService.save(toUpdateCustomer)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(updatedCustomer))
    }
}