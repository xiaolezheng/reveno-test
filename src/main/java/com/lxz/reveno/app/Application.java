package com.lxz.reveno.app;

import com.lxz.reveno.model.Account;
import com.lxz.reveno.model.AccountView;
import lombok.extern.slf4j.Slf4j;
import org.reveno.atp.api.Reveno;
import org.reveno.atp.api.dynamic.DynamicCommand;
import org.reveno.atp.core.Engine;

import java.util.concurrent.TimeUnit;

import static org.reveno.atp.utils.MapUtils.map;

/**
 * Created by xiaolezheng on 16/6/16.
 */
@Slf4j
public class Application {
    public static void main(String[] args) {
        Reveno reveno = new Engine("/tmp/reveno-sample");

        try {
            reveno.domain().viewMapper(Account.class, AccountView.class, (id, e, r) -> new AccountView(id, e.name, e.balance));

            DynamicCommand createAccount = reveno.domain()
                    .transaction("createAccount", (t, c) -> c.repo().store(t.id(), new Account(t.arg(), 0)))
                    .uniqueIdFor(Account.class).command();

            DynamicCommand changeBalance = reveno.domain()
                    .transaction("changeBalance", (t, c) -> c.repo().store(t.longArg(), c.repo().get(Account.class, t.arg()).add(t.intArg("inc"))))
                    .command();


            reveno.startup();

            long accountId = reveno.executeSync(createAccount, map("name", "John"));
            for (int i = 0; i < 10; i++) {
                reveno.execute(changeBalance, map("id", accountId, "inc", 10));
            }

            log.info("accountId: {}", accountId);

            while (true) {
                AccountView result = reveno.query().find(AccountView.class, accountId);
                if (result.balance != 100) {
                    try {
                        TimeUnit.MICROSECONDS.sleep(10);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                } else {
                    log.info("result: {}", result);
                    return;
                }

                log.info("result: {}", result);
            }
        } finally {
            reveno.shutdown();
        }
    }
}
