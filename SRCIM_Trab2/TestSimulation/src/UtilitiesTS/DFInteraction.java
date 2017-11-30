/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UtilitiesTS;


import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.LinkedList;
/**
 *
 * @author André Dionisio Rocha
 */
public class DFInteraction {

    //Regista o serviço com o nome name e do tipo type relativo ao myAgent
    public static void RegisterInDF(Agent myAgent, String name, String type) throws FIPAException {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(name);
        dfd.addServices(sd);

        DFService.register(myAgent, dfd);

    }
    
    
//    public static void RegisterInDF_RA(Agent myAgent, String type, LinkedList<String> mySkills) throws FIPAException {
//
//        DFAgentDescription dfd = new DFAgentDescription();
//        dfd.setName(myAgent.getAID());
//        for (String mySkill : mySkills) { 
//            ServiceDescription sd = new ServiceDescription();
//            sd.setType(type);
//            sd.setName(mySkill);
//            dfd.addServices(sd);
//        } 
//        DFService.register(myAgent, dfd);
//    }
    
    public static void RegisterInDF_RA(Agent myAgent, String type_skill, String type_loc, LinkedList<String> mySkills, String myLocation) throws FIPAException {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());
        ServiceDescription sd;
        for (String mySkill : mySkills) { 
            sd = new ServiceDescription();
            sd.setType(type_skill);
            sd.setName(mySkill);
            dfd.addServices(sd);
        } 
        sd = new ServiceDescription();
        sd.setType(type_loc);
        sd.setName(myLocation);
        dfd.addServices(sd);
        DFService.register(myAgent, dfd);
    }

    //Procura no DF por serviços do tipo type
    //Retorno: Vector com os registos encontrados
    
    //UtilitiesTS.Constants.TYPE_RA_SKILL, mySkills.getFirst(), this
    public static DFAgentDescription[] SearchInDF(String type,String name, Agent myAgent) throws FIPAException {

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType(type);
        dfd.addServices(sd);

        DFAgentDescription[] resultado = DFService.search(myAgent, dfd);

        return resultado;
    }
    
      //Procura no DF por serviços do tipo type
    //Retorno: Vector com os registos encontrados
    public static DFAgentDescription[] SearchInDFByType(String type, Agent myAgent) throws FIPAException {

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        dfd.addServices(sd);

        DFAgentDescription[] resultado = DFService.search(myAgent, dfd);

        return resultado;
    }
    
    //Procura no DF por serviços do tipo name
    //Retorno: Vector com os registos encontrados
    public static DFAgentDescription[] SearchInDFByName(String name, Agent myAgent) throws FIPAException {

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        dfd.addServices(sd);

        DFAgentDescription[] resultado = DFService.search(myAgent, dfd);

        return resultado;
    }
    
    
}
    
