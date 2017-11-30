/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport.Libs;

import Transport.Conveyor;
import Transport.Diverter;
import Transport.RulesInterface;
import Transport.TransportAgent;

/**
 *
 * @author André Dionisio Rocha
 */
public class Layout2Rules implements RulesInterface {

    @Override
    public String routeItem(String dispatchingEntity, String nextDestination) {
        switch (dispatchingEntity) {

            //Source
            case "So":
                if (!nextDestination.equals("")) {
                    return "C_1";
                } else {
                    return null;
                }

            //Diverters
            case "D_1":
                switch (nextDestination) {
                    case ("D_7"):
                        return "C_10";
                    default:
                        return "C_2";
                }
            case "D_2":
                switch (nextDestination) {
                    case ("D_8"):
                        return "C_3";
                    default:
                        return "C_9";
                }
            case "D_3":
                switch (nextDestination) {
                    case ("D_8"):
                        return "C_12";
                    default:
                        return "C_4";
                }
            case "D_4":
                return "C_5";
            case "D_5":
                switch (nextDestination) {
                    case ("D_9"):
                        return "C_14";
                    default:
                        return "C_6";
                }
            case "D_6":
                switch (nextDestination) {
                    case ("Si"):
                        return "C_7";
                    default:
                        return "C_8";
                }
            case "D_7":
                return "C_11";
            case "D_8":
                return "C_13";
            case "D_9":
                return "C_15";

            //Conveyors
            case "C_1":
                return "D_1";
            case "C_2":
                return "D_2";
            case "C_3":
                return "D_3";
            case "C_4":
                return "D_4";
            case "C_5":
                return "D_5";
            case "C_6":
                return "D_6";
            case "C_7":
                return "Si";
            case "C_8":
                return "D_1";
            case "C_9":
                return "D_5";
            case "C_10":
                return "D_7";
            case "C_11":
                return "D_2";
            case "C_12":
                return "D_8";
            case "C_13":
                return "D_4";
            case "C_14":
                return "D_9";
            case "C_15":
                return "D_6";
        }
        return null;
    }

    @Override
    public void initRules(TransportAgent myTA) {
        //Add all Diverters and Conveyors
        //Source
        myTA.addNewDiverter(new Diverter("So"));
        //Sink
        myTA.addNewDiverter(new Diverter("Si"));
        //Diverters
        myTA.addNewDiverter(new Diverter("D_1"));
        myTA.addNewDiverter(new Diverter("D_2"));
        myTA.addNewDiverter(new Diverter("D_3"));
        myTA.addNewDiverter(new Diverter("D_4"));
        myTA.addNewDiverter(new Diverter("D_5"));
        myTA.addNewDiverter(new Diverter("D_6"));
        myTA.addNewDiverter(new Diverter("D_7"));
        myTA.addNewDiverter(new Diverter("D_8"));
        myTA.addNewDiverter(new Diverter("D_9"));
        //Conveyors
        myTA.addNewConveyor(new Conveyor("C_1", 3));
        myTA.addNewConveyor(new Conveyor("C_2", 5));
        myTA.addNewConveyor(new Conveyor("C_3", 5));
        myTA.addNewConveyor(new Conveyor("C_4", 3));
        myTA.addNewConveyor(new Conveyor("C_5", 5));
        myTA.addNewConveyor(new Conveyor("C_6", 5));
        myTA.addNewConveyor(new Conveyor("C_7", 3));
        myTA.addNewConveyor(new Conveyor("C_8", 3));
        myTA.addNewConveyor(new Conveyor("C_9", 3));
        myTA.addNewConveyor(new Conveyor("C_10", 3));
        myTA.addNewConveyor(new Conveyor("C_11", 3));
        myTA.addNewConveyor(new Conveyor("C_12", 2));
        myTA.addNewConveyor(new Conveyor("C_13", 2));
        myTA.addNewConveyor(new Conveyor("C_14", 3));
        myTA.addNewConveyor(new Conveyor("C_15", 3));

    }
}
