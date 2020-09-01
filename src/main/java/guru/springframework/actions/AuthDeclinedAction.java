package guru.springframework.actions;

import guru.springframework.domain.PaymentEvent;
import guru.springframework.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthDeclinedAction {

    public Action<PaymentState, PaymentEvent> execute() {
        return context -> {
            log.info("Payment auth declined");
        };
    }
}
