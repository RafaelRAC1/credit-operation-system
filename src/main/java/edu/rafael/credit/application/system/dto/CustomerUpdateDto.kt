package edu.rafael.credit.application.system.dto

import edu.rafael.credit.application.system.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto (
    @field:NotEmpty(message = "First name field can't be empty") val firstName: String,
    @field:NotEmpty(message = "Last name field can't be empty") val lastName: String,
    @field:NotNull(message = "Income filed can't be empty") val income: BigDecimal,
    @field:NotEmpty(message = "Zip Code field can't be empty") val zipCode: String,
    @field:NotEmpty(message = "Street field can't be empty") val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }

}
