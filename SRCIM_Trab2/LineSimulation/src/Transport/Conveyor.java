/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import java.util.LinkedList;

/**
 *
 * @author André Dionisio Rocha
 */
public class Conveyor {
    
    String ID;
    int dimension;
    LinkedList<Item> inventory;

    public Conveyor(String ID, int dimension) {
        this.ID = ID;
        this.dimension = dimension;
        this.inventory = new LinkedList<>();
    }

    public LinkedList<Item> getInventory() {
        return inventory;
    }

    public String getID() {
        return ID;
    }
    
}
