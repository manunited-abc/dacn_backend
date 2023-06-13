package nlu.dacn.dacn_backend.evenlistener;

import lombok.Getter;
import nlu.dacn.dacn_backend.entity.Account;

@Getter
public class AccountCreatedEvent {
    private Account account;

    public AccountCreatedEvent(Account newAccount) {
        this.account = newAccount;
    }
}
