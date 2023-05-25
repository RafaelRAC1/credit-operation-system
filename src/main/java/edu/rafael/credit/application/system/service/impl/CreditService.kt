package edu.rafael.credit.application.system.service.impl

import edu.rafael.credit.application.system.entity.Credit
import edu.rafael.credit.application.system.exception.BusinessException
import edu.rafael.credit.application.system.repository.CreditRepository
import edu.rafael.credit.application.system.service.CreditService
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): CreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> = this.creditRepository.findAllByCustomerId(customerId)


    override fun findbyCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = (this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("Credit code was not found"))
        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Something went wrong! Contact admin")
    }
}