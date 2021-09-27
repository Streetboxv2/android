package com.zeepos.domain.repository

import com.zeepos.models.master.Tax
import com.zeepos.models.transaction.TaxSales

interface TaxRepo {
    fun insertUpdate(tax: TaxSales)
}