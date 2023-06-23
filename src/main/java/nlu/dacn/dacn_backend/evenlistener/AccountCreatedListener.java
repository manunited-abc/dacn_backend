package nlu.dacn.dacn_backend.evenlistener;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.entity.Account;
import nlu.dacn.dacn_backend.entity.Cart;
import nlu.dacn.dacn_backend.repository.CartRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountCreatedListener {
    private final CartRepository cartRepository;

    @EventListener
    public void handleAccountCreateEvent(AccountCreatedEvent event) {
        Account account = event.getAccount();
        Cart cart = new Cart();
        cart.setAccount(account);
        cartRepository.save(cart);
    }
}
