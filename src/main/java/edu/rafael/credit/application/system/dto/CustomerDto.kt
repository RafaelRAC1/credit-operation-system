package edu.rafael.credit.application.system.dto

import edu.rafael.credit.application.system.entity.Address
import edu.rafael.credit.application.system.entity.Customer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto (
    @field:NotEmpty(message = "Name field can't be empty") val firstName: String,
    @field:NotEmpty(message = "Last Name field can't be empty") val lastName: String,
    @field:CPF(message = "Invalid CPF") val cpf: String,
    @field:NotNull(message = "Income field can't be empty") val income: BigDecimal,
    @field:Email(message = "Invalid email") val email: String,
    @field:NotEmpty(message = "Password field can't be empty") val password: String,
    @field:NotEmpty(message = "Zip Code field can't be empty") val zipCode: String,
    @field:NotEmpty(message = "Street field can't be empty") val street: String
) {
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(
            zipCode = this.zipCode,
            street = this.street
        )
    )
}
