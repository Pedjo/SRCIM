/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

/**
 *
 * @author André Dionisio Rocha
 */
public class Diverter {

    String ID;
    Item myItem;

    public Diverter(String ID) {
        this.ID = ID;
    }

    public Item getMyItem() {
        return myItem;
    }
    
    public void setPosition(int pos){
        this.myItem.position = pos;
    }
    
    public int getPosition(){
        return this.myItem.position;
    }

    public String getID() {
        return ID;
    }

    public void setMyItem(Item myItem) {
        this.myItem = myItem;
    }
    
}
