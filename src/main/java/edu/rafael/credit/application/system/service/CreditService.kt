package edu.rafael.credit.application.system.service

import edu.rafael.credit.application.system.entity.Credit
import java.util.*

interface CreditService {
    fun save(credit: Credit): Credit
    fun findAllByCustomer(customerId: Long): List<Credit>
    fun findbyCreditCode(customerId: Long, creditCode: UUID): Credit
}