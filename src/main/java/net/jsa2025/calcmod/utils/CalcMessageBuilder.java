package net.jsa2025.calcmod.utils;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

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

    MutableText messageText = Text.literal("");

    public CalcMessageBuilder() {
        this.messageType = MessageType.NONE;
    }

    public CalcMessageBuilder(MessageType type, String[] inputs, String[] results) {
        try {
            if ((type.inputsLength != inputs.length) || (type.resultsLength != results.length)) {
                System.err.println("CalcMessageBuilder: Mismatch between MessageType spec and provided input/result lengths for type: " + type.name());
                this.messageType = MessageType.NONE;
                return;
            }
            this.messageType = type;
            addFromArray(type.equation, inputs, results);
        } catch (Exception e) { 
            System.err.println("CalcMessageBuilder: Error during construction with MessageType: " + e.getMessage());
            this.messageType = MessageType.NONE; 
        }
    }

    public CalcMessageBuilder(String helpMessage) {
        this.messageType = MessageType.HELP;
        this.helpMessage = helpMessage;
    }

    public CalcMessageBuilder addString(String text) {
        this.messageText.append(text);
        return this;
    }

    /**
     * Appends a pre-styled Text component to the message.
     * @param text The Text component to append.
     * @return This CalcMessageBuilder instance for chaining.
     */
    public CalcMessageBuilder appendText(Text text) {
        this.messageText.append(text);
        return this;
    }

    public CalcMessageBuilder addInput(String text) {
        this.messageText.append("§b" + text + "§f");
        return this;
    }
    public CalcMessageBuilder addResult(String text) {
        String cleanText = text.replaceAll("§[0-9a-fk-or]", "");
        this.messageText.append(Text.literal("§a" + text + "§f")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, cleanText))));
        return this;
    }

    public CalcMessageBuilder addFromArray(String[] template, String[] inputs, String[] results) {
        int resultsAdded = 0;
        int inputsAdded = 0;
        for (String eqnPart : template) {
            if (Objects.equals(eqnPart, "input")) {
                if (inputsAdded < inputs.length) {
                    addInput(inputs[inputsAdded]);
                    inputsAdded++;
                } else {
                     System.err.println("CalcMessageBuilder: Not enough inputs provided for template.");
                }
            } else if (Objects.equals(eqnPart, "result")) {
                if (resultsAdded < results.length) {
                    addResult(results[resultsAdded]);
                    resultsAdded++;
                } else {
                    System.err.println("CalcMessageBuilder: Not enough results provided for template.");
                }
            } else {
                addString(eqnPart);
            }
        }
        return this;
    }
    
    /**
    * Concatenates another CalcMessageBuilder's text to this one.
    * Styles and click events from the other builder's components are preserved.
    * @param other The other CalcMessageBuilder instance.
    * @return This CalcMessageBuilder instance for chaining.
    */
    public CalcMessageBuilder concat(CalcMessageBuilder other) {
        if (other != null && other.messageText != null) {
            this.messageText.append(other.messageText);
        }
        return this;
    }


    public Text generateStyledText() {
        if (this.messageType == MessageType.HELP && this.helpMessage != null && this.messageText.getString().isEmpty()) {
            return Text.literal(this.helpMessage);
        }
        
        MutableText finalMessage = Text.empty().append(this.messageText); 
        
        String currentMessageContent = this.messageText.getString();
        String cleanContentForCopy = currentMessageContent.replaceAll("§[0-9a-fk-or]", "");
        
        finalMessage.append(" ");
        finalMessage.append(Text.literal("§3[Click to Copy]§f")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, cleanContentForCopy))));
        
        return finalMessage;
    }
}
