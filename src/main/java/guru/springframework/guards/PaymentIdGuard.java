package guru.springframework.guards;

import guru.springframework.domain.PaymentEvent;
import guru.springframework.domain.PaymentState;
import guru.springframework.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {

    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
        log.info("paymentIdGuard check");

        return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
    }
}
