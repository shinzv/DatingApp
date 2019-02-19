package eulberg.datingapp;

public class Message {

    private String sender;
    private String receiver;
    private String message;

    /**
     * Konstruktor: initialisiert die Attribute der Klasse
     * @param sender Absender
     * @param receiver Adressat
     * @param message Nachricht
     */
    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }


    /**
     * Dieser leere Konstruktor wird für Firebase benötigt
     */
    public Message(){}

    /**
     * sondierende Methode
     * @return sender: gibt den Sender zurück.
     */
    public String getSender() {
        return sender;
    }

    /**
     * verändernde Methode
     * @param sender neue Sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * sondierende Methode
     * @return receiver: gibt den Adressaten zurück.
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * verändernde Methode
     * @param receiver neue Adressat
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * sondierende Methode
     * @return message: gibt die Nachricht zurück.
     */
    public String getMessage() {
        return message;
    }

    /**
     * verändernde Methode
     * @param message neue Nachricht.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
