package utils;

import ru.multa.entia.conversion.api.confirmation.Confirmation;
import ru.multa.entia.conversion.api.message.Message;
import ru.multa.entia.fakers.impl.Faker;

public class FakerUtil {
    public static Message randomMessage(){
        return new TestMessage(
                Faker.uuid_().random(),
                Faker.uuid_().random(),
                new TestAddress(Faker.str_().random()),
                new TestAddress(Faker.str_().random()),
                // TODO: 18.10.2023 use faker
                false,
                new TestContent(new TestType(Faker.str_().random()), Faker.str_().random())
        );
    }

    public static Confirmation confirmation(final Message message){
        return new TestConfirmation(
                message.id(),
                message.conversation(),
                message.to(),
                message.to(),
                "",
                new Object[0]
        );
    }
}
