package edu.rafael.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import edu.rafael.credit.application.system.dto.CustomerDto
import edu.rafael.credit.application.system.dto.CustomerUpdateDto
import edu.rafael.credit.application.system.entity.Address
import edu.rafael.credit.application.system.entity.Customer
import edu.rafael.credit.application.system.repository.CustomerRepository
import jakarta.validation.Valid
import org.aspectj.lang.annotation.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.awt.PageAttributes
import java.math.BigDecimal
import java.util.*
import javax.print.attribute.standard.Media

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`(){
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Rafa"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Correa"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("17479755082"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`(){
        //given
        customerRepository.save(builderCustomerDto().toEntity())
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("409"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with firstName empty and return 400 status`(){
        //given
        val customerDto: CustomerDto = builderCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .content(valueAsString)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Rafa"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Correa"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("17479755082"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find costumer when with invalid id and return 400 status`(){
        //given
        val invalidId: Long = 2L
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class edu.rafael.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id and return 204 status`(){
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print()
            )
    }

    @Test
    fun `should not delete customer by id and return 400 status`(){
        //given
        val invalidId: Long = Random().nextLong()

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${invalidId}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class edu.rafael.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update customer and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("17479755082"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("RafaUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("CorreaUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("2000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("54321"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Random Avenue Updated"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update customer with invalid id and return 400 status`() {
        //given
        val randomFakeId: Long = Random().nextLong()
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=$randomFakeId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class edu.rafael.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun builderCustomerDto(
        firstName: String = "Rafa",
        lastName: String = "Correa",
        cpf: String = "17479755082",
        email: String = "rafa@email.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "12345",
        street: String = "Random Avenue"
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun builderCustomerUpdateDto(
        firstName: String = "RafaUpdate",
        lastName: String = "CorreaUpdate",
        income: BigDecimal = BigDecimal.valueOf(2000.0),
        zipCode: String = "54321",
        street: String = "Random Avenue Updated"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )
}