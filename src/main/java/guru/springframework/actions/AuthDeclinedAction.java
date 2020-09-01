package guru.springframework.actions;

import guru.springframework.domain.PaymentEvent;
import guru.springframework.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthDeclinedAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> stateContext) {
        log.info("Payment auth declined");
    }
}
