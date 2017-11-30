/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UtilitiesTS;

/**
 *
 * @author André Dionisio Rocha
 */
public class Constants {
    
    //Types
    public static final String TYPE_RA_SKILL = "RA_Skill";
    public static final String TYPE_RA_LOCAL = "RA_Loc";
    public static final String TYPE_CLA_SKILL = "CLA_Skill";
    //public static final String TYPE_RA = "TYPE_RA";
    public static final String TYPE_PRODUCT = "Product";
    
    //DF Services
    public static final String DFSERVICE_TRANSPORT = "dfservice_transport";
    
    //Ontologies
    public static final String ONTOLOGY_TRANSPORT = "ont_tra_req";
    public static final String ONTOLOGY_START_SIMULATION = "ont_start_sim";
    public static final String ONTOLOGY_KILL_PRODUCT = "ont_kill_prod";
    public static final String ONTOLOGY_NEW_PRODUCT = "ont_new_prod";
    
    ///////////////////NOVAS////////////////
    public static final String ONTOLOGY_PRODUCT_CONTRACT = "ont_prod_c";
    public static final String ONTOLOGY_PRODUCT_REQUEST = "ont_prod_r";
    public static final String ONTOLOGY_EXECUTE_PA = "ont_execute_pa";
    public static final String ONTOLOGY_EXECUTE_CLA = "ont_execute_cla";
    public static final String ONTOLOGY_SKILL_CLA = "ont_skill_cla";
    ////////////////////////////////////////
    
    //Tokens
    public static final String TOKEN = " #";
    public static final String TOKEN_PRODUCT_SKILLS = ";";
    
    //Timers
    public static final long TIMER_LAUNCH_PROD = 1000;                          //ms
    public static long TIMER_CHECK_NEW_PROD_MSG = 250;                          //ms

}
