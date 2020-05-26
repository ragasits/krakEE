/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.calc.CandleDTO;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import org.bson.types.ObjectId;

/**
 * JSF bean for one Candle
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "learnBean")
public class LearnBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    MongoEJB mongo;
    @EJB
    LearnEJB learn;

    /**
     * Get all Learn
     *
     * @return
     */
    public List<LearnDTO> getLearnList() {
        return learn.get();
    }
}
