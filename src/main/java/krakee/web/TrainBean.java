/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.train.TrainDTO;
import krakee.train.TrainEJB;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "trainBean")
public class TrainBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<TrainDTO> trainList;

    @EJB
    TrainEJB trainEjb;
    
    public void onTrainQuery(){
        this.trainList = trainEjb.get();
    }

    public List<TrainDTO> getTrainList() {
        return trainList;
    }
    


}
