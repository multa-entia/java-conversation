package ru.multa.entia.conversion.impl.confirmation;

import ru.multa.entia.conversion.api.confirmation.Confirmation;

record DefaultConfirmation(String code, Object... args) implements Confirmation {}
