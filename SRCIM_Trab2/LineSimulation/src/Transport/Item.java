/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import jade.lang.acl.ACLMessage;

/**
 *
 * @author André Dionisio Rocha
 */
public class Item {
    
    String ID;
    int position;
    String nextDestination;
    ACLMessage msgToInform;
    long entryDate;

    public int getPosition() {
        return position;
    }

    public String getID() {
        return ID;
    }

    public void decrementPosition() {
        this.position--;
    }

    public Item(String ID, int position) {
        this.ID = ID;
        this.position = position;
        this.nextDestination = "";
        this.entryDate = System.currentTimeMillis();
    }

    public ACLMessage getMsgToInform() {
        return msgToInform;
    }

    public void setMsgToInform(ACLMessage msgToInform) {
        this.msgToInform = msgToInform;
    }

    public long getEntryDate() {
        return entryDate;
    }  
    
}
