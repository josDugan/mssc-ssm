package guru.springframework.services;

import guru.springframework.domain.Payment;
import guru.springframework.domain.PaymentEvent;
import guru.springframework.domain.PaymentState;
import guru.springframework.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository repository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return repository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);

        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);

        return sm;
    }

    @Override
    @Deprecated // not needed
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTH_DECLINED);

        return sm;
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = repository.getOne(paymentId);

        StateMachine<PaymentState,PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(accessor -> {
                    accessor.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    accessor.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
                });

        sm.start();

        return sm;
    }

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();

        sm.sendEvent(msg);
    }
}
