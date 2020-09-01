package guru.springframework.actions;

import guru.springframework.domain.PaymentEvent;
import guru.springframework.domain.PaymentState;
import guru.springframework.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class AuthorizeAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.info("Authorize was called!!!");

        if (new Random().nextInt(10) < 8) {
            log.info("Authorize approved");

            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                    .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                    .build());
        } else {
            log.info("Authorize declined");

            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                    .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                    .build());
        }
    }
}
