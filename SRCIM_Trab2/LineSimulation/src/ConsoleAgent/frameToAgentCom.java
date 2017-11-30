/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConsoleAgent;

import java.util.ArrayList;

/**
 *
 * @author André Dionisio Rocha
 */
interface frameToAgentCom {
    
    /*
     * Arguments -  [0] RulesLib Name
     *              [1] HardwareLib Name
     */
    boolean launchNewSimulationEnvironment(String RulesLib, String HardwareLib);
    
    /*
     * Arguments -  List of Skills
     */
    void launchNewProduct(String myProcess);

    public void launchNewResource(String ResourceID, String ResourceLocation, String ResourceSkills);
    
    public void launchNewCLA(String CLA_ID, String CLAlocation);
    
}
