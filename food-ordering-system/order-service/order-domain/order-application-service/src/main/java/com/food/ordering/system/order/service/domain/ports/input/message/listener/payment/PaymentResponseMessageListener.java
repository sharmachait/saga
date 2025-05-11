package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {
    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentCancelled(PaymentResponse paymentResponse);
    /*
    can be used in case payment fails or as part of the payment cancel request
     as part of the saga rollback operation
     this is like the command handler for the event that will be received over kafka
    */
}
