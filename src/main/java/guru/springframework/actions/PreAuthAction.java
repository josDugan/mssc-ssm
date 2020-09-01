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
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.info("PreAuth was called!!!");

        if (new Random().nextInt(10) < 8) {
            log.info("PreAuth Approved");

            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                    .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                    .build());
        } else {
            log.info("PreAuth Declined! No Credit!!!");

            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                    .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                    .build());

        }
    }
}
