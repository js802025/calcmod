package net.jsa2025.calcmod.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


import java.util.Objects;

public class CalcMessageBuilder {

    public enum MessageType {
        NONE (new String[]{}, 0, 0),
        BASIC(new String[] {"input", " = ", "result"}, 1, 1),
        HELP(new String[]{}, 0, 0);
        final String[] equation;
        final int inputsLength;
        final int resultsLength;
        MessageType(String[] equation, int inputsLength, int resultsLength) {
            this.equation = equation;
            this.inputsLength = inputsLength;
            this.resultsLength = resultsLength;
        }

    }
    MessageType messageType;
    String helpMessage;

    Component messageText = Component.text("");

    public CalcMessageBuilder() {
        this.messageType = MessageType.NONE;

    }
    public CalcMessageBuilder(MessageType type, String[] inputs, String[] results) {
        try {
            if ((type.inputsLength != inputs.length) || (type.resultsLength != results.length))
                throw new Exception("Hello");
            messageType = type;
            addFromArray(type.equation, inputs, results);
        } catch (Exception ignored) {

        }

    }

    public CalcMessageBuilder(String helpMessage) {
        this.messageType = MessageType.HELP;
        this.helpMessage = helpMessage;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public CalcMessageBuilder addString(String text) {
        messageText = messageText.append(MiniMessage.miniMessage().deserialize(text));
        return this;
    }
    public CalcMessageBuilder addInput(String text) {
        messageText = messageText.append(MiniMessage.miniMessage().deserialize(text)
                .style(Style.style(TextColor.fromHexString("#55FFFF"))));
        return this;
    }
    public CalcMessageBuilder addResult(String text) {
        messageText = messageText.append(Component.text(text)

                .style(Style.style(TextColor.fromHexString("#55FF55"))))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                ;
        return this;
    }

    public CalcMessageBuilder addFromArray(String[] template, String[] inputs, String[] results) {
        int resultsAdded = 0;
        int inputsAdded = 0;
        for (String eqnPart : template) {
            if (Objects.equals(eqnPart, "input")) {
                addInput(inputs[inputsAdded]);
                inputsAdded++;
            } else if (Objects.equals(eqnPart, "result")) {
                addResult(results[resultsAdded]);
                resultsAdded++;
            } else {
                addString(eqnPart);
            }
        }
        return this;
    }

    public CalcMessageBuilder addRunCommand(String text, String command) {
        messageText = messageText.append(MiniMessage.miniMessage().deserialize(text).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        return this;
    }

    public Component generateStyledText() {
        if (Objects.requireNonNull(this.messageType) == MessageType.HELP && helpMessage != null) {
            return MiniMessage.miniMessage().deserialize(helpMessage);
        }
        if (Objects.requireNonNull(this.messageType) != MessageType.HELP) {
            messageText = messageText.append(Component.text(" "));
            messageText = messageText.append(Component.text("[Click to Copy]")
                            .style(Style.style(TextColor.fromHexString("#00AAAA")))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, PlainTextComponentSerializer.plainText().serialize(messageText).replaceAll("§.", "").replaceAll("<aqua>", "").replaceAll("<gray>", "").replaceAll("<white>", ""))));
        }
        return messageText;
    }


}
